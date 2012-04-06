package csc120.lab6.Tuner;

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
import csc120.lab6.FFT.*;

/**
 * Class TunerActivity is in charge of displaying to the user what note 
 * they are playing and how close the user is to said note.
 * @author 
 *
 */
public class TunerActivity extends Activity implements OnClickListener{
    public ProgressBar leftBar; // GUI element that indicates the flatness of the note
    public ProgressBar rightBar; // GUI element that indicates the sharpness of the note
    public TextView frequencyDisplay; // GUI element that displays the estimated frequency
    public TextView noteDisplay; // GUI element that displays the estimated note (e.g. C#4)
    public static MusicNotes notes = new MusicNotes(440);
    
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
     * Android function - requests that an audio sample be collected and analyzed
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
        // set up the FFT to accept arrays of desired size; this function could go anywhere, provided the number of calls to it is small
        // for example, it could go inside of WelcomeActivity.onCreate()
        FastFourierTransform.initialize(SoundAnalyzer.getSize()); 
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
			tare();
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
	 * This function may include any part of your analysis after you obtain the frequency magnitudes from the FFT. 
	 * It will automatically be called continuously in concert with SoundAnalyzer.onHandleIntent().
	 */
	public void tune() {
		int noteIndex = notes.getNearestNoteIndex(SoundAnalyzer.getFrequency()); 
		double logFreq = Math.log(SoundAnalyzer.getFrequency()) / Math.log(2);
		int left = 0, right = 0; // the left and right progresses
		if(logFreq < notes.getLogNotes()[noteIndex]) { // less than the note's frequency, to the left
			/**
			 * divide by 1/24 because that is the midpoint distance between two notes in log scale.
			 * 1/24 is also the longest distance possible to the closest note.
			 * Multiply by 100 to scale to max progress.
			 */
			left = (int) (((notes.getLogNotes()[noteIndex] - logFreq) * (double) 24) * (double) 100);
		}
		else { // greater than, to the right
			right = (int) (((logFreq - notes.getLogNotes()[noteIndex]) * (double) 24) * (double) 100);
		}
		updateGUI(notes.getNote(noteIndex), 
				SoundAnalyzer.getFrequency(),
				left,
				right);
	}
	
	/**
	 * Update the display elements. Should be called once in tune().
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
	 * For example, you could press the tare button to mask background noise.
	 */
	private void tare(){
		/**
		 * Chose to do nothing. Could have collected magnitudes from the FFT
		 * and stored them in a separate array. The choice of maximum magnitude
		 * could then be altered such that the maximum must be greater than the 
		 * value stored in this separate threshold array. The idea is that this 
		 * will prevent constant dominant sounds from being chosen as the 
		 * representative frequencies. The consequence is that the function of 
		 * choosing the max magnitude must be moved into TunerActivity or the 
		 * separate array is accessed in SoundAnalyzer via a static accessor.
		 */
	}
	
}