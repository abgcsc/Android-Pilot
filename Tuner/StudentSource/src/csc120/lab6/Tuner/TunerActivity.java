package csc120.lab6.Tuner;

import csc120.lab6.FFT.Complex;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Class TunerActivity is in charge of displaying to the user what note 
 * they are playing and how close the user is to said note.
 * @author 
 *
 */
public class TunerActivity extends Activity implements OnClickListener{
    public ProgressBar leftBar;
    public ProgressBar rightBar;
    public TextView frequencyDisplay;
    public TextView noteDisplay;
    
    /**
     * Android construct - controls feedback loop with SoundAnalyzer
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
   	 	@Override
   	 	public void onReceive(Context context, Intent intent) {
   	 		Log.d("TunerActivity Receiver", "Got message.");
   	 		tune();
   	 		sendRequest();
   	 	}
    };
    
    /**
     * Android function - requests that an audio sample be analyzed
     */
    public void sendRequest() {
    	Intent request = new Intent(this, SoundAnalyzer.class);
    	this.startService(request);
    }
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tuneractivity);
        leftBar = (ProgressBar) this.findViewById(R.id.leftProgress);
        rightBar = (ProgressBar) this.findViewById(R.id.rightProgress);
        frequencyDisplay = (TextView) this.findViewById(R.id.freqText);
        noteDisplay = (TextView) this.findViewById(R.id.noteDisp);
        Button stop = (Button) this.findViewById(R.id.stopBttn);
        stop.setOnClickListener(this);
        Button tare = (Button) this.findViewById(R.id.tareBttn);
        tare.setOnClickListener(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("AudioEvent"));
        // start cycle
        sendRequest();
    }
    
    /**
     * Android function - called when the activity starts
     */
    @Override
    public void onStart() {
    	super.onStart();
    }

    /**
     * Android function - called when something gets clicked
     */
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.stopBttn:
			this.finish();
			break;
		case R.id.tareBttn:
			break;
		}
	}
	
	/**
	 * Android function - called when this activity is finishing
	 */
	@Override
	protected void onDestroy() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
		super.onDestroy();
    }
	
	/**
	 * Student:
	 * This function may include any part of your analysis after you obtain the frequencies. 
	 * It will automatically be called continuously in concert with SoundAnalyzer.onHandleIntent().
	 */
	public void tune() {
								
	}
	
	/**
	 * Update the display elements
	 * @param latestNote		The latest note that the tuner has identified.
	 * @param frequency			The latest frequency related to the latest note.
	 * @param leftProgress		How flat or far below the true note is the frequency? Between 0 and 100.
	 * @param rightProgress		How sharp or far above the true note is the frequency? Between 0 and 100.
	 */
	public void updateGUI(String latestNote, double frequency, int leftProgress, int rightProgress)
	{
		noteDisplay.setText(latestNote);
		frequencyDisplay.setText(""+frequency);
		leftBar.setProgress(leftProgress);
		rightBar.setProgress(rightProgress);
	}
	
	/**
	 * Student:
	 * This part is optional, but may help improve your functionality. 
	 * This function would provide interactive thresholding and noise handling.
	 * For example, you could press the tare button 
	 */
	private void tare(){
		
	}
	
}