package sneer.bricks.skin.main.synth.scroll.impl;

import javax.swing.JScrollPane;

import sneer.bricks.skin.main.synth.Synth;
import sneer.bricks.skin.main.synth.scroll.SynthScrolls;
import static sneer.foundation.commons.environments.Environments.my;

public class SynthScrollsImpl implements SynthScrolls {

	SynthScrollsImpl(){
		my(Synth.class).load(this.getClass());
	}
	
	@Override
	public JScrollPane create() {
		JScrollPane scroll = new JScrollPane();
		my(Synth.class).attach(scroll);
		return scroll;
	}

}
