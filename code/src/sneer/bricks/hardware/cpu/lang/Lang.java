package sneer.bricks.hardware.cpu.lang;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import sneer.foundation.brickness.Brick;

@Brick
public interface Lang {

	Arrays arrays(); 
	Serialization serialization(); 
	Strings strings();
	
	interface Arrays { 
		void reverse(Object[] array);
	}
	
	interface Serialization {
		byte[] serialize(Serializable obj) ;
		<T> T serialize(byte[] data);
	}
	
	interface Strings { 
		boolean isEmpty(String str);
		String join(Collection<?> collection, String separator);
		String trimToNull(String str);
		String chomp(String str, String suffix);
		String substringBeforeLast(String str, String separator);
		String deleteWhitespace(String str);
		String insertSpacedSeparators(String str, String separator, int interval);
		List<String> readLines(String input);
		byte[] toByteArray(String string);
		String[] splitRight(String line, char separator, int maxParts);
		String substringAfterLast(String str, String separator);
	}
}

