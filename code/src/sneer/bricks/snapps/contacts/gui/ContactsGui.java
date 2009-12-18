package sneer.bricks.snapps.contacts.gui;

import sneer.bricks.network.social.Contact;
import sneer.bricks.pulp.natures.gui.GUI;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.skin.main.instrumentregistry.Instrument;
import sneer.bricks.software.bricks.snappstarter.Snapp;
import sneer.foundation.brickness.Brick;

@Snapp
@Brick(GUI.class)
public interface ContactsGui extends Instrument {

	Signal<Contact> selectedContact();
	
}
