package sneer.bricks.pulp.blinkinglights.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.SignalUtils;
import sneer.bricks.software.folderconfig.testsupport.BrickTestWithFiles;

public class BlinkingLightsTest extends BrickTestWithFiles {

	private final BlinkingLights _subject = my(BlinkingLights.class);

	private final Clock _clock = my(Clock.class);

	
	@Test
	public void testLights() throws Exception {
		assertLightCount(0, _subject);
		
		Light light = _subject.turnOn(LightType.ERROR, "caption", "some error", new NullPointerException());
		assertTrue(light.isOn());
		assertEquals("caption", light.caption());
		assertNotNull(light.error());
		assertLightCount(1, _subject);
		
		_subject.turnOnIfNecessary(light, "foo", "bar");
		assertLightCount(1, _subject);
		
		_subject.turnOffIfNecessary(light);
		assertFalse(light.isOn());
		assertLightCount(0, _subject);
	}

	private void assertTrue(Signal<Boolean> booleanSignal) {
		assertTrue(booleanSignal.currentValue());
	}

	private void assertFalse(Signal<Boolean> booleanSignal) {
		assertFalse(booleanSignal.currentValue());
	}

	@Test (timeout = 2000)
	public void testTimeout() throws Exception {
		final String message = "some error";
		final String caption = "some caption";
		final NullPointerException exception = new NullPointerException();
		final int timeout = 1000;

		final Light light = _subject.turnOn(LightType.ERROR, caption, message, exception, timeout);
		assertTrue(light.isOn());

		_clock.advanceTime(timeout - 1);
		assertTrue(light.isOn()); //Fix: Make sure all asynchronous dispatching has finished.
		
		_clock.advanceTime(1);
		my(SignalUtils.class).waitForValue(light.isOn(), false);
	}


	private void assertLightCount(int count, BlinkingLights _lights) {
		assertEquals(count, _lights.lights().size().currentValue().intValue());
	}
	

}
