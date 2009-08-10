package sneer.bricks.network.computers.sockets.connections.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;
import java.util.Arrays;

import sneer.bricks.network.computers.sockets.protocol.ProtocolTokens;
import sneer.bricks.pulp.keymanager.Seals;
import sneer.bricks.pulp.network.ByteArraySocket;

class OutgoingHandShaker {

	private static final Seals Seals = my(Seals.class);

	
	static void greet(ByteArraySocket socket) throws IOException {
		socket.write(ProtocolTokens.SNEER_WIRE_PROTOCOL_1);
		socket.write(Seals.ownSeal().bytes());
		expectOK(socket);
	}

	
	private static void expectOK(ByteArraySocket socket) throws IOException {
		byte[] response = socket.read();
		if (!Arrays.equals(response, ProtocolTokens.OK))
			throw new IOException("Peer did not return OK in outgoing socket."); //Optimize: use a single exception instance, that does not fill the stack.
	}

}
