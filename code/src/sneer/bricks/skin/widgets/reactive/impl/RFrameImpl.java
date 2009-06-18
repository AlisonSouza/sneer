package sneer.bricks.skin.widgets.reactive.impl;

import static sneer.foundation.environments.Environments.my;

import javax.swing.JFrame;

import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.skin.widgets.reactive.Widget;
import sneer.foundation.lang.Consumer;

class RFrameImpl extends JFrame implements Widget<JFrame> {

	@SuppressWarnings("unused")	private final Object _referenceToAvoidGc;

	RFrameImpl(Signal<?> titleSignal) {
		_referenceToAvoidGc = my(Signals.class).receive(titleSignal, new Consumer<Object>() { @Override public void consume(final Object title) {
			setTitle(valueToString(title));
		}});
	}

	private String valueToString(Object title) {
		return (title == null) ? "" : title.toString();
	}

	@Override
	public JFrame getMainWidget() {
		return this;
	}
}