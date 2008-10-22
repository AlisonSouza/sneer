package sneer.pulp.tuples.impl;

import static wheel.lang.Types.cast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import sneer.pulp.tuples.Tuple;
import sneer.pulp.tuples.TupleSpace;
import wheel.lang.Omnivore;
import wheel.lang.Types;

public class TupleSpaceImpl implements TupleSpace {
	
	//Refactor The synchronization will no longer be necessary when the container guarantees synchronization of model bricks.

	static class Subscription {

		private final Omnivore<? super Tuple> _subscriber;
		private final Class<? extends Tuple> _tupleType;

		<T extends Tuple> Subscription(Omnivore<? super T> subscriber, Class<T> tupleType) {
			_subscriber = cast(subscriber);
			_tupleType = tupleType;
		}

		void filterAndNotify(Tuple tuple) {
			if (!Types.instanceOf(tuple, _tupleType))
				return;
			
			_subscriber.consume(tuple);
		}


	}

	private static final int TUPLE_SPACE_SIZE_LIMIT = 1000;
	private final Set<Tuple> _tuples = new LinkedHashSet<Tuple>();
	private final List<Subscription> _subscriptions = new ArrayList<Subscription>();
	private long _offset = 0;

	@Override
	public synchronized void publish(Tuple tuple) {
		if (tuple == null) throw new IllegalArgumentException();
		
		if (!_tuples.add(tuple)) return;

		capSize();
		
		for (Subscription subscription : _subscriptions)
			subscription.filterAndNotify(tuple);
	}

	private void capSize() {
		if (_tuples.size() <= TUPLE_SPACE_SIZE_LIMIT) return;

		Iterator<Tuple> tuplesIterator = _tuples.iterator();
		tuplesIterator.next();
		tuplesIterator.remove();
		_offset++;
		
	}

	@Override
	public synchronized <T extends Tuple> void addSubscription(Class<T> tupleType,	Omnivore<? super T> subscriber) {
		_subscriptions.add(new Subscription(subscriber, tupleType));
	}
	
	@Override
	public synchronized <T extends Tuple> void removeSubscription(Class<T> tupleType, Object subscriber) {
		final Iterator<Subscription> iterator = _subscriptions.iterator();
		while (iterator.hasNext()) {
			final Subscription current = iterator.next();
			if (current._tupleType == tupleType
				&& current._subscriber == subscriber) {
				iterator.remove();
				return;
			}
		}
		throw new IllegalArgumentException("Subscription not found.");
	}

}
