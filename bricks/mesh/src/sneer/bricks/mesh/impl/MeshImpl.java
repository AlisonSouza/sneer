package sneer.bricks.mesh.impl;

import java.util.HashMap;
import java.util.Map;

import sneer.bricks.connection.Connection;
import sneer.bricks.connection.ConnectionManager;
import sneer.bricks.mesh.Mesh;
import sneer.contacts.Contact;
import sneer.contacts.ContactManager;
import sneer.lego.Brick;
import sneer.lego.Injector;
import sneer.lego.Startable;
import wheel.reactive.Signal;
import wheel.reactive.maps.impl.SimpleMapReceiver;

public class MeshImpl implements Mesh, Startable {

	private final Map<Contact, ContactProxy> _proxiesByContact = new HashMap<Contact, ContactProxy>();
	
	@SuppressWarnings("unused")
	private SimpleMapReceiver<Contact, Connection> _connectionReceiverToAvoidGC;

	@Brick
	private ConnectionManager _connectionManager;

	@Brick
	private ContactManager _contactManager;

	@Brick
	private Injector _injector;

	@Override
	public void start() throws Exception {
		_connectionReceiverToAvoidGC = new SimpleMapReceiver<Contact, Connection>(_connectionManager.connections()){

			@Override
			public void elementAdded(Contact contact, Connection connection) {
				System.out.println("New connection with: "+contact.nickname());
				//TODO: start new Thread to read bytes from connection
				throw new wheel.lang.exceptions.NotImplementedYet();
			}

			@Override
			public void elementRemoved(Contact contact, Connection connection) {
				System.out.println("removed connection with: "+contact.nickname());
				throw new wheel.lang.exceptions.NotImplementedYet();
			}

			@Override
			public void elementReplaced(Contact contact, Connection connection) {
				System.out.println("replaced connection with: "+contact.nickname());
				throw new wheel.lang.exceptions.NotImplementedYet();
			}};
	}

	@Override
	public <T> Signal<T> findSignal(String nicknamePath, String signalPath) {
		String[] path = nicknamePath.split("/", 1);
		String head = path[0];
		String tail = path.length > 1
			? path[1]
			: "";
			
		Contact immediateContact = _contactManager.contactGiven(head);
		ContactProxy proxy = produceProxyFor(immediateContact);
		
		return proxy.findSignal(tail, signalPath);
	}

	private ContactProxy produceProxyFor(Contact contact) {
		synchronized (_proxiesByContact) {
			ContactProxy proxy = _proxiesByContact.get(contact);
			if (proxy == null) {
				proxy = new ContactProxy(_injector, contact);
				_proxiesByContact.put(contact, proxy);
			}
			return proxy;
		}
	}
}
