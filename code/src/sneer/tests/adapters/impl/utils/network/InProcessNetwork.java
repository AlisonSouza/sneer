package sneer.tests.adapters.impl.utils.network;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import sneer.bricks.network.computers.tcp.ByteArrayServerSocket;
import sneer.bricks.network.computers.tcp.ByteArraySocket;
import sneer.bricks.network.computers.tcp.TcpNetwork;

public class InProcessNetwork implements TcpNetwork {

	private final Map<Integer, ByteArrayServerSocket> _serverSocketByPort = new HashMap<Integer, ByteArrayServerSocket>();

	@Override
	public synchronized ByteArraySocket openSocket(String serverIpAddress, int serverPort) throws IOException {
		crashIfNotLocal(serverIpAddress);
		return startClient(serverPort);
	}

	@Override
	public ByteArrayServerSocket openServerSocket(int port) throws IOException {
		return startServer(port);
	}

	private void crashIfNotLocal(String serverIpAddress) throws IOException {
	    if (!serverIpAddress.equals("localhost")) throw new IOException("Only localhost connections are supported by the NetworkMock. Attempted: " + serverIpAddress);
	}

	private InProcessByteArrayServerSocket findServer(int serverPort) {
	    InProcessByteArrayServerSocket existing = (InProcessByteArrayServerSocket) _serverSocketByPort.get(serverPort);
	    if (null == existing)
	    	return null;
	    
	    if (existing.isCrashed()) {
	    	_serverSocketByPort.remove(serverPort);
	    	return null;
	    }
	    
		return existing;
	}

	private ByteArraySocket startClient(int serverPort) throws IOException {
	    InProcessByteArrayServerSocket server = findServer(serverPort); 
	    if (server == null) throw new IOException("No server is listening on this port.");
	
	    return server.openClientSocket();
	}

	private ByteArrayServerSocket startServer(int serverPort) throws IOException {
	    InProcessByteArrayServerSocket old = findServer(serverPort);
	    if (old != null) throw new IOException("Port " + serverPort + " already in use.");
	
	    InProcessByteArrayServerSocket result = new InProcessByteArrayServerSocket(serverPort);
	    _serverSocketByPort.put(serverPort, result);
	    
	    return result;
	}

	@Override
	public String remoteIpFor(ByteArraySocket socket) {
		return "localhost"; // Implement
	}
}