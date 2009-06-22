package sneer.tests.adapters;

import java.io.File;

import sneer.foundation.brickness.Brick;
import sneer.tests.SovereignParty;

@Brick
public interface SneerParty extends SovereignParty {

	byte[] publicKey();

	void setSneerPort(int port);
	int sneerPort();

	void setOwnBinDirectory(File ownBinDirectory);

}