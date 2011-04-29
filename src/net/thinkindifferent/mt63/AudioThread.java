package net.thinkindifferent.mt63;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;

public class AudioThread extends Thread {
	private boolean running = false;
	private AudioRecord audioRecord;
	private Demodulator demodulator;
	private short[] samples;
	
	public AudioThread(Demodulator demodulator) {
		this.demodulator = demodulator;
		samples = new short[4000];
	}
	
	public void run() {
		while(running) {
			int shortsRead = audioRecord.read(samples, 0, 4000);
			if(shortsRead == 4000) {
				demodulator.addSamples(samples);
			}
		}
	}
	
	public void startAudio() {
		final int MONO = Integer.parseInt(android.os.Build.VERSION.SDK) < 5 ? AudioFormat.CHANNEL_CONFIGURATION_MONO : AudioFormat.CHANNEL_IN_MONO;
		audioRecord = new AudioRecord(AudioSource.DEFAULT, 8000, MONO, AudioFormat.ENCODING_PCM_16BIT, 16000);
		audioRecord.startRecording();
		running = true;
		this.start();
	}
	
	public void stopAudio() {
		running = false;
		while(true) {
			try {
				this.join();
				break;
			} catch (InterruptedException e) { }
		}
		audioRecord.stop();
		audioRecord.release();
		audioRecord = null;
	}
}
