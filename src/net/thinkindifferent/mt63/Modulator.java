package net.thinkindifferent.mt63;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class Modulator extends Thread {
	private boolean running = false;
	private String textToSend = "";
	
	static {
		System.loadLibrary("MT63Modulator");
	}
	
	private native int processText(String text, short[] buffer);
	private native void initTx();
	
	public Modulator() {
	}
	
	public void run() {
		final int MONO = Integer.parseInt(android.os.Build.VERSION.SDK) < 5 ? AudioFormat.CHANNEL_CONFIGURATION_MONO : AudioFormat.CHANNEL_OUT_MONO;
		while(running) {
			if(textToSend != "") {
				short[] buffer = new short[240000];
				int length = processText(textToSend, buffer);
				AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, MONO, AudioFormat.ENCODING_PCM_16BIT, length * 2, AudioTrack.MODE_STATIC);
				track.write(buffer, 0, length);
				track.play();
				textToSend = "";
			}
		}
	}
	
	public void startModulator() {
		running = true;
		initTx();
		this.start();
	}
	
	public void stopModulator() {
		running = false;
		while(true) {
			try {
				this.join();
				break;
			} catch(InterruptedException e) { }
		}
	}
	
	public void sendText(String text) {
		textToSend = text;
	}	
}
