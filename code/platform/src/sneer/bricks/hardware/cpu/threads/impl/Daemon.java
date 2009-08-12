package sneer.bricks.hardware.cpu.threads.impl;

import java.util.HashSet;
import java.util.Set;

abstract class Daemon extends Thread {

	static private final Set<Daemon> _instances = new HashSet<Daemon>();

	public Daemon(String name) {
		super(name);
		addInstance(this);

		setDaemon(true);
		start();
	}

	synchronized static private void addInstance(Daemon instance) {
		_instances.add(instance);
	}

	synchronized static void killAllInstances() {
		for (Daemon victim : _instances)
			victim.dieQuietly();
		
		_instances.clear();
	}

	@SuppressWarnings("deprecation")
	private void dieQuietly() {
		setUncaughtExceptionHandler(new UncaughtExceptionHandler() { @Override public void uncaughtException(Thread t, Throwable ignored) {
			//Do nothing.
		}});
		
		interrupt();
		stop();
		try {
			join();
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	
}
