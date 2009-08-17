package sneer.bricks.hardware.cpu.threads.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.lang.contracts.Contract;
import sneer.bricks.hardware.cpu.threads.Latch;
import sneer.bricks.hardware.cpu.threads.Steppable;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.pulp.events.pulsers.PulseSource;
import sneer.bricks.pulp.events.pulsers.Pulser;
import sneer.bricks.pulp.events.pulsers.Pulsers;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;

class ThreadsImpl implements Threads {

	private static final Latches Latches = my(Latches.class);
	
	private final Latch _crash = Latches.newLatch();
	private final Pulser _crashingPulser = my(Pulsers.class).newInstance();
	static private boolean _isCrashing = false;

	@Override
	public void waitWithoutInterruptions(Object object) {
		try {
			object.wait();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void sleepWithoutInterruptions(long milliseconds) {
		try {
			Thread.sleep(milliseconds);

		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}


	@Override
	public void joinWithoutInterruptions(Thread thread) {
		try {
			thread.join();

		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void startDaemon(final String threadName, final Runnable runnable) {
		if (_isCrashing) return;
		
		final Environment environment = my(Environment.class);
		final Latch hasStarted = Latches.newLatch();

		new Daemon(threadName) { @Override public void run() {
			hasStarted.open();
			Environments.runWith(environment, runnable);
		}};
		
		hasStarted.waitTillOpen();
	}

	@Override
	public Contract startStepping(Steppable steppable) {
		Stepper result = new Stepper(steppable);
		startDaemon(inferThreadName(), result);
		return result;
	}

	private String inferThreadName() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		StackTraceElement element = stackTrace[3];
		String className = toSimpleClassName(element.getClassName());
		
		return className + "." + element.getMethodName() + "(" + Threads.class.getClassLoader() + ")"; 
	}

	private static String toSimpleClassName(String className) {
		return className.substring(className.lastIndexOf(".") + 1);
	}

	/**Waits until crashAllThreads() is called. */
	@Override
	public void waitUntilCrash() {
		_crash.waitTillOpen();
	}

	@Override
	public void crashAllThreads() {
		_isCrashing = true;

		_crashingPulser.sendPulse();
		Daemon.killAllInstances();
		_crash.open();
	}

	@Override
	public PulseSource crashing() {
		return _crashingPulser.output();
	}
}
