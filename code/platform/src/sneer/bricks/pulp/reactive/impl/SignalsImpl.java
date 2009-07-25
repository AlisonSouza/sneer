package sneer.bricks.pulp.reactive.impl;

import sneer.bricks.hardware.cpu.lang.contracts.Contract;
import sneer.bricks.pulp.events.EventSource;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.foundation.lang.Consumer;
import sneer.foundation.lang.Functor;

class SignalsImpl implements Signals {

	private static final Consumer<Object> SINK = new Consumer<Object>() { @Override public void consume(Object ignored){} };

	@Override
	public <T> Signal<T> constant(T value) {
		return new ConstantImpl<T>(value);
	}

	@Override
	public Consumer<Object> sink() {
		return SINK;
	}

	@Override
	public <A, B> Signal<B> adapt(Signal<A> input, Functor<A, B> functor) {
		return new Adapter<A, B>(input, functor).output();
	}

	@Override
	public <A, B> Signal<B> adaptSignal(Signal<A> input, Functor<A, Signal<B>> functor) {
		return new SignalAdapter<A, B>(input, functor).output();
	}

	@Override
	public <T> Register<T> newRegister(T initialValue) {
		return new RegisterImpl<T>(initialValue);
	}

	@Override
	public <T> Contract receive(Consumer<? super T> receiver, EventSource<? extends T>... sources) {
		return new UmbrellaContract(receiver, sources);
	}

	@Override
	public <T> Contract receive(EventSource<? extends T> source, Consumer<? super T> receiver) {
		return source.addReceiver(receiver);
	}
}
