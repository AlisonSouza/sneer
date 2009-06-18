package sneer.foundation.threads;

import java.util.HashSet;
import java.util.Set;

public abstract class Daemon extends Thread {

	static private final Set<Daemon> _instances = new HashSet<Daemon>();

	private final boolean _isInterruptible; 
	
	
	public Daemon(String name) {
		this(name, false);
	}

	public Daemon(String name, boolean isInterruptible) {
		super(name);
		_isInterruptible = isInterruptible;
		addInstance(this);

		setDaemon(true);
		start();
	}

	synchronized static private void addInstance(Daemon instance) {
		_instances.add(instance);
	}

	synchronized static public void killAllInstances() {
		for (Daemon victim : _instances)
			victim.dieQuietly();
		
		_instances.clear();
	}

	@SuppressWarnings("deprecation")
	private void dieQuietly() {
		this.setUncaughtExceptionHandler(new UncaughtExceptionHandler() { @Override public void uncaughtException(Thread t, Throwable ignored) {
			//Do nothing.
		}});
			
		this.stop();
	}

	@Override
	public void interrupt() {
		if (!_isInterruptible)
			throw new UnsupportedOperationException("This daemon is not interruptible.");
		
		super.interrupt();
	}
	
	
	
}
