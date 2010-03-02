package sneer.bricks.hardware.gui.nature.tests;

import static sneer.foundation.environments.Environments.my;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.junit.Test;

import sneer.bricks.hardware.gui.nature.tests.fixtures.SomeGuiBrick;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;

//TODO: nature inheritance (annotation Instrument interface for instance)
//TODO: methods declaring checked exceptions
public class GUINatureTest extends BrickTest {

	@Test
	public void instantiationHappensInTheSwingThread() {
		assertTrue(isGuiThread(my(SomeGuiBrick.class).constructorThread()));
	}

	@Test
	public void invocationHappensInTheSwingThread() {
		assertTrue(isGuiThread(my(SomeGuiBrick.class).currentThread()));
	}

	@Test
	public void listenerInvocationHappensInBricknessEnvironment() {
		final ActionListener listener = my(SomeGuiBrick.class).listenerFor(Environments.current());
		Environments.runWith(null, new Closure() { @Override public void run() {
			listener.actionPerformed(new ActionEvent(this, 0, null));
		}});
	}

	@Test
	public void invocationHappensInBricknessEnvironment() {
		assertSame(Environments.current(), my(SomeGuiBrick.class).currentEnvironment());
	}

	@Test
	public void invocationInTheSwingThreadForVoidMethod() {
		Environments.runWith(Environments.current(), new Closure() { @Override public void run() {
			assertFalse(isGuiThread(Thread.currentThread()));
			my(SomeGuiBrick.class).run(new Closure() { @Override public void run() {
				assertTrue(isGuiThread(Thread.currentThread()));
			}});
		}});
	}

	private boolean isGuiThread(Thread thread) {
		return thread.getName().contains("AWT");
	}

}
