package sneer.bricks.pulp.tuples.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.io.log.tests.TestThatUsesLogger;
import sneer.bricks.pulp.tuples.TupleSpace;

public class TupleGcTest extends TestThatUsesLogger {

	private final TupleSpace _subject = my(TupleSpace.class);
	
	static volatile String _currentGeneration;
	static volatile int _garbageCollectedCounter;
	
	
	@Test (timeout = 2000)
	public void tuplesLimitAmount() {
		_garbageCollectedCounter = 0;

		_currentGeneration = "tuplesLimitAmount";
		int cache = _subject.transientCacheSize();
		publishMyTestTuples(cache + 42);
		
		while (_garbageCollectedCounter != 42) {
			System.gc();
			my(Threads.class).sleepWithoutInterruptions(20);
		}
	}


	private void publishMyTestTuples(int amount) {
		for (int i = 0; i < amount; i++)
			_subject.publish(new GcTestTuple(i));
	}
	
}


