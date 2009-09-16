package sneer.bricks.pulp.tuples.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.Tuple;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;

public class TuplePersistenceTest extends BrickTest {

	@Test
	public void tuplePersistence() {
		runInNewEnvironment(new Runnable() { @Override public void run() {
			TupleSpace subject1 = createSubject();
	
			assertEquals(0, subject1.keptTuples().size());
	
			subject1.keep(TestTuple.class);
			subject1.publish(tuple(0));
			subject1.publish(tuple(1));
			subject1.publish(tuple(2));
		}});
		
		runInNewEnvironment(new Runnable() { @Override public void run() {
			
			TupleSpace subject2 = createSubject();
			List<Tuple> kept = subject2.keptTuples();
			assertEquals(3, kept.size());
			assertEquals(0, ((TestTuple)kept.get(0)).intValue);
			assertEquals(1, ((TestTuple)kept.get(1)).intValue);
			assertEquals(2, ((TestTuple)kept.get(2)).intValue);

		}});
	}

	
	@Test
	public void filesAreClosedUponCrash() throws IOException {
		
		my(TupleSpace.class).keep(TestTuple.class);
		my(TupleSpace.class).publish(tuple(42));
		
		my(Threads.class).crashAllThreads();
		
		my(IO.class).files().forceDelete(tmpFolder());
	}


	private TestTuple tuple(int i) {
		return new TestTuple(i);
	}
	
	private void runInNewEnvironment(Runnable runnable) {
		Environment newEnvironment = newTestEnvironment(my(FolderConfig.class));
		Environments.runWith(newEnvironment, runnable);
	}
	
	private TupleSpace createSubject() {
		return my(TupleSpace.class);
	}
}


