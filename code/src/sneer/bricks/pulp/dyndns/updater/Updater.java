package sneer.bricks.pulp.dyndns.updater;

import java.io.IOException;

import basis.brickness.Brick;


@Brick
public interface Updater {

	void update(String dynDnsHost, String dynDnsUser, String password, String newIp) throws UpdaterException, IOException;

}
