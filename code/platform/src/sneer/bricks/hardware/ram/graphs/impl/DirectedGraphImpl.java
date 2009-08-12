package sneer.bricks.hardware.ram.graphs.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import sneer.bricks.hardware.ram.graphs.DirectedGraph;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Producer;

class DirectedGraphImpl<T> implements DirectedGraph<T> {

	private CacheMap<T, Set<T>> _successorsByVertex = CacheMap.newInstance();

	@Override
	public void addEdge(T vertex, T successor) {
		addVertex(successor);
		if (vertex.equals(successor)) return;
		
		produceSuccessors(vertex).add(successor);
	}

	@Override
	public void addVertex(T successor) {
		produceSuccessors(successor); //Registers the vertex in the _successorsByVertex map
	}

	private Set<T> produceSuccessors(final T payload) {
		return _successorsByVertex.get(payload, new Producer<Set<T>>() { @Override public Set<T> produce() {
			return new HashSet<T>();
		}});
	}

	@Override
	public Collection<T> detectCycle() {
		ArrayList<T> result = new ArrayList<T>();
		
		for (T vertex : vertices())
			if (detectCycle(vertex, result)) break;
						
		return result;
	}

	private boolean detectCycle(T vertex, List<T> cycle) {
		if (cycle.contains(vertex)) {
			removeVerticesVisitedBeforeEnteringCycle(vertex, cycle);
			return true;
		}
		
		cycle.add(vertex);
		
		for (T successor : produceSuccessors(vertex))
			if (detectCycle(successor, cycle)) return true;

		cycle.remove(cycle.size() - 1);
		return false;
	}

	private void removeVerticesVisitedBeforeEnteringCycle(T vertexThatStartedCycle, List<T> cycle) {
		Iterator<T> it = cycle.iterator();
		while (it.hasNext() && !it.next().equals(vertexThatStartedCycle))
			it.remove();
	}

	@Override
	public T pluck() {
		if (vertices().isEmpty()) return null;
		
		for (T vertex : vertices())
			if (produceSuccessors(vertex).isEmpty())
				return pluck(vertex);
		
		throw new IllegalStateException("Unable to pluck a leaf vertex because this graph has cycles.");
	}

	private T pluck(T vertex) {
		for (T otherVertex : vertices())
			produceSuccessors(otherVertex).remove(vertex);

		_successorsByVertex.remove(vertex);
		return vertex;
	}

	private Set<T> vertices() {
		return _successorsByVertex.keySet();
	}

}
