package csc120.lab6.Tuner;

import android.app.IntentService;
import android.content.Intent;
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
	private static Complex magnitudes[] = new Complex [minSize]; // the array that will be used to interface with the FFT
	private static double frequency = 0; // the representative frequency that we choose
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
	 * Returns the frequency magnitudes as we have calculated them.
	 * @return
	 */
	public static Complex[] getMagnitudes() {
		return magnitudes;
	}
	
	public static double getFrequency() {
		return frequency;
	}
	
	/**
	 * Returns the size of the buffer that we are using.
	 */
	public static int getSize() {
		return minSize;
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
		* and save the results in static variables for external access.
		**/
		setMagnitudes(getAudio()); // get the audio
		FastFourierTransform.fft(magnitudes); // pass it through the FFT
		frequency = getFrequency(getMaxMagnitude()); // pick a representative frequency (involves the analysis)
		sendMessage(); // this should be the last line of the function
	}
	
	/**
	 * Reads audio from the microphone and stores it in an array of shorts.
	 * @return
	 */
	private short[] getAudio() {
		int bufSize = AudioRecord.getMinBufferSize(44100, 
				AudioFormat.CHANNEL_CONFIGURATION_MONO, 
				AudioFormat.ENCODING_PCM_16BIT);
		short[] buffer = new short[bufSize];
		AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
				SoundAnalyzer.getSamplingFrequency(), 
				AudioFormat.CHANNEL_CONFIGURATION_MONO, 
				AudioFormat.ENCODING_PCM_16BIT, 
				bufSize);
		recorder.startRecording();
		recorder.read(buffer, 0, bufSize);
		recorder.stop();
		recorder.release();
		return buffer;
	}
	
	/**
	 * Converts the audio shorts into Complex numbers fit for entry in the FFT.
	 * @param buffer
	 */
	private void setMagnitudes(short[] buffer) {
		for(int i = 0; i < minSize; i++) {
			magnitudes[i] = new Complex (buffer[i], 0);
		}
	}
	
	/**
	 * Get index of maximum amplitude frequency.
	 * @return	The index of the largest magnitude.
	 */
	private int getMaxMagnitude() {
		int max = 0;
		int halfSize = minSize/2; // don't care about second half, the range from 22,550 Hz to 44,100
		for(int i = 1; i < halfSize; i++) {
			if(magnitudes[i].getSquareMagnitude() > magnitudes[max].getSquareMagnitude()) {
				max = i;
			}
		}
		return max;
	}
	
	/**
	 * Calculates the frequency associated with the given index
	 * @param index	The index of a magnitude for which the frequency is desired.
	 * @return		The frequency, in Hz, associated with the requested index.
	 */
	private double getFrequency(int index) {
		double width = (double) SoundAnalyzer.getSamplingFrequency()/ (double) SoundAnalyzer.getSize(); // the width of a frequency bin
		return index*width;
	}
	
	/**
	 * Android function -  called when the Service is created
	 */
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	/**
	 * Android function - called when the Service is finished
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	/**
	 * Android function - Informs anybody interested that the audio analysis is complete.
	 */
	public void sendMessage() {
		Intent audioEvent = new Intent("AudioEvent");
		LocalBroadcastManager.getInstance(this).sendBroadcast(audioEvent);
	}

}
