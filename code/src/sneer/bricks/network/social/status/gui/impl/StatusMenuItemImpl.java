package sneer.bricks.network.social.status.gui.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.network.social.status.Status;
import sneer.bricks.network.social.status.StatusFactory;
import sneer.bricks.network.social.status.gui.StatusMenuItem;
import sneer.bricks.skin.main.menu.MainMenu;
import sneer.bricks.skin.menu.MenuFactory;
import sneer.bricks.skin.menu.MenuGroup;

class StatusMenuItemImpl implements StatusMenuItem {

	{
		MenuGroup<JMenu> statusSubmenu = my(MenuFactory.class).createMenuGroup("Status");
		ButtonGroup statusGroup = new ButtonGroup();
		for (StatusFactory.Status status : StatusFactory.Status.values()) {
			JRadioButtonMenuItem statusMenuItem = new JRadioButtonMenuItem(status.toString());
			statusMenuItem.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent event) {
				System.out.println("Changing status to: " + event.getActionCommand());
				my(Attributes.class).myAttributeSetter(Status.class).consume(event.getActionCommand());
			}});
			statusGroup.add(statusMenuItem);
			if (status.equals(StatusFactory.DEFAULT_STATUS)) statusMenuItem.setSelected(true);
			statusSubmenu.getWidget().add(statusMenuItem);
		}
		statusSubmenu.getWidget().addSeparator(); // Fix: innocuous while using Synth LAF
		my(MainMenu.class).addGroup(statusSubmenu, 0);
	}

}
