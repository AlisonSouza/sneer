package sneer.bricks.snapps.wind.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.Comparator;

import sneer.bricks.pulp.reactive.collections.CollectionSignals;
import sneer.bricks.pulp.reactive.collections.ListRegister;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.bricks.pulp.reactive.collections.listsorter.ListSorter;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.snapps.wind.Shout;
import sneer.bricks.snapps.wind.Wind;
import sneer.foundation.lang.Consumer;

class WindImpl implements Wind, Consumer<Shout> {

	private final ListSignal<Shout> _sortedShouts;
	private final ListRegister<Shout> _shoutsHeard = my(CollectionSignals.class).newListRegister();

	WindImpl(){
		my(TupleSpace.class).addSubscription(Shout.class, this);
		my(TupleSpace.class).keep(Shout.class);
		
		Comparator<Shout> _comparator = new Comparator<Shout>(){ @Override public int compare(Shout o1, Shout o2) {
			return (int) (o1.publicationTime() - o2.publicationTime());
		}};
		_sortedShouts = my(ListSorter.class).sort(_shoutsHeard.output(), _comparator);
	}

	@Override
	public ListSignal<Shout> shoutsHeard() {
		return _sortedShouts;
	}

	@Override
	public void consume(Shout shout) {
		_shoutsHeard.adder().consume(shout);
	}

	@Override
	public Consumer<String> megaphone() {
		return new Consumer<String>(){ @Override public void consume(String phrase) {
			shout(phrase);
		}};
	}

	private void shout(String phrase) {
		my(TupleSpace.class).publish(new Shout(phrase));
	}
}