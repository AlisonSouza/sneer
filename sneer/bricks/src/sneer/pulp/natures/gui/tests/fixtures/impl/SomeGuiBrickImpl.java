package sneer.pulp.natures.gui.tests.fixtures.impl;

import static sneer.commons.environments.Environments.my;

import java.awt.EventQueue;

import sneer.commons.environments.Environment;
import sneer.pulp.natures.gui.tests.fixtures.SomeGuiBrick;
import wheel.io.ui.GuiThread;

class SomeGuiBrickImpl implements SomeGuiBrick {

	@Override
	public Thread currentThread() {
		return Thread.currentThread();
	}
	
	public Thread _currentThread() {
		if (!EventQueue.isDispatchThread()) {
			final CurrentThreadInvocation invocation = new CurrentThreadInvocation();
			GuiThread.strictInvokeAndWait(invocation);
			return invocation.result;
		}
		return Thread.currentThread();
	}
	
	private class CurrentThreadInvocation implements Runnable {
		
		public Thread result;

		@Override
		public void run() {
			result = _currentThread();
		}
	}
	
	@Override
	public Environment currentEnvironment() {
		return my(Environment.class);
	}
}