package sneer.bricks.hardware.ram.arrays.impl;

import java.util.Collection;

import sneer.bricks.hardware.ram.arrays.ImmutableArray;
import sneer.bricks.hardware.ram.arrays.ImmutableArrays;
import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.bricks.hardware.ram.arrays.ImmutableByteArray2D;

class ImmutableArraysImpl implements ImmutableArrays {

	@Override
	public ImmutableByteArray newImmutableByteArray(byte[] bufferToCopy) {
		return new ImmutableByteArrayImpl(bufferToCopy);
	}

	@Override
	public ImmutableByteArray newImmutableByteArray(byte[] bufferToCopy, int bytesToCopy) {
		return new ImmutableByteArrayImpl(bufferToCopy, bytesToCopy);
	}

	@Override
	public ImmutableByteArray2D newImmutableByteArray2D(byte[][] bufferToCopy) {
		return new ImmutableByteArray2DImpl(bufferToCopy);
	}

	@Override
	public <T> ImmutableArray<T> newImmutableArray(final Collection<T> elements) {
		return new ImmutableArrayImpl<T>(elements);
	}

	@Override
	public <T> ImmutableArray<T> newImmutableArray(T[] elements) {
		return new ImmutableArrayImpl(elements);
	}

}
