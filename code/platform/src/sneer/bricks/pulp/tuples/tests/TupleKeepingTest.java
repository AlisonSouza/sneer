package sneer.bricks.pulp.tuples.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.log.tests.BrickTestWithLogger;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.foundation.lang.Consumer;

public class TupleKeepingTest extends BrickTestWithLogger {

	private int _notificationCounter;
	
	
	@Test (timeout = 5000)
	public void tuplesLimitAmount() {

		@SuppressWarnings("unused")
		WeakContract contract = subject().addSubscription(KeptTuple.class, new Consumer<KeptTuple>() { @Override public void consume(KeptTuple ignored) {
			_notificationCounter++;
		}});
		
		subject().keep(KeptTuple.class);
		subject().publish(new KeptTuple(1));
		flushCache();
		subject().publish(new KeptTuple(1));
		
		subject().waitForAllDispatchingToFinish();

		assertEquals(1, subject().keptTuples().size());
		assertEquals(1, _notificationCounter);
	}


	private TupleSpace subject() {
		return my(TupleSpace.class);
	}


	private void flushCache() {
		int cacheSize = subject().transientCacheSize();
		publishTestTuples(cacheSize);
	}


	private void publishTestTuples(int amount) {
		for (int i = 0; i < amount; i++)
			subject().publish(new TestTuple(i));
	}
	
}


