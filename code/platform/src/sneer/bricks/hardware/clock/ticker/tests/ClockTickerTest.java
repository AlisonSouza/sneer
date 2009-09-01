package sneer.bricks.hardware.clock.ticker.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.clock.ticker.ClockTicker;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.foundation.brickness.testsupport.BrickTest;
import sneer.foundation.lang.Consumer;


public class ClockTickerTest extends BrickTest {

	private final Clock _clock = my(Clock.class);

	{
		my(ClockTicker.class);
	}

	@Test (timeout = 3000)
	public void testTicking() {
		waitForATick();
		waitForATick();
	}

	private void waitForATick() {
		final Latch latch = my(Latches.class).produce();
		@SuppressWarnings("unused")
		WeakContract contract = _clock.time().addReceiver(new Consumer<Long>() { @Override public void consume(Long value) {
			latch.open();
		}});
		latch.waitTillOpen();
	}
	
}
