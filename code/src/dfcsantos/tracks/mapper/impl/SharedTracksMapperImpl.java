package dfcsantos.tracks.mapper.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.folder.keeper.TracksFolderKeeper;
import dfcsantos.tracks.mapper.SharedTracksMapper;

class SharedTracksMapperImpl implements SharedTracksMapper {

	private Latch _sharedTracksMapping = my(Latches.class).produce();

	private AtomicBoolean _stopMapping;

	@SuppressWarnings("unused") private WeakContract _toAvoidGC;

	{
		_toAvoidGC = my(TracksFolderKeeper.class).sharedTracksFolder().addReceiver(new Consumer<File>() { @Override public void consume(File sharedTracksFolder) {
			if (_stopMapping != null) _stopMapping.set(true);
			if (_sharedTracksMapping.isOpen()) _sharedTracksMapping = my(Latches.class).produce(); 
			startSharedTracksMapping();
		}});
	}

	private void startSharedTracksMapping() {
		my(Threads.class).startDaemon("Shared Tracks Folder Mapping", new Runnable() { @Override public void run() {
			map(sharedTracksFolder());
			_stopMapping = null;
			_sharedTracksMapping.open();
		}});
	}

	private File sharedTracksFolder() {
		return my(TracksFolderKeeper.class).sharedTracksFolder().currentValue();
	}

	private void map(final File tracksFolder) {		
		try {
			_stopMapping = new AtomicBoolean();
			my(FileMap.class).put(tracksFolder, _stopMapping, "mp3");
		} catch (IOException e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		}
	}

	@Override
	public void waitTillMappingIsFinished() {
		_sharedTracksMapping.waitTillOpen();
	}

}
