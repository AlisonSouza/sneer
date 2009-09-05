package sneer.bricks.software.bricks.finder.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.junit.Test;

import sneer.bricks.hardware.io.log.tests.BrickTestWithLogger;
import sneer.bricks.software.bricks.finder.BrickFinder;
import sneer.bricks.software.code.compilers.java.tests.JarUtils;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.foundation.testsupport.AssertUtils;

public class BrickFinderTest extends BrickTestWithLogger {

	private final BrickFinder _subject = my(BrickFinder.class);
	
	@Test
	public void findBricks() throws IOException {
		File testDir = JarUtils.fileFor(getClass()).getParentFile();
		my(FolderConfig.class).ownBinFolder().set(testDir);
		my(FolderConfig.class).platformBinFolder().set(testDir);
		
		Collection<String> bricks = _subject.findBricks();

		AssertUtils.assertSameContents(bricks,
			sneer.bricks.software.bricks.finder.tests.fixtures.brick1.BrickWithoutNature.class.getName(),
			sneer.bricks.software.bricks.finder.tests.fixtures.brick2.BrickWithNature.class.getName(),
			sneer.bricks.software.bricks.finder.tests.fixtures.nature.SomeNature.class.getName()
		);
	}
	
}
