package dfcsantos.tracks.sharing.playingtracks.server.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.sharing.playingtracks.protocol.PlayingTrack;
import dfcsantos.tracks.sharing.playingtracks.server.PlayingTrackServer;
import dfcsantos.wusic.Wusic;

class PlayingTrackServerImpl implements PlayingTrackServer {

	@SuppressWarnings("unused") private final WeakContract _refToAvoidGC;

	{
		_refToAvoidGC = my(Wusic.class).playingTrackName().addReceiver(new Consumer<String>() { @Override public void consume(String playingTrack) {
			broadcastPlayingTrack(playingTrack);
		}});
	}

	private void broadcastPlayingTrack(String playingTrack) {
		if (playingTrack.equals("<No track to play>")) return;
		my(TupleSpace.class).acquire(new PlayingTrack(playingTrack));
	}

}