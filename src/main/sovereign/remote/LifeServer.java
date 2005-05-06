//Copyright (C) 2004 Klaus Wuestefeld
//This is free software. It is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the license distributed along with this file for more details.
//Contributions: Rodrigo B de Oliveira, Fabio Roger Manera.

package sovereign.remote;

import java.util.HashMap;
import java.util.Map;

import org.prevayler.foundation.Cool;
import org.prevayler.foundation.network.ObjectServerSocket;
import org.prevayler.foundation.network.ObjectSocket;

import sovereign.Life;
import sovereign.LifeView;

public class LifeServer implements Runnable {

	public static final int REQUEST_FOR_SERVER = 1; 
	public static final int REQUEST_FOR_CLIENT = 2; 
	
	private final Life _life;
	
	private LifeView _remoteLife;

	private final ObjectServerSocket _serverSocket;
	
	private Map<String,LifeView> _remoteLifes = new HashMap<String,LifeView>();

	private String addRemoteLife(LifeView remoteLife) {
		String id = Integer.toString(_remoteLifes.size()); // FIXME: this creates a securty hole... a better (bug still not 100%) approach would be generating a like random 32bits unique id here ... or maybe change the design to reach something else safer 
		_remoteLifes.put(id, remoteLife);
		return id; 
	}
	
	private LifeView remoteLife (String id) {
		return _remoteLifes.get(id);
	}
	
	public LifeServer(Life life, ObjectServerSocket serverSocket) {
		_life = life;
		_serverSocket = serverSocket;
		Cool.startDaemon(this);
	}

	public void run() {
		while (true)
			try {
				final ObjectSocket socket = _serverSocket.accept();

				int kindOfRequest = (Integer)socket.readObject();
				
				if (kindOfRequest == REQUEST_FOR_CLIENT) {
					
					_remoteLife = RemoteLife.createWith(socket, null);
					if (_life.somebodyAskingToBeYourFriend(_remoteLife)) {
						socket.writeObject(addRemoteLife(_remoteLife));
					} else {
						socket.close();
					}
					
				} else {
					
					_remoteLife = remoteLife((String) socket.readObject());

					new LifeResponder(_life, _remoteLife, socket);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

}
