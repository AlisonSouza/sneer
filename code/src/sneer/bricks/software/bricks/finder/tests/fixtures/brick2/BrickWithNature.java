package sneer.bricks.software.bricks.finder.tests.fixtures.brick2;

import sneer.bricks.software.bricks.finder.tests.fixtures.nature.SomeNature;
import sneer.foundation.brickness.Brick;

@Brick(value = SomeNature.class, hasImpl = false)
public interface BrickWithNature {}
