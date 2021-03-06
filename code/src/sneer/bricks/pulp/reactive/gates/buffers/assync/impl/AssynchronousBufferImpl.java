package sneer.bricks.pulp.reactive.gates.buffers.assync.impl;

import static basis.environments.Environments.my;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import basis.lang.Closure;
import basis.lang.Consumer;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.ram.ref.weak.keeper.WeakReferenceKeeper;
import sneer.bricks.pulp.notifiers.Notifier;
import sneer.bricks.pulp.notifiers.Notifiers;
import sneer.bricks.pulp.notifiers.Source;


class AssynchronousBufferImpl<T> {

	private final BlockingQueue<T> _buffer = new LinkedBlockingQueue<T>();
	private final Notifier<T> _delegate = my(Notifiers.class).newInstance();
	private Thread _daemon;
	@SuppressWarnings("unused")	private final WeakContract _refToAvoidGc;

	
	AssynchronousBufferImpl(Source<T> input, String threadName) {
		my(Threads.class).startDaemon(threadName, new Closure() { @Override public void run() {
			_daemon = Thread.currentThread();
			
			try {
				while (true)
					_delegate.notifyReceivers(_buffer.take());
			} catch (InterruptedException expected) {
				//finalizer() will interrupt this thread.
			}

		}});
		
		
		_refToAvoidGc = input.addReceiver(new Consumer<T>() { @Override public void consume(T value) {
			_buffer.add(value);
		}});
	}


	Source<T> output() {
		return my(WeakReferenceKeeper.class).keep(_delegate.output(), finalizer());
	}


	private Object finalizer() {
		return new Object() {
			@Override protected void finalize() {
				_daemon.interrupt();
			}
		};
	}
	
}
