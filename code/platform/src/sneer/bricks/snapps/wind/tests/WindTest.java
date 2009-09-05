package sneer.bricks.snapps.wind.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.io.log.tests.TestThatUsesLogger;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.snapps.wind.Shout;
import sneer.bricks.snapps.wind.Wind;

public class WindTest extends TestThatUsesLogger {

	private static final int YEAR_ONE = 1000 * 60 * 60 * 24 * 356;
	private final Wind _subject =  my(Wind.class);
	
	@Test(timeout = 4000)
	public void oldShoutsAreNotHeard() {
		
		my(Clock.class).advanceTimeTo(YEAR_ONE);

		ShoutMock ahhh = new ShoutMock("AHHH!!!", 0);
		ShoutMock choo = new ShoutMock("CHOOO!!!", YEAR_ONE);

		tupleSpace().publish(ahhh);
		tupleSpace().publish(choo);

		tupleSpace().waitForAllDispatchingToFinish();

		assertTrue(_subject.shoutsHeard().currentElements().contains(choo));
		assertEquals(1, _subject.shoutsHeard().currentElements().size());
	}

	
	@Test(timeout = 4000)
	public void testSortedShoutsHeard() {
		tupleSpace().publish(new ShoutMock(""+15, 15));

		for (int i = 30; i > 20; i--) {
			ShoutMock shout = new ShoutMock(""+i, i);
			tupleSpace().publish(shout);
		}
		
		for (int i = 10; i > 0; i--) {
			ShoutMock shout = new ShoutMock(""+i, i);
			tupleSpace().publish(shout);
		}

		tupleSpace().waitForAllDispatchingToFinish();
		Shout previousShout = null;
		for (Shout _shout : _subject.shoutsHeard()) {
			
			if(previousShout==null){
				previousShout = _shout;
				continue;
			}
			
			assertTrue(previousShout.publicationTime() < _shout.publicationTime());
			previousShout = _shout;
		}

		assertEquals(21, _subject.shoutsHeard().size().currentValue().intValue());
	}

	private TupleSpace tupleSpace() {
		return my(TupleSpace.class);
	}
}