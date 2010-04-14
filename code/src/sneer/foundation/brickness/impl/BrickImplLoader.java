package sneer.foundation.brickness.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sneer.foundation.brickness.Brick;
import sneer.foundation.brickness.BrickConventions;
import sneer.foundation.brickness.BrickLoadingException;
import sneer.foundation.brickness.Nature;


class BrickImplLoader {

	<T> Class<T> loadImplClassFor(Class<T> brick) throws ClassNotFoundException {
		File path = ClassFiles.classpathRootFor(brick);
		String implPackage = BrickConventions.implPackageFor(brick.getName());
		List<Nature> natures = naturesFor(brick);

		ClassLoader apiClassLoader = brick.getClassLoader();
		ClassLoader libsClassLoader = ClassLoaderForBrickLibs.newInstanceIfNecessary(path, implPackage, natures, apiClassLoader);
		ClassLoader nextClassLoader = libsClassLoader == null ? apiClassLoader : libsClassLoader;
		ClassLoader packageLoader = new ClassLoaderForPackage(path, implPackage, natures, nextClassLoader);

		return load(brick, packageLoader);
	}

	private <T> Class<T> load(Class<T> brick, ClassLoader packageLoader) throws ClassNotFoundException {
		String implName = implNameFor(brick.getName());
		try {
			return (Class<T>)packageLoader.loadClass(implName);
		} catch (ClassNotFoundException e) {
			throw new ClassNotFoundException("Impl class " + implName + " not found for brick: " + brick, e);
		}
	}

	public static List<Nature> naturesFor(Class<?> brick) {
		final Brick annotation = brick.getAnnotation (Brick.class);
		if (annotation == null) throw new BrickLoadingException("Brick '" + brick.getName() + "' is not annotated as such!");

		return naturesImplsFor(annotation.value());
	}
	
	private static List<Nature> naturesImplsFor(final Class<? extends Nature>[] natureClasses) {
		final ArrayList<Nature> result = new ArrayList<Nature>(natureClasses.length);
		for (Class<? extends Nature> natureClass : natureClasses)
			result.add(my(natureClass));
		
		return result;
	}
	
	private static String implNameFor(final String brickInterfaceName) {
		return BrickConventions.implClassNameFor(brickInterfaceName);
	}

}

