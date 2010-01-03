package sneer.bricks.hardware.ram.arrays;

import java.util.Arrays;

public class ImmutableByteArray2D {
	
	private final byte[][] _payload;
	
	public ImmutableByteArray2D(byte[][] bufferToCopy) {
		_payload = copy(bufferToCopy);
	}

	public byte[][] copy() {
		return copy(_payload);
	}

	public byte[] get(@SuppressWarnings("unused") int index) {
		throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
	}

	static private byte[][] copy(byte[][] original) {
		byte[][] result = new byte[original.length][0];
		
		for (int i = 0; i < original.length; i++)
			result[i] = Arrays.copyOf(original[i], original[i].length);
		
		return result;
	}


}
