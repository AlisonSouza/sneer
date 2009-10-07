package sneer.bricks.pulp.dyndns.checkip.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;

import org.jmock.Expectations;
import org.junit.Test;

import sneer.bricks.pulp.dyndns.checkip.CheckIp;
import sneer.bricks.pulp.httpclient.HttpClient;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.testsupport.Bind;

public class CheckIpTest extends BrickTest {
	
	@Bind final HttpClient _client = mock(HttpClient.class);
	
	@Test
	public void test() throws IOException {
		
		final String ip = "123.456.78.90";
		final String responseBody = 
			"<html><head><title>Current IP Check</title></head><body>Current IP Address: "
			+ ip
			+ "</body></html>";
		
		checking(new Expectations() {{
			one(_client).get("http://checkip.dyndns.org/"); will(returnValue(responseBody));
		}});
		
		final CheckIp checkIp = my(CheckIp.class);
		assertEquals(ip, checkIp.check());
		
	}
}
