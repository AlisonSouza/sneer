package sneer.software.bricks.impl;

import static sneer.commons.environments.Environments.my;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import sneer.brickness.StoragePath;
import sneer.software.bricks.Bricks;
import sneer.software.code.compilers.classpath.Classpath;
import sneer.software.code.compilers.classpath.ClasspathFactory;
import sneer.software.code.compilers.java.JavaCompiler;
import sneer.software.code.compilers.java.Result;
import sneer.software.code.metaclass.MetaClass;

public class BricksImpl implements Bricks {

	@Override
	public void publish(File sourceDirectory) {
		
		final Result result = compile(sourceDirectory, binDirectory());
		if (!result.success())
			throw new CompilationError(result.getErrorString());
		
		final String brickName = brickNameFor(result);
		if (null == brickName)
			throw new IllegalArgumentException("Directory '" + sourceDirectory + "' contains no bricks.");
		
		instatiate(brickName);
	}

	private void instatiate(final String brickName) {
		try {
			my(BricksImpl.class.getClassLoader().loadClass(brickName));
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
	}

	private String brickNameFor(Result result) {
		for (MetaClass metaClass : result.compiledClasses())
			if (metaClass.isInterface())
				return metaClass.getName();
		return null;
	}

	private Result compile(File sourceDirectory, final File tempOutputDir) {
		final List<File> sourceFiles = sourceFilesIn(sourceDirectory);
		final Classpath classPath = my(ClasspathFactory.class).sneerApi();
		return my(JavaCompiler.class).compile(sourceFiles, tempOutputDir, classPath);
	}

	private List<File> sourceFilesIn(File sourceDirectory) {
		String[] extensions = { "java" };
		return new ArrayList<File>(FileUtils.listFiles(sourceDirectory, extensions, true));
	}

	private File binDirectory() {
		File result = new File(my(StoragePath.class).get(), "bin");
		if (!result.exists() && !result.mkdirs())
			throw new IllegalStateException("Could not create temporary directory '" + result + "'!");
		return result;
	}

}