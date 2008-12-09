package sneer.skin.sound.mic.tests;

import static wheel.lang.Environments.my;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.TargetDataLine;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

import sneer.skin.sound.kernel.Audio;
import sneer.skin.sound.mic.Mic;
import tests.Contribute;
import tests.JMockContainerEnvironment;
import tests.TestThatIsInjected;
import wheel.testutil.SignalUtils;

@RunWith(JMockContainerEnvironment.class)
public class MicTest extends TestThatIsInjected {

	private static Mic _subject = my(Mic.class);

	private final Mockery _mockery = new JUnit4Mockery();
	@Contribute private final Audio _audio = _mockery.mock(Audio.class);
	private final TargetDataLine _line = _mockery.mock(TargetDataLine.class);
	private final AudioFormat _format = new AudioFormat(8000, 16, 1, true, false);

	
	@Test
	public void testIsRunningSignal() {
		_mockery.checking(soundExpectations());

		SignalUtils.waitForValue(false, _subject.isRunning());
		
		_subject.open();
		SignalUtils.waitForValue(true, _subject.isRunning());
		
		_subject.close();
		SignalUtils.waitForValue(false, _subject.isRunning());
	}


	private Expectations soundExpectations() {
		return new Expectations() {{
			Sequence main = _mockery.sequence("main");
			one(_audio).tryToOpenCaptureLine(); will(returnValue(_line)); inSequence(main);
			allowing(_audio).defaultAudioFormat(); will(returnValue(_format));
			one(_line).read(with(aNonNull(byte[].class)), with(0), with(320)); will(returnValue(320)); inSequence(main);
			one(_line).close(); inSequence(main);
		}};
	}

}