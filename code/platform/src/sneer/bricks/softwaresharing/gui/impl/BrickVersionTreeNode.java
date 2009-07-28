package sneer.bricks.softwaresharing.gui.impl;

import static sneer.foundation.environments.Environments.my;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.tree.TreeNode;

import sneer.bricks.skin.image.ImageFactory;
import sneer.bricks.softwaresharing.BrickVersion;
import sneer.bricks.softwaresharing.BrickVersion.Status;

class BrickVersionTreeNode extends AbstractTreeNodeWrapper<String> {

	private final String _toString; 
	private final BrickVersion _brickVersion;
	
	private static SimpleDateFormat _ddMMyyHHmmss = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
	private static final ImageIcon _currentVersion = loadIcon("currentVersion.png");
	private static final ImageIcon _rejectedVersion = loadIcon("rejectedVersion.png");

	private static final ImageIcon _differentVersion = loadIcon("differentVersion.png");
	private static final ImageIcon _divergingVersion = loadIcon("divergingVersion.png");

	private static final ImageIcon _addDifferentVersion = loadIcon("addDifferentVersion.png");
	private static final ImageIcon _addDivergingVersion = loadIcon("addDivergingVersion.png");

	private static ImageIcon loadIcon(String fileName){
		return my(ImageFactory.class).getIcon(BrickInfoTreeNode.class, fileName);
	}
	
	BrickVersionTreeNode(TreeNode parent, BrickVersion brickVersion) {
		super(parent, brickVersion);
		_brickVersion = brickVersion;
		
		_toString = _ddMMyyHHmmss.format(new Date(_brickVersion.publicationDate())) + " (users = " + usersCount() + ")";
	}

	@Override public ImageIcon getIcon() {
		if(_brickVersion.status()==Status.DIFFERENT){
			if(_brickVersion.isStagedForExecution())
				return _addDifferentVersion;
			
			return _differentVersion;
		}

		if(_brickVersion.status()==Status.DIVERGING){
			if(_brickVersion.isStagedForExecution())
				return _addDivergingVersion;
			
			return _divergingVersion;
		}

		if(_brickVersion.status()==Status.REJECTED)
			return _rejectedVersion;

		return _currentVersion;
	}
	
	private int usersCount() {
		return _brickVersion.unknownUsers() + _brickVersion.knownUsers().size();
	}

	@Override public String toString() { return  _toString;	}
	
	@Override protected List<String> listChildren() { 
		Collections.sort(_brickVersion.knownUsers(), new Comparator<String>(){ @Override public int compare(String nick1, String nick2) {
			return nick1.compareTo(nick2);
		}});
		return _brickVersion.knownUsers(); 
	}
	
	@SuppressWarnings("unchecked")
	@Override protected AbstractTreeNodeWrapper wrapChild(int childIndex) {
		return new StringTreeNode(this, listChildren().get(childIndex));
	}	
}
