package sneer.kernel.container.bytecode.dependencies;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface DependencyFinder {

	List<String> findDependencies(InputStream classInputStream) throws IOException;

}

