package sneer.bricks.pulp.reactive.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.SignalUtils;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
import sneer.bricks.pulp.reactive.collections.SetSignal;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.Predicate;

class SignalUtilsImpl implements SignalUtils {

	@Override
	public <T> void waitForValue(Signal<T> signal, final T expected) {
		final StringBuilder seen = new StringBuilder();
		final Latch latch = my(Latches.class).newLatch();
		@SuppressWarnings("unused")
		WeakContract reception = signal.addReceiver(new Consumer<T>() { @Override public void consume(T value) {
			if (equalsWithNulls(expected, value))
				latch.open();
			else
				seen.append(value.toString() + ", ");
		}});
		
		try {
			latch.waitTillOpen();
		} catch (RuntimeException e) {
			throw new IllegalStateException("Expected: " + expected + " Seen: " + seen, e);
		}
	}

	@Override
	public <T> void waitForElement(SetSignal<T> setSignal, final T expected) {
		waitForElement(setSignal, new Predicate<T>() { @Override public boolean evaluate(T value) {
			return equalsWithNulls(expected, value);
		}});		
	}
	
	@Override
	public <T> void waitForElement(SetSignal<T> setSignal, final Predicate<T> predicate) {
		final Latch latch = my(Latches.class).newLatch();
		WeakContract reception = setSignal.addReceiver(new Consumer<CollectionChange<T>>() { @Override public void consume(CollectionChange<T> change) {
			for (T element : change.elementsAdded())
				if (predicate.evaluate(element)) {
					latch.open();
					break;
				}
		}});
		
		latch.waitTillOpen();
		reception.dispose();
	}
	
	static private <T> boolean equalsWithNulls(final T expected, T value) {
		return value == null
			? expected == null
			: value.equals(expected);
	}
	
}

