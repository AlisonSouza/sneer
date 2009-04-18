package snapps.watchme;

import sneer.brickness.Tuple;
import sneer.hardware.ram.arrays.ImmutableByteArray;

public class ImageDeltaPacket extends Tuple {
	
	public final int x;
	public final int y;
	public final ImmutableByteArray imageData;
	public final int cacheHandle;

	public ImageDeltaPacket(int x_, int y_, ImmutableByteArray imageData_, int cacheHandle_) {
		x = x_;
		y = y_;
		imageData = imageData_;
		cacheHandle = cacheHandle_;
	}

}
