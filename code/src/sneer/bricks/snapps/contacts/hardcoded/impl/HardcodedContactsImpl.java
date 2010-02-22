package sneer.bricks.snapps.contacts.hardcoded.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.codec.Codec;
import sneer.bricks.hardware.cpu.codec.DecodeException;
import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.pulp.internetaddresskeeper.InternetAddressKeeper;
import sneer.bricks.pulp.keymanager.ContactSeals;
import sneer.bricks.pulp.keymanager.Seal;
import sneer.bricks.snapps.contacts.hardcoded.HardcodedContacts;
import sneer.foundation.lang.exceptions.Refusal;

public class HardcodedContactsImpl implements HardcodedContacts {

	
	private final Contacts _contactManager = my(Contacts.class);

	
	HardcodedContactsImpl() throws DecodeException {
		if(!_contactManager.contacts().currentElements().isEmpty()) 
			return;
				
		for (ContactInfo contact : contacts())
			add(contact);
	}
	
	
	private void add(ContactInfo contact) {
		if (my(ContactSeals.class).ownSeal().equals(contact._seal)) return;
		addAddresses(contact);
		addSeal(contact);
	}

	
	private void addSeal(ContactInfo contact) {
		if (contact._seal == null) return;
		try {
			my(ContactSeals.class).put(contact._nick, contact._seal);
		} catch (Refusal e) {
			throw new IllegalStateException(e); // Fix Handle this exception.
		}
	}

	
	private void addAddresses(ContactInfo contact) {
		String nick = contact._nick;
		addAddress(nick, contact._host, contact._port);
		
		for (String host : alternativeHostsFor(nick))
			addAddress(nick, host, contact._port);
	}

	
	private String[] alternativeHostsFor(String nick) {
		if (nick.equals("Kalecser")) return new String[]{"10.42.11.165"};
		if (nick.equals("Klaus")) return new String[]{"200.169.90.89", "10.42.11.19"};
		return new String[]{};
	}

	
	private void addAddress(String nick, String host, int port) {
		Contact contact = _contactManager.produceContact(nick);
		my(InternetAddressKeeper.class).add(contact, host, port);
	}

	
	private ContactInfo[] contacts() throws DecodeException {
		return new ContactInfo[] {
			new ContactInfo("Agnaldo4j", "agnaldo4j.selfip.com", 5923),
			new ContactInfo("Bamboo","rbo.selfip.net",5923, sealFromString("5107729C83C3862A1DE5DED0F00889DA67CBF152443CF4D0BCCBAEB49475A40AF88DCEBFD1B36F1D24F983BE05943B09C50903CC1F1BBABED8086A00444A4624CEAE2AE0FE48942C38708351BE55E47417CE9A468EB4785ADF1992BFE24C53EAAD3DFCA7F1E2117A4CD3E3FBF4A54A60EBB80C62417CBD5EC370E56721D710FA")),
			new ContactInfo("Bihaiko", "bihaiko.dyndns.org", 6789),
			new ContactInfo("Célio", "ccidral.dyndns.org", 9789),
			new ContactInfo("Daniel Santos", "dfcsantos.homeip.net", 7777),
			new ContactInfo("Douglas Giacomini", "dtgiacomini.dyndns.org", 5923),
			new ContactInfo("Dummy", "localhost", 7777, new Seal(new ImmutableByteArray(new byte[128]))),
			new ContactInfo("Igor Arouca", "igorarouca.dyndns.org", 6789, sealFromString("F9EEBC9D1E11037D0A6B8BDBFF83FAE393F8BC3975D843BD51BE7C3311EEBA5CA582EEDBF1CB023C09534128E2CEE064CAEA9CA925AC7BB16D15A01F2C713B1260E38ABBDBD5728CE54B7962FF45B4B367D5FE3A25C89D6689A52D88F6AAEAFCAFFC18B7B677C5E0E32C89B1AB5F09F732A22C566D036A5CF92224786C5E7951")),
			new ContactInfo("Kalecser", "kalecser.dyndns.org", 7770),
			new ContactInfo("Klaus", "klausw.selfip.net", 5923, sealFromString("46162EB567B755C523200A68559E9FAA4EC68ED7F0788EAE418DE5A5FFC08C069779F2DC316B91BBD9F9F259D74A9A039795292E8C72F0860F08DC96C6400619B87CB2429932F4F859CEA0AA3C14B37DA27DE4BC12068B6361A20CE340DE5A7EBFF243F312021B15302EF2CBC8B752ADFB557A0D7E7894C0883331FB4AC3BAC6")),
			new ContactInfo("Nell", "anelisedaux.dyndns.org", 5924),
			new ContactInfo("Priscila Vriesman", "priscilavriesman.dyndns.org", 7770),
			new ContactInfo("Ramon Tramontini", "ramontramontini.dyndns.org", 7770),
			new ContactInfo("Vitor Pamplona", "vfpamp.dyndns.org", 5923),
		};
	}

	
	private Seal sealFromString(String seal) throws DecodeException {
		return new Seal(new ImmutableByteArray(my(Codec.class).hex().decode(seal)));
	}

	
	static class ContactInfo {
		final String _nick;
		final String _host;
		final int _port;
		final Seal _seal;

		ContactInfo(String nick, String host, int port) {
			this(nick, host, port, null);
		}

		ContactInfo(String nick, String host, int port, Seal seal) {
			_nick = nick;
			_host = host;
			_port = port;
			_seal = seal;
		}
	}

}