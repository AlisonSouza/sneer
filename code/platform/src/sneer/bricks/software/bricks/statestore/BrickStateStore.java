package sneer.bricks.software.bricks.statestore;

import java.io.IOException;

import sneer.foundation.brickness.Brick;

@Brick
public interface BrickStateStore {

	void writeObjectFor(Class<?> brick, Object object) throws IOException;
	Object readObjectFor(Class<?> brick, ClassLoader classloader) throws IOException, ClassNotFoundException;
	
}