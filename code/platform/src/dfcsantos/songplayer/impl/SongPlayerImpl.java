package dfcsantos.songplayer.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.BufferedInputStream;
import java.io.InputStream;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.foundation.lang.ByRef;
import dfcsantos.songplayer.SongContract;
import dfcsantos.songplayer.SongPlayer;


class SongPlayerImpl implements SongPlayer {

	private Player _player;
	
	@Override
	public SongContract startPlaying(InputStream stream, Runnable toCallWhenFinished) {
		BufferedInputStream bis = new BufferedInputStream(stream);
		try {
			_player = new Player(bis);
		} catch (JavaLayerException e1) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e1); // Fix Handle this exception.
		}
		
		final ByRef<Thread> playerThread = ByRef.newInstance();
		
		my(Threads.class).startDaemon("Song Player", new Runnable() { @Override public void run() {
			playerThread.value = Thread.currentThread();
			try {
				_player.play();
			} catch (JavaLayerException e) {
				throw new sneer.foundation.lang.exceptions.NotImplementedYet(e);
			}
		}});
		
		return new SongContractImpl(playerThread.value);
	}

}