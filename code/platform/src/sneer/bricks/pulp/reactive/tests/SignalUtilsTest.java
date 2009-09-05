package sneer.bricks.pulp.reactive.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.io.log.tests.TestThatUsesLogger;
import sneer.bricks.pulp.reactive.SignalUtils;
import sneer.bricks.pulp.reactive.collections.impl.SetRegisterImpl;
import sneer.foundation.lang.Predicate;


public class SignalUtilsTest extends TestThatUsesLogger {
	
	@Test (timeout = 2000)
	public void waitForExistingElementWithPredicate() {
		SetRegisterImpl<String> setRegister = new SetRegisterImpl<String>();
		setRegister.add("foo");
		my(SignalUtils.class).waitForElement(setRegister.output(), new Predicate<String>() { @Override public boolean evaluate(String value) {
			return value.equals("foo");
		}});
		
	}

	@Test (timeout = 2000)
	public void waitForNewElementWithPredicate() {
		final SetRegisterImpl<String> setRegister = new SetRegisterImpl<String>();
		
		my(Threads.class).startDaemon("SignalUtils Test", new Runnable() { @Override public void run() {
			my(Threads.class).sleepWithoutInterruptions(200);
			setRegister.add("foo");
		}});
		
		my(SignalUtils.class).waitForElement(setRegister.output(), new Predicate<String>() { @Override public boolean evaluate(String value) {
			return value.equals("foo");
		}});
		
	}

}
