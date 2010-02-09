package sneer.bricks.network.computers.sockets.accepter.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;

import sneer.bricks.hardware.cpu.lang.contracts.Contract;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.network.computers.sockets.accepter.SocketAccepter;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.events.EventNotifier;
import sneer.bricks.pulp.events.EventNotifiers;
import sneer.bricks.pulp.events.EventSource;
import sneer.bricks.pulp.network.ByteArrayServerSocket;
import sneer.bricks.pulp.network.ByteArraySocket;
import sneer.bricks.pulp.network.Network;
import sneer.bricks.pulp.port.PortKeeper;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Consumer;

class SocketAccepterImpl implements SocketAccepter {
	
	private final PortKeeper _portKeeper = my(PortKeeper.class);
	private final Network _network = my(Network.class);
	private final BlinkingLights _lights = my(BlinkingLights.class);
	private final Threads _threads = my(Threads.class);
	@SuppressWarnings("unused")
	private final WeakContract _crashingContract = _threads.crashing().addPulseReceiver(new Closure() { @Override public void run() {
		crashServerSocketIfNecessary();
	}});
	

	private final EventNotifier<ByteArraySocket> _notifier = my(EventNotifiers.class).newInstance();

	private final transient Object _portToListenMonitor = new Object();

	private ByteArrayServerSocket _serverSocket;
	
	private int _portToListen;
	
	private Light _cantOpenServerSocket = _lights.prepare(LightType.ERROR);

	private final Light _cantAcceptSocket = _lights.prepare(LightType.ERROR);

	@SuppressWarnings("unused") private final Object _receptionRefToAvoidGc;
	private Contract _stepperContract;

	SocketAccepterImpl() {
		_receptionRefToAvoidGc = _portKeeper.port().addReceiver(new Consumer<Integer>() { @Override public void consume(Integer port) {
			setPort(port);
		}});

		_threads.startStepping(new Closure() { @Override public void run() {
			listenToSneerPort();
		}});
	}

	@Override
    public EventSource<ByteArraySocket> lastAcceptedSocket() {
    	return _notifier.output();
    }

    private void setPort(int port) {
		synchronized (_portToListenMonitor) {
			_portToListen = port;
			_portToListenMonitor.notify();
		}
	}

    private void listenToSneerPort() {
		int myPortToListen = _portToListen;
		crashServerSocketIfNecessary();
		openServerSocket(myPortToListen);	

		if(_serverSocket != null) startAccepting();

		synchronized (_portToListenMonitor) {
			if (myPortToListen == _portToListen)
				_threads.waitWithoutInterruptions(_portToListenMonitor);
		}
    }
	
	private void startAccepting() {
		_stepperContract = _threads.startStepping(new Closure() { @Override public void run() {
			try {
				dealWith(_serverSocket.accept());
			} catch (IOException e) {
				dealWith(e);
			}
		}});

	}
	
	private void dealWith(ByteArraySocket incomingSocket) {
		_lights.turnOffIfNecessary(_cantAcceptSocket);
		_notifier.notifyReceivers(incomingSocket);
	}

	private void dealWith(IOException e) {
		if (_stepperContract != null) 
			_lights.turnOnIfNecessary(_cantAcceptSocket, "Unable to accept client connection", null, e);
	}

	private void openServerSocket(int port) {
		if (port == 0) return;
		try {
			_serverSocket = _network.openServerSocket(port);
			_lights.turnOffIfNecessary(_cantOpenServerSocket);
			_lights.turnOn(LightType.GOOD_NEWS, "TCP port opened: " + port, "Sneer has successfully opened TCP port " + port + " to receive incoming connections from others.", 7000);
		} catch (IOException e) {
			if (_stepperContract != null)
				_lights.turnOnIfNecessary(_cantOpenServerSocket, "Unable to listen on TCP port " + port, helpMessage(), e);
		}
	}

	private String helpMessage() {
		return "Typical causes:\n" +
			"- You might have another Sneer instance already running\n" +
			"- Some other application is already using that port\n" +
			"- Your operating system or firewall is blocking that port, especially if it is below 1024\n" +
			"\n" +
			"You can run multiple Sneer instances on the same machine but each has to use a separate TCP port.";
	}

	private void crashServerSocketIfNecessary() {
		if(_serverSocket == null) return;

		my(Logger.class).log("crashing server socket");
		if (_stepperContract != null) _stepperContract.dispose();
		_stepperContract = null;
		_serverSocket.crash();
	}
}
