package sneer.kernel.container.bytecode.dependencies;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import sneer.brickness.Brick;

@Brick
public interface DependencyFinder {

	List<String> findDependencies(InputStream classInputStream) throws IOException;

}

