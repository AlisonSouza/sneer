package sneer.bricks.skin.main.menu.exit.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.skin.main.menu.MainMenu;
import sneer.bricks.skin.main.menu.exit.ExitMenuItem;
import sneer.foundation.lang.Closure;

class ExitMenuItemImpl implements ExitMenuItem{
	{
		my(MainMenu.class).addAction("Exit", new Closure() { @Override public void run() {
			System.exit(0);
		}});
	}
}
