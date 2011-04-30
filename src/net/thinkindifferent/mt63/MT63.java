package net.thinkindifferent.mt63;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MT63 extends Activity {
	private AudioThread audioThread;
	private Demodulator demodulator;
	
	class TxBtnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			EditText tx_text = (EditText)findViewById(R.id.tx_text);
			Modulator.sendText(tx_text.getText().toString());
			tx_text.setText("");
		}
		
	}
	
    /** Called when the activity is first created. */	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ((TextView)findViewById(R.id.text)).setMovementMethod(new ScrollingMovementMethod());
        ((Button)findViewById(R.id.tx_btn)).setOnClickListener(new TxBtnClickListener());
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