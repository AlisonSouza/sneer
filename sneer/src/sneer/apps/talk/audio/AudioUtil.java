package sneer.apps.talk.audio;

import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.*;

public class AudioUtil {
	static final private int SAMPLE_DURATION_MILLIS = 20;
    static final int SAMPLE_RATE = 11025;
    static final int SAMPLE_SIZE_IN_BITS = 16;
    static final int CHANNELS = 2; //for linux/alsa compatibility, should not use mono
    static final boolean SIGNED = true;
    static final boolean BIG_ENDIAN = false;

    static final int FRAMES_PER_AUDIO_PACKET = 10;

    static final int PCM_BUFFER_SIZE = SAMPLE_RATE * SAMPLE_DURATION_MILLIS * CHANNELS * SAMPLE_SIZE_IN_BITS / 8 / 1000;
    
    private static final AudioFormat AUDIO_FORMAT = new AudioFormat(SAMPLE_RATE,SAMPLE_SIZE_IN_BITS,CHANNELS,SIGNED,BIG_ENDIAN);
	static final int SOUND_QUALITY = 8; //From 0 (bad) to 10 (good);
	static final int NARROWBAND_ENCODING = 0;
    
    static TargetDataLine getTargetDataLine() throws LineUnavailableException{
        return AudioSystem.getTargetDataLine(AUDIO_FORMAT);
    }
    
    static SourceDataLine getSourceDataLine() throws LineUnavailableException{
        return AudioSystem.getSourceDataLine(AUDIO_FORMAT);
    }
    
	static void shortToByte(byte[] buffer, int offset, int value) {
		buffer[offset+1] = (byte) (value & 0x00FF);
		buffer[offset] = (byte) ((value >> 8) & 0x000000FF);
	}

	static int byteToShort(byte[] buffer, int offset) {
		int result = buffer[offset+1];
		result += (buffer[offset]& 0x00FF) << 8;
		return result;
	}
	
	private AudioUtil() {}
}
