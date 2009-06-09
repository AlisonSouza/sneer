package sneer.pulp.network;

import java.io.IOException;

public interface ByteArraySocket {

	byte[] read() throws IOException;

	void write(byte[] array) throws IOException;
	
	void crash();

}
