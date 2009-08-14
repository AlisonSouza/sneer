package sneer.bricks.softwaresharing.gui.impl;

import static sneer.foundation.environments.Environments.my;

import javax.swing.ImageIcon;

import sneer.bricks.skin.image.ImageFactory;
import sneer.bricks.softwaresharing.FileVersion;

public class FileVersionWrapper {
	
	private final ImageIcon _icon;
	
	public ImageIcon getIcon() {
		return _icon;
	}

	private static final ImageIcon _extra = loadIcon("extraFile.png");
	private static final ImageIcon _current = loadIcon("currentFile.png");
	private static final ImageIcon _modified = loadIcon("modifiedFile.png");
	private static final ImageIcon _missing = loadIcon("missingFile.png");

	private static ImageIcon loadIcon(String fileName){
		return my(ImageFactory.class).getIcon(BrickInfoTreeNode.class, fileName);
	}
	
	private final FileVersion _fileVersion;

	public FileVersionWrapper(FileVersion fileVersion) {
		_fileVersion = fileVersion;
		
		if(_fileVersion.status() == FileVersion.Status.EXTRA ) {
			_icon = _extra;
			return;
		}
		
		if(_fileVersion.status() == FileVersion.Status.MISSING ) {
			_icon = _missing;
			return;
		}
		
		if(_fileVersion.status() == FileVersion.Status.DIFFERENT ) {
			_icon = _modified;
			return;
		}
		
		_icon = _current;		
	}
	
	@Override public String toString() {
		return _fileVersion.name();
	}

	public FileVersion fileVersion(){
		return _fileVersion;
	}
}
