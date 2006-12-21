import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.JOptionPane;


public class Boot {
	
	public static void main(String[] ignored) {
		try {
			tryToRun();
		} catch (Throwable t) {
			t.printStackTrace();
			showError(t.toString());
		}
	}


	private static void tryToRun() throws Exception {
		checkJavaVersionOtherwiseExit();
		strap();
	}


	private static void strap() throws Exception {
		URLClassLoader loader = createIndependentClassLoaderFor(strapURL());
		Thread.currentThread().setContextClassLoader(loader);
		invokeMainMethodOn(loader.loadClass("sneer.strap.Main"));
	}


	private static URLClassLoader createIndependentClassLoaderFor(URL jar) {
		return new URLClassLoader(new URL[]{jar}, vmBootstrapClassLoader());
	}


	private static ClassLoader vmBootstrapClassLoader() {
		ClassLoader candidate = ClassLoader.getSystemClassLoader();
		while (candidate.getParent() != null) candidate = candidate.getParent();
		return candidate;
	}


	private static URL strapURL() {
		return Boot.class.getClassLoader().getResource("strap.jar");
	}


	private static void invokeMainMethodOn(Class clazz) throws Exception {
		clazz.getMethod("main", new Class[] { String[].class }).invoke(null, new Object[] { new String[0] });
	}

	
	private static void checkJavaVersionOtherwiseExit() {
		String version = System.getProperty("java.specification.version");
		if (Float.parseFloat(version) >= 1.6f) return;
		
		String message = "You are running Sneer on Java version " + version + ".\n\n" +
				" You need Java version 6 or newer.";
		showError(message);
		System.exit(-1);
	}


	private static void showError(String message) {
		try {
			JOptionPane.showOptionDialog(null, " " + message + "\n\n", "Sneer", JOptionPane.ERROR_MESSAGE, 0, null, new Object[]{"Exit"}, "Exit");
		} catch (RuntimeException headlessExceptionDoesNotExistInOlderJREs) {
			System.out.println("ERROR: " + message);
		}
	}

}
