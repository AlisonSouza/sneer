package sneer.bricks.skin.audio.speaker.impl;

import javax.sound.sampled.LineUnavailableException;

import sneer.bricks.skin.audio.speaker.Speaker;

class SpeakerImpl implements Speaker {

	@Override
	public Line acquireLine() throws LineUnavailableException {
		return new LineImpl();
	}
	

}
