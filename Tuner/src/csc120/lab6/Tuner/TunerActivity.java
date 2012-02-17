package csc120.lab6.Tuner;

import csc120.lab6.FFT.Complex;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v4.content.LocalBroadcastManager;

public class TunerActivity extends Activity implements OnClickListener{
    public ProgressBar leftBar;
    public ProgressBar rightBar;
    public TextView frequencyDisplay;
    public TextView noteDisplay;
    private double[] thresholds = new double[4096]; // magnitude thresholds
    private double[] tFrequencies = new double[4096]; // threshold frequencies
    public static Bundle b = new Bundle();
    String lastNote = "A4";
    MusicNotes Notes = new MusicNotes();
    public static int audioFlag = 0; // 0 = not reading, 1 = has read
    private SoundAnalyzer mService;
    
    private BroadcastReceiver receiver = new BroadcastReceiver() {
   	 	@Override
   	 	public void onReceive(Context context, Intent intent) {
   	 		Log.d("TunerActivity Receiver", "Got message.");
   	 		tune();
   	 		sendRequest();
   	 	}
    };
    
    public void sendShutdownMessage() {
    	Intent shutdown = new Intent("AudioShutdown");
    	LocalBroadcastManager.getInstance(this).sendBroadcast(shutdown);
    }
    
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
        //sendRequest(); //get first tare
        //bindService(intentAudio, iConnection, Context.BIND_AUTO_CREATE);
        //tare();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("AudioEvent"));
        // start cycle
        sendRequest();
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    }

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
	
	@Override
	protected void onDestroy() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
		super.onDestroy();
    }
	
	public static synchronized void setAudioFlag(int a) {
		audioFlag = a;
	}
	
	public static synchronized int getAudioFlag() {
		return audioFlag;
	}
	
	/**
	 * The main method, it finds the maximum frequency and the nearest note.
	 */
	public void tune() {
		double[] latestMags = new double[4096], latestFreqs = new double[4096];
		getFreq(latestMags, latestFreqs);
		int max = getMax(latestMags);
		if(max != -1) {
			// note detected
			double rawFreq = latestFreqs[max];
			int size = Notes.getNotes().length;
			double[] notes = new double[size];
			double log2 = Math.log(2);
			double distance = 1000000;
			int closest = 0;
			double freq = Math.log(rawFreq)/log2;
			for(int y = 0; y < size; y++) {
				notes[y] = Math.log(Notes.getNotes()[y])/log2;
				if(Math.abs(freq - notes[y]) < distance) {
					distance = Math.abs(freq - notes[y]);
					closest = y;
				}
			}
			lastNote = Notes.getNote(closest);
			noteDisplay.setText(lastNote);
			frequencyDisplay.setText("" + rawFreq);
			if(freq - notes[closest] > 0) { //rightProgressBar 
				leftBar.setProgress(0);
				rightBar.setProgress((int) ((distance* (double) 24)* (double) 100));
			}
			else { //leftProgressBar 
				rightBar.setProgress(0);
				leftBar.setProgress((int) ((distance* (double) 24)* (double) 100));
			}
		} 						
	}
	
	public int getMax(double[] magnitudes){
		int max = 0;
		int flag = 0;
		if(magnitudes[0] > thresholds[0]) {
			max = 0;
			flag = 1;
		}
		
		for(int z = 0; z < magnitudes.length; z++){
			if(magnitudes[z] > magnitudes[max] && magnitudes[z] > thresholds[z]){
				max = z;
				flag = 1;
			}
		}
		if(flag == 1) {
			return max;
		}
		else {
			return -1;
		}
	}
		
	private void getFreq(double[] magnitudes, double[] frequencies) {
		if(getAudioFlag() == 1) {
			if(TunerActivity.b.getBoolean("IsFailure", false) == false) {
				//Complex[] rMagnitudes = (Complex[]) TunerActivity.b.getParcelableArray("Frequencies");
				Complex[] rMagnitudes = SoundAnalyzer.getFreqMagnitudes();
				getMagnitudes(rMagnitudes, magnitudes);
				//int rate = TunerActivity.b.getInt("SampleRate", 44100);
				int rate = SoundAnalyzer.getSamplingFrequency();
				getFrequencies(magnitudes.length, rate, frequencies);
			}
			else {
				magnitudes = null;
				frequencies = null;
			}
			setAudioFlag(0);
		}
	}
	
	/**
	 * Computes minimum thresholds for frequencies based upon the 
	 * current sound levels input to the Android mic.
	 */
	private void tare(){
		getFreq(thresholds, tFrequencies);
	}
	
	private double[] getMagnitudes(Complex[] rFrequencies, double [] destination) {
		int size = destination.length;
		for(int i = 0; i < size; i++){
			destination[i] = rFrequencies[i].getMagnitude();
		}
		return destination;
	}
	
	private double[] getFrequencies(int size, int rate, double [] destination){
		for(int i = 0; i < size; i++) {
			destination[i] = (double) i* (double) rate/ (double) size;
		}
		return destination;
	}
	
	
}