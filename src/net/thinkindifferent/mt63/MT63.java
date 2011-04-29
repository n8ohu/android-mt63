package net.thinkindifferent.mt63;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class MT63 extends Activity {
	private AudioThread audioThread;
	private Demodulator demodulator;
	
    /** Called when the activity is first created. */	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ((TextView)findViewById(R.id.text)).setMovementMethod(new ScrollingMovementMethod());
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	audioThread.stopAudio();
    	demodulator.stopDemodulator();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	demodulator = new Demodulator(this);
    	demodulator.startDemodulator();
    	audioThread = new AudioThread(demodulator);
    	audioThread.startAudio();
    }
}