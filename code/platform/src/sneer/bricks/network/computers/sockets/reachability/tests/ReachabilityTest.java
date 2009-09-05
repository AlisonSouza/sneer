package sneer.bricks.network.computers.sockets.reachability.tests;

import static sneer.foundation.environments.Environments.my;

import org.jmock.Expectations;
import org.junit.Test;

import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.io.log.tests.TestThatUsesLogger;
import sneer.bricks.network.computers.sockets.accepter.SocketAccepter;
import sneer.bricks.network.computers.sockets.reachability.ReachabilitySentinel;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.events.EventNotifier;
import sneer.bricks.pulp.events.EventNotifiers;
import sneer.bricks.pulp.network.ByteArraySocket;
import sneer.bricks.pulp.reactive.SignalUtils;
import sneer.foundation.brickness.testsupport.Bind;

public class ReachabilityTest extends TestThatUsesLogger {
	
	@Bind private final SocketAccepter _accepter = mock(SocketAccepter.class);
	private final EventNotifier<ByteArraySocket> _notifier = my(EventNotifiers.class).newInstance();
	
	private final Clock _clock = my(Clock.class);
	
	private final BlinkingLights _lights = my(BlinkingLights.class);
		
	@Test
	public void testBlinkingLightWhenUnreachable() throws Exception {
		checking(new Expectations() {{
			exactly(1).of(_accepter).lastAcceptedSocket();
				will(returnValue(_notifier.output()));
		}});

		startReachabilitySentinel();
		assertEquals(0, _lights.lights().size().currentValue().intValue());
		
		_clock.advanceTime(30*1000);
		my(SignalUtils.class).waitForValue(_lights.lights().size(), 1);
		
		ByteArraySocket socket = mock(ByteArraySocket.class);
		_notifier.notifyReceivers(socket);
		assertEquals(0, _lights.lights().size().currentValue().intValue());
	}

	private void startReachabilitySentinel() {
		my(ReachabilitySentinel.class);
	}
}
