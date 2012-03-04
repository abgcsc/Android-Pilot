package csc120.lab6.Tuner;

import android.app.IntentService;
import android.content.Intent;
//import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.v4.content.LocalBroadcastManager;
import csc120.lab6.FFT.*;

/**
 * Class SoundAnalyzer is responsible for the frequency analysis of the incoming sound
 * from the Android microphone. It merely analyzes the frequency; it does not perform
 * any action with the information.
 * @author 
 *
 */
public class SoundAnalyzer extends IntentService{
	private static int samplingFrequency = 44100; // The sampling frequency you should use
	private static int minSize = 4096; // The minimum size buffer an AudioRecord can return
	
	/**
	 * Constructor
	 */
	public SoundAnalyzer() {
		super("FFT");
	}
	
	/**
	 * Returns the sampling frequency
	 * @return
	 */
	public static int getSamplingFrequency() {
		return samplingFrequency;
	}
		
	/**
	 * Student:
	 * Everything meaningful that you do must be accessible from or contained within this function.
	 * This function will be called automatically by the application to get the process going.
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		/**
		* This is where you should get the audio, 
		* pass it through the FFT,
		* analyze the audio,
		* pick a representative frequency,
		* and save the results in static variables.
		**/
		sendMessage();
	}
	
	/**
	 * Android function
	 */
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	/**
	 * Android function
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	/**
	 * Android function
	 */
	public void sendMessage() {
		Intent audioEvent = new Intent("AudioEvent");
		LocalBroadcastManager.getInstance(this).sendBroadcast(audioEvent);
	}

}
