package spikes.sneer.bricks.pulp.own.tagline.impl;

import static basis.environments.Environments.my;
import basis.lang.Consumer;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import spikes.sneer.bricks.pulp.own.tagline.OwnTaglineKeeper;

class OwnTaglineKeeperImpl implements OwnTaglineKeeper {

	private Register<String> _tagline = my(Signals.class).newRegister("[My Tagline]");

	@Override
	public Signal<String> tagline() {
		return _tagline.output();
	}

	@Override
	public Consumer<String> taglineSetter() {
		return _tagline.setter();
	}
}
