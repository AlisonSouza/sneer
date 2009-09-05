package sneer.bricks.pulp.reactive.collections.tests;

import static sneer.foundation.environments.Environments.my;

import java.util.ArrayList;

import org.junit.Test;

import sneer.bricks.hardware.io.log.tests.TestThatUsesLogger;
import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.ListRegister;
import sneer.foundation.lang.Consumer;

public class ListRegisterTest extends TestThatUsesLogger {

	@Test
	public void testSize() {
		final ListRegister<String> _subject = my(CollectionSignals.class).newListRegister();
		final ArrayList<Integer> _sizes = new ArrayList<Integer>();

		@SuppressWarnings("unused") final Object referenceToAvoidGc = _subject.output().size().addReceiver(new Consumer<Integer>() {@Override public void consume(Integer value) {
			_sizes.add(value);
		}});

		assertEquals(0, _subject.output().size().currentValue().intValue());
		assertSameContents(_sizes, 0);

		_subject.add("spam");
		assertEquals(1, _subject.output().size().currentValue().intValue());
		assertSameContents(_sizes, 0, 1);

		_subject.add("eggs");
		assertEquals(2, _subject.output().size().currentValue().intValue());
		assertSameContents(_sizes, 0, 1, 2);
	}
}
