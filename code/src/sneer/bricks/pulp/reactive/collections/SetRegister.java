//Copyright (C) 2004 Klaus Wuestefeld
//This is free software. It is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the license distributed along with this file for more details.
//Contributions: Kalecser Kurtz, Fabio Roger Manera.

package sneer.bricks.pulp.reactive.collections;

import java.util.Collection;

public interface SetRegister<T> extends CollectionRegister<T> {

	@Override
	SetSignal<T> output();

	boolean add(T elementAdded);
	void addAll(Collection<? extends T> elements);
	<U extends T> void addAll(U[] elements);
	<U extends T> void remove(U elementRemoved);

	boolean change(CollectionChange<T> change);
	void clear();

}
