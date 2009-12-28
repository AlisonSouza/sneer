package spikes.sneer.bricks.snapps.watchme.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.image.BufferedImage;
import java.util.List;

import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.exceptions.Hiccup;
import sneer.bricks.hardware.cpu.lang.contracts.Contract;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.datastructures.cache.Cache;
import sneer.bricks.pulp.datastructures.cache.CacheFactory;
import sneer.bricks.pulp.events.EventNotifier;
import sneer.bricks.pulp.events.EventNotifiers;
import sneer.bricks.pulp.events.EventSource;
import sneer.bricks.pulp.keymanager.Seal;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.exceptions.FriendlyException;
import spikes.sneer.bricks.skin.screenshotter.Screenshotter;
import spikes.sneer.bricks.snapps.watchme.ImageDeltaPacket;
import spikes.sneer.bricks.snapps.watchme.WatchMe;
import spikes.sneer.bricks.snapps.watchme.codec.ImageCodec;
import spikes.sneer.bricks.snapps.watchme.codec.ImageDelta;
import spikes.sneer.bricks.snapps.watchme.codec.ImageCodec.Decoder;
import spikes.sneer.bricks.snapps.watchme.codec.ImageCodec.Encoder;

class WatchMeImpl implements WatchMe {

	private static final int CACHE_CAPACITY = 0; //3000;

	private static final int PERIOD_IN_MILLIS = 1000;

	private final Screenshotter _shotter = my(Screenshotter.class);
	private final ImageCodec _codec = my(ImageCodec.class);
	private final CacheFactory _cacheFactory = my(CacheFactory.class);
	private final TupleSpace _tupleSpace = my(TupleSpace.class);
	private final Threads _threads = my(Threads.class);
	private final BlinkingLights _lights = my(BlinkingLights.class);

	private final Light _light = _lights.prepare(LightType.ERROR);

	private Encoder _encoder;
	private Cache<ImmutableByteArray> _cache;

	private Consumer<ImageDeltaPacket> _consumerToAvoidGc;
	@SuppressWarnings("unused")	private WeakContract _tupleSpaceContract;

	private Contract _stepperContract;


	@Override
	public EventSource<BufferedImage> screenStreamFor(final Seal publisher) {
		if (publisher == null)
			throw new IllegalArgumentException("The publisher argument can't be null.");
		
		EventNotifier<BufferedImage> result = my(EventNotifiers.class).newInstance();
		
		_consumerToAvoidGc = imageDeltaPacketConsumer(publisher, result);
		_tupleSpaceContract = _tupleSpace.addSubscription(ImageDeltaPacket.class, _consumerToAvoidGc);
		
		return result.output();
	}

	private Consumer<ImageDeltaPacket> imageDeltaPacketConsumer(final Seal publisher,	final EventNotifier<BufferedImage> notifier) {
		final Decoder decoder = _codec.createDecoder();
		final Cache<ImmutableByteArray> cache = _cacheFactory.createWithCapacity(CACHE_CAPACITY);
		
		return new Consumer<ImageDeltaPacket>(){@Override public void consume(ImageDeltaPacket delta) {
			if (!delta.publisher().equals(publisher))
				return;
			
			ImmutableByteArray imageData = delta.imageData != null
				? delta.imageData
				: cache.getByHandle(delta.cacheHandle);
			
			if (imageData == null) {
				my(Logger.class).log("Local WatchMe image cache out of sync with peer's cache.");
				return;
			}

			cache.keep(imageData);
			
			if (decoder.applyDelta(new ImageDelta(imageData, delta.x, delta.y)));
				notifier.notifyReceivers(decoder.screen());
		}};
	}

	@Override
	public void startShowingMyScreen() {
		_stepperContract = _threads.startStepping(new Runnable(){ @Override public void run() {
			doPublishShot();
			my(Timer.class).sleepAtLeast(PERIOD_IN_MILLIS);
		}});
	}

	private void doPublishShot() {
		try {
			tryToPublishShot();
			_lights.turnOffIfNecessary(_light);
		} catch (Hiccup ok) {			
		} catch (FriendlyException e) {
			_lights.turnOnIfNecessary(_light, e);
		}
	}

	private void tryToPublishShot() throws Hiccup, FriendlyException {
		BufferedImage shot = _shotter.takeScreenshot();
		
		List<ImageDelta> deltas = encoder().generateDeltas(shot);
		for (ImageDelta delta : deltas)
			publish(delta);
	}

	private void publish(ImageDelta delta) {
		if (cache().contains(delta.imageData))
			publishCacheHit(delta);
		else
			_tupleSpace.publish(new ImageDeltaPacket(delta.x, delta.y, delta.imageData, 0));
		
		cache().keep(delta.imageData);
	}

	private void publishCacheHit(ImageDelta delta) {
		int handle = cache().handleFor(delta.imageData);
		_tupleSpace.publish(new ImageDeltaPacket(delta.x, delta.y, null, handle));
	}

	private Encoder encoder() {
		if (_encoder == null)
			_encoder = _codec.createEncoder();
		return _encoder;
	}

	private Cache<ImmutableByteArray> cache() {
		if (_cache == null)
			_cache = _cacheFactory.createWithCapacity(CACHE_CAPACITY);
		return _cache;
	}

	@Override
	public void stopShowingMyScreen() {
		_stepperContract.dispose();
		_encoder = null;
		_cache = null;
	}

}
