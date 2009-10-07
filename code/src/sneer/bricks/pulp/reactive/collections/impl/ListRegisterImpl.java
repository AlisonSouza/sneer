package sneer.bricks.pulp.reactive.collections.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
import sneer.bricks.pulp.reactive.collections.ListChange;
import sneer.bricks.pulp.reactive.collections.ListRegister;
import sneer.bricks.pulp.reactive.collections.ListSignal;
import sneer.foundation.lang.Consumer;

class ListRegisterImpl<VO> implements ListRegister<VO> {

	private class MyOutput extends AbstractListSignal<VO> {

		@Override
		public VO currentGet(int index) {
			return _list.get(index);
		}

		@Override
		public int currentIndexOf(VO element) {
			return _list.indexOf(element);
		}

		public Iterator<VO> iterator() {
			synchronized (_list) {
				return new ArrayList<VO>(_list).iterator(); //Optimize
			}
		}

		@Override
		public Signal<Integer> size() {
			return _size.output();
		}

		@Override
		public List<VO> currentElements() {
			synchronized (_list) {
				return new ArrayList<VO>(_list);
			}
		}

		@Override
		protected CollectionChange<VO> currentElementsAsCollectionChange() {
			return new CollectionChangeImpl<VO>(currentElements(), null);
		}

		@Override
		protected ListChange<VO> currentElementsAsListChange() {
			return new CurrentListElements<VO>(currentElements());
		}

	}

	private final List<VO> _list = new ArrayList<VO>();
	private final Register<Integer> _size = my(Signals.class).newRegister(0);
	private final MyOutput _output = new MyOutput();

	@Override
	public void add(VO element) {
		int index = -1;		
		synchronized (_list) {
			_list.add(element);
			_size.setter().consume(_list.size());
			index = _list.size() - 1;
		}
		_output.notifyReceivers(new ListElementAdded<VO>(index, element));
	}
	
	@Override
	public void addAt(int index, VO element) {
		synchronized (_list) {
			_list.add(index, element);
			_size.setter().consume(_list.size());
		}
		_output.notifyReceivers(new ListElementAdded<VO>(index, element));
	}
	
	@Override
	public void remove(VO element) {
		synchronized (_list) {
			int index = _list.indexOf(element);
			if (index == -1) throw new IllegalArgumentException("ListRegister did not contain element to be removed: " + element);
			
			removeAt(index);
		}
	}

	@Override
	public void removeAt(int index) {
		VO oldValue;
		synchronized (_list) {
			oldValue = _list.remove(index);
			_size.setter().consume(_list.size());
		}
		_output.notifyReceivers(new ListElementRemoved<VO>(index, oldValue));
	}

	public ListSignal<VO> output() {
		return _output;
	}

	@Override
	public Consumer<VO> adder() {
		return new Consumer<VO>() { @Override public void consume(VO valueObject) {
			add(valueObject);
		}};
	}

	@Override
	public Consumer<VO> remover() {
		return new Consumer<VO>() { @Override public void consume(VO valueObject) {
			remove(valueObject);
		}};
	}

	@Override
	public void replace(int index, VO newElement) {
		VO old;
		synchronized (_list) {
			old = _list.remove(index);
			_list.add(index, newElement);
		}
		_output.notifyReceivers(new ListElementReplaced<VO>(index, old, newElement));
	}
	
	@Override
	public void move(int oldIndex, int newIndex) {
		if(oldIndex==newIndex) return;
		int tmpIndex = newIndex>oldIndex ? newIndex-1 : newIndex;
		tmpIndex = newIndex<0 ? 0 : newIndex;
		
		VO element;
		synchronized (_list) {
			element = _list.get(oldIndex);
			_list.remove(oldIndex);
			if(newIndex > _list.size())
				_list.add(element);
			else
				_list.add(tmpIndex, element);
		}
		_output.notifyReceivers(new ListElementMoved<VO>(oldIndex, newIndex, element));
	}
	
	private static final long serialVersionUID = 1L;

}
