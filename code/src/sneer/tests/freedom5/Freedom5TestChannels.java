package sneer.tests.freedom5;

import org.junit.Test;

import sneer.bricks.network.computers.channels.Channels;
import sneer.tests.SovereignFunctionalTestBase;

public class Freedom5TestChannels extends SovereignFunctionalTestBase {

	@Test// (timeout = 13000)
	public void openControlChannel() {
		if (!Channels.READY_FOR_PRODUCTION) return;
		
		b().keepSendingMessageInControlChannel(a().ownName(), "Hello".getBytes());
		byte[] actual = a().waitForMessageInControlChannel(b().ownName());
		assertArrayEquals("Hello".getBytes(), actual);

	}

}