package sneer.bricks.software.bricks.interception.tests.fixtures.brickwithlib.impl;

import sneer.bricks.software.bricks.interception.tests.fixtures.brickwithlib.BrickWithLib;

public class BrickWithLibImpl implements BrickWithLib {

	@Override
	public int fooBar() {
		return foo.Foo.bar();
	}
}