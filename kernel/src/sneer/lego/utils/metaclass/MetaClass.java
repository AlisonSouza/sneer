package sneer.lego.utils.metaclass;

import java.io.File;
import java.io.IOException;

public interface MetaClass {

    String getName();
    
    String getPackageName();

    File classFile();

    byte[] bytes() throws IOException;

    boolean isInterface();
}