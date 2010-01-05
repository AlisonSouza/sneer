package sneer.bricks.pulp.probe.tests;

import static sneer.foundation.environments.Environments.my;

import org.jmock.Expectations;
import org.jmock.api.Invocation;
import org.jmock.lib.action.CustomAction;
import org.junit.Test;

import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.bricks.network.computers.sockets.connections.ByteConnection;
import sneer.bricks.network.computers.sockets.connections.ConnectionManager;
import sneer.bricks.network.computers.sockets.connections.ByteConnection.PacketScheduler;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.ContactManager;
import sneer.bricks.pulp.distribution.filtering.TupleFilterManager;
import sneer.bricks.pulp.keymanager.Seal;
import sneer.bricks.pulp.keymanager.Seals;
import sneer.bricks.pulp.probe.ProbeManager;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.pulp.serialization.Serializer;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.bricks.statestore.BrickStateStore;
import sneer.bricks.software.bricks.statestore.tests.BrickStateStoreMock;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.testsupport.Bind;
import sneer.foundation.lang.Consumer;

public class ProbeManagerTest extends BrickTest {

	@Bind private final ConnectionManager _connectionManager = mock(ConnectionManager.class);
	@SuppressWarnings("unused")
	@Bind private final BrickStateStore _store = new BrickStateStoreMock();

	@SuppressWarnings("unused")
	private final ProbeManager _subject = my(ProbeManager.class);
	private final TupleFilterManager _filter = my(TupleFilterManager.class);
	private final TupleSpace _tuples = my(TupleSpace.class);
	
	private final ByteConnection _connection = mock(ByteConnection.class);
	private PacketScheduler _scheduler;
	@SuppressWarnings("unused")
	private Consumer<byte[]> _packetReceiver;

	@Test (timeout = 1000)
	public void testTupleBlocking() throws Exception {
		checking(new Expectations(){{
			one(_connectionManager).connectionFor(with(aNonNull(Contact.class))); will(returnValue(_connection));
			one(_connection).isConnected(); will(returnValue(my(Signals.class).constant(true)));
			one(_connection).initCommunications(with(aNonNull(PacketScheduler.class)), with(aNonNull(Consumer.class)));
				will(new CustomAction("capturing scheduler") { @Override public Object invoke(Invocation invocation) throws Throwable {
					_scheduler = (PacketScheduler) invocation.getParameter(0);
					return null;
				}});
		}});

		my(ContactManager.class).addContact("Neide");
		my(Seals.class).put("Neide", newSeal(new byte[]{1}));

		_tuples.acquire(new TupleTypeA(1));
		assertPacketToSend(1);
		_tuples.acquire(new TupleTypeB(2));
		assertPacketToSend(2);

		_filter.block(TupleTypeB.class);
		
		_tuples.acquire(new TupleTypeA(3));
		_tuples.acquire(new TupleTypeB(4));
		assertPacketToSend(3);
		_tuples.acquire(new TupleTypeA(5));
		assertPacketToSend(5);
	}


	private void assertPacketToSend(int id) throws Exception {
		_tuples.waitForAllDispatchingToFinish();
		byte[] packet = _scheduler.highestPriorityPacketToSend();
		_scheduler.previousPacketWasSent();
		
		assertEquals(id, desserialize(packet).id);
	}


	private TupleWithId desserialize(byte[] packet) throws Exception {
		return (TupleWithId)my(Serializer.class).deserialize(packet, TupleWithId.class.getClassLoader());
	}

	
	private Seal newSeal(byte[] bytes) {
		return new Seal(new ImmutableByteArray(bytes));
	}

}
