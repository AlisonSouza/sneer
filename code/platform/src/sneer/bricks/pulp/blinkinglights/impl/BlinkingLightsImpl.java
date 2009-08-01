package sneer.bricks.pulp.blinkinglights.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.clock.timer.Timer;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.ListRegister;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.exceptions.FriendlyException;

class BlinkingLightsImpl implements BlinkingLights {
	
	private final ListRegister<Light> _lights = my(CollectionSignals.class).newListRegister();
	@SuppressWarnings("unused")	private WeakContract _turnOffContract;
	
	@Override
	public Light turnOn(LightType type, String caption, String helpMessage, Throwable t, int timeout) {
		Light result = prepare(type);
		turnOnIfNecessary(result, caption, helpMessage, t, timeout);
		return result;
	}
	
	@Override
	public Light turnOn(LightType type, String caption, String helpMessage, Throwable t) {
		return turnOn(type, caption, helpMessage, t, LightImpl.NEVER);
	}

	@Override
	public Light turnOn(LightType type, String caption, String helpMessage, int timeToLive) {
		return turnOn(type, caption, helpMessage, null, timeToLive);
	}

	@Override
	public Light turnOn(LightType type, String caption, String helpMessage) {
		return turnOn(type, caption, helpMessage, LightImpl.NEVER);
	}
	
	@Override
	public ListSignal<Light> lights() {
		return _lights.output();
	}
	
	@Override
	public void turnOffIfNecessary(Light light) {
		if (!light.isOn()) return;
		
		_lights.remove(light);
		((LightImpl)light).turnOff();
	}
	
	private void turnOffIn(final Light light, int millisFromNow) {
		_turnOffContract = my(Timer.class).wakeUpInAtLeast(millisFromNow, new Runnable() { @Override public void run() {
			turnOffIfNecessary(light);	
		}});
	}

	@Override
	public Light prepare(LightType type) {
		return new LightImpl(type);
	}

	@Override
	public void turnOnIfNecessary(Light light, FriendlyException e) {
		turnOnIfNecessary(light, e, LightImpl.NEVER);
	}
	
	@Override
	public void turnOnIfNecessary(Light light, FriendlyException e, int timeout) {
		turnOnIfNecessary(light, e.getMessage(), e.getHelp(), e, timeout);
	}

	@Override
	public void turnOnIfNecessary(Light light, String caption, String helpMessage) {
		turnOnIfNecessary(light, caption, helpMessage, null, LightImpl.NEVER);
	}
	
	@Override
	public void turnOnIfNecessary(Light light, String caption, Throwable t) {
		turnOnIfNecessary(light, caption, null, t, LightImpl.NEVER);
	}

	@Override
	public void turnOnIfNecessary(Light light, String caption, String helpMessage, Throwable t) {
		turnOnIfNecessary(light, caption, helpMessage, t, LightImpl.NEVER);
	}
	
	@Override
	public void turnOnIfNecessary(Light light_, String caption, String helpMessage, Throwable t, int timeout) {
		if (!(light_ instanceof LightImpl)) throw new IllegalArgumentException();
		LightImpl light = (LightImpl)light_;

		if (light.isOn()) return;
		light._isOn = true;
		light._caption = caption;
		light._error = t;
		light._helpMessage = helpMessage == null ? "If this problem doesn't go away on its own, get an expert sovereign friend to help you. ;)" : helpMessage;
		
		logIfNecessary(t);
		
		_lights.add(light);
		
		if (timeout != LightImpl.NEVER)
			turnOffIn(light, timeout);
	}


	@Override
	public void askForConfirmation(LightType type, String caption, String helpMessage, Consumer<Boolean> confirmationReceiver) {
		LightImpl light = new LightImpl(type, confirmationReceiver);
		light._caption = caption;
		light._helpMessage = helpMessage;
		turnOnIfNecessary(light, caption, helpMessage);
	}
	

	private void logIfNecessary(Throwable throwable) {
		if (throwable == null) return;
		my(Logger.class).log(throwable, "Blinking light turned on.");
	}

}