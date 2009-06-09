package snapps.contacts.gui.delete.impl;

import static sneer.commons.environments.Environments.my;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import snapps.contacts.actions.ContactAction;
import snapps.contacts.actions.ContactActionManager;
import snapps.contacts.gui.ContactsGui;
import snapps.contacts.gui.delete.DeleteContactWindow;
import sneer.commons.lang.ByRef;
import sneer.pulp.contacts.Contact;
import sneer.pulp.contacts.ContactManager;
import sneer.skin.windowboundssetter.WindowBoundsSetter;

class DeleteContactWindowImpl extends JFrame implements DeleteContactWindow {
	
	private final JButton _yes = new JButton("Yes");
	
	DeleteContactWindowImpl(){
		initGui();
		addContactEditAction();
	}
	
	private void addContactEditAction() {

		final ByRef<Contact> contactRef = ByRef.newInstance();
		_yes.addActionListener(new ActionListener(){ @Override public void actionPerformed(ActionEvent e) {
			setVisible(false);
			my(ContactManager.class).removeContact(contactRef.value);
		}});
		
		my(ContactActionManager.class).addContactAction(new ContactAction(){
			@Override public boolean isEnabled() { return true; }
			@Override public boolean isVisible() { return true; }
			@Override public String caption() { return "Delete Contact...";}
			@Override public void run() {
				Contact contact = my(ContactsGui.class).selectedContact().currentValue();
				contactRef.value = contact;
				
				if(contact==null) return;
				open(contact);
			}});
	}

	private void initGui() {
		setLayout(new GridBagLayout());
		JButton no = new JButton("No");
		no.addActionListener(new ActionListener(){ @Override public void actionPerformed(ActionEvent e) {
			setVisible(false);
		}});
		
		add(0, _yes);
		add(1, no);
	}

	private void add(int x, JButton btn) {
		getContentPane().add(btn,  new GridBagConstraints(x, 0, 1, 1, 0.0, 0.0, 
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0) );
	}
	
	private void open(final Contact contact) {
		setSize(300, 100);
		my(WindowBoundsSetter.class).setBestBounds(this);
		setVisible(true);
		setTitle("Delete '" + contact + "'?");
	}
}