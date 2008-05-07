package sneer.bricks.deployer.test;

import java.io.File;

import org.apache.commons.lang.SystemUtils;

import sneer.bricks.config.SneerConfig;

public class SneerConfigMock implements SneerConfig {

	private File _sneerDirectory;
	
	private File _tmpDirectory;

	private File _brickDirectory;
	
	public SneerConfigMock(File root) {
		_sneerDirectory = new File(root, ".sneer");
		_brickDirectory = new File(_sneerDirectory, "bricks");
		_tmpDirectory = new File(_sneerDirectory,"tmp");
	}
	
	@Override
	public File sneerDirectory() {
		return _sneerDirectory;
	}

	@Override
	public File brickRootDirectory() {
		return _brickDirectory;
	}

	@Override
	public File eclipseDirectory() {
		return new File(SystemUtils.getUserDir(), "bin");
	}

	@Override
	public File tmpDirectory() {
		return _tmpDirectory;
	}
}
