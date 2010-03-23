package sneer.bricks.network.computers.sockets.connections.originator.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.HashMap;
import java.util.Map;

import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.computers.sockets.connections.originator.SocketOriginator;
import sneer.bricks.pulp.internetaddresskeeper.InternetAddress;
import sneer.bricks.pulp.internetaddresskeeper.InternetAddressKeeper;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
import sneer.foundation.lang.Consumer;

class SocketOriginatorImpl implements SocketOriginator {

	private static final ContactSeals Seals = my(ContactSeals.class);
	
	@SuppressWarnings("unused")
	private final Object _refToAvoidGC;
	private final Map<InternetAddress, OutgoingAttempt> _attemptsByAddress = new HashMap<InternetAddress, OutgoingAttempt>();
	
	
	SocketOriginatorImpl() {
		_refToAvoidGC = my(InternetAddressKeeper.class).addresses().addReceiver(new Consumer<CollectionChange<InternetAddress>>(){ @Override public void consume(CollectionChange<InternetAddress> value) {
			for (InternetAddress address : value.elementsRemoved()) 
				stopAddressing(address);
		
			for (InternetAddress address : value.elementsAdded()) 
				startAddressing(address);
		}});
	}

	
	private void startAddressing(InternetAddress address) {
		if (isMyOwnAddress(address)) return;
		
		OutgoingAttempt attempt = new OutgoingAttempt(address);
		_attemptsByAddress.put(address, attempt);
	}

	
	private void stopAddressing(InternetAddress address) {
		OutgoingAttempt attempt = _attemptsByAddress.remove(address);
		attempt.crash();
	}

	
	private boolean isMyOwnAddress(InternetAddress address) {
		return my(OwnSeal.class).get().equals(Seals.sealGiven(address.contact()).currentValue());
	}
	
}