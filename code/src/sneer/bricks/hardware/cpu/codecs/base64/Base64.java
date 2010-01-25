package sneer.bricks.hardware.cpu.codecs.base64;

import sneer.foundation.brickness.Brick;

@Brick
public interface Base64 {

	String encode(final byte[] bytes);

	String encode(String text);

}