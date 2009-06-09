package sneer.software.code.filefilters.java;

import java.io.File;
import java.util.List;

import sneer.software.code.metaclass.MetaClass;

public interface JavaFilter {

	List<MetaClass> listMetaClasses();

	List<File> listFiles();

}
