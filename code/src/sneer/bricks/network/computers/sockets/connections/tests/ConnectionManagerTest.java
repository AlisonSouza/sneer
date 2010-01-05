package sneer.bricks.network.computers.sockets.connections.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.network.computers.sockets.connections.ConnectionManager;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.SignalUtils;
import sneer.bricks.software.folderconfig.tests.BrickTest;

public class ConnectionManagerTest extends BrickTest {

	private final ConnectionManager _subject = my(ConnectionManager.class);

	private final Contacts _contactManager = my(Contacts.class);

	@Ignore
	@Test (timeout = 2000)
	public void connection() {
		final Contact neide = _contactManager.produceContact("Neide");

		Signal<Boolean> isConnected = _subject.connectionFor(neide).isConnected();
		assertFalse(isConnected.currentValue());
		
		//Implement: MOCK THE INCOMING OR OUTGOING CONNECTIONS TO NEIDE. 

		my(SignalUtils.class).waitForValue(isConnected, true);
	}
}
