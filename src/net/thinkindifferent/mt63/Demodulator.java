package net.thinkindifferent.mt63;

import java.util.concurrent.LinkedBlockingQueue;

import android.app.Activity;
import android.widget.TextView;

public class Demodulator extends Thread {
	private boolean running = false;
	private Activity parent;
	private LinkedBlockingQueue<short[]> sampleQueue;
	
	static {
		System.loadLibrary("MT63Demodulator");
	}
	
	private native void initRx();
	private native String processAudio(short audio[]);
	
	private class TextViewUpdater implements Runnable {
		private String text;
		
		public TextViewUpdater(String text) {
			this.text = text;
		}
		
		@Override
		public void run() {
			TextView tv = (TextView)parent.findViewById(R.id.text);
			if(tv != null) {
				tv.append(text);
			}
		}	
	}
	
	public Demodulator(Activity parent) {
		this.parent = parent;
		sampleQueue = new LinkedBlockingQueue<short[]>();
	}
	
	public void run() {
		while(running) {
			String text = "";
			boolean success = false;
			while(!success){
				try {
					text = processAudio(sampleQueue.take());
					success = true;
				} catch (InterruptedException e) { }
			}
			if(text.length() > 0)
				parent.runOnUiThread(new TextViewUpdater(text));
		}
	}
	
	public void startDemodulator() {
		running = true;
		initRx();
		this.start();
	}
	
	public void stopDemodulator() {
		running = false;
		while(true) {
			try {
				this.join();
				break;
			} catch(InterruptedException e) { }
		}
	}
	
	public void addSamples(short[] samples) {
		short[] temp = new short[4000];
		for(int i = 0; i < 4000; ++i) {
			temp[i] = samples[i];
		}
		sampleQueue.add(temp);
	}
}
