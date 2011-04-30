package net.thinkindifferent.mt63;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class Modulator {
	static {
		System.loadLibrary("MT63Modulator");
	}
	
	private static native short[] processText(String text);
	
	public static void sendText(String text) {
		final int MONO = Integer.parseInt(android.os.Build.VERSION.SDK) < 5 ? AudioFormat.CHANNEL_CONFIGURATION_MONO : AudioFormat.CHANNEL_OUT_MONO;
		short[] samples = processText(text);
		AudioTrack t = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, MONO, AudioFormat.ENCODING_PCM_16BIT, samples.length * 2, AudioTrack.MODE_STATIC);
		t.write(samples, 0, samples.length);
		t.play();
	}
}
