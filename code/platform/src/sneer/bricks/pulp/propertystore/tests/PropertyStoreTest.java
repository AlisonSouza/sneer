package sneer.bricks.pulp.propertystore.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.pulp.propertystore.PropertyStore;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.environments.Environments;

public class PropertyStoreTest extends BrickTest {

	@Test
	public void testPropertyStore() {
		runInNewEnvironment(new Runnable() { @Override public void run() {
			PropertyStore subject1 = my(PropertyStore.class);
			assertNull(subject1.get("Height"));
			subject1.set("Height", "1,80m");
			subject1.set("Weight", "85kg");
			assertEquals("1,80m", subject1.get("Height"));
			assertEquals("85kg", subject1.get("Weight"));
		}});
		
		runInNewEnvironment(new Runnable() { @Override public void run() {
			PropertyStore subject2 = my(PropertyStore.class);
			assertEquals("1,80m", subject2.get("Height"));
			assertEquals("85kg", subject2.get("Weight"));
		}});
	}

	private void runInNewEnvironment(Runnable runnable) {
		Environments.runWith(newTestEnvironment(my(FolderConfig.class)), runnable);
	}
	
}

