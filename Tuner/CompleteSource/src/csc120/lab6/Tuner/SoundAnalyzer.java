package csc120.lab6.Tuner;

import android.app.IntentService;
import android.content.Intent;
//import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.v4.content.LocalBroadcastManager;
import csc120.lab6.FFT.*;

public class SoundAnalyzer extends IntentService{
	private static int samplingFrequency = 44100;
	private static int minSize = 4096;
	private static int size = minSize;
	private static Complex[] frequencies = new Complex [size];
	
	public static Complex[] getFreqMagnitudes(){
		return frequencies;
	}
	
	public static int getSamplingFrequency() {
		return samplingFrequency;
	}
	
	public static int getBufferSize() {
		return size;
	}
	
	public static void setBufferSize(int nSize) {
		size = nSize;
		frequencies = new Complex [size];
	}
	
	public SoundAnalyzer() {
		super("FFT");
	}
	
	public void getMagnitudes() {
		if(TunerActivity.getAudioFlag() == 0) {
			try {
				int loopNum = (int) ((double) size / (double) minSize);
				short[] rawAudio = getAudio();
				int bufferSize = rawAudio.length/2;
				for(int i = 0; i < bufferSize; i++){
					frequencies[i] = new Complex ((double) rawAudio[i], 0);
				}
				FastFourierTransform.fft(frequencies);
			} catch (NullPointerException e) {
				TunerActivity.b.putBoolean("IsFailure", true);
			}
			TunerActivity.setAudioFlag(1);
		}
			
	}
	
	private short[] getAudio(){
		// record data from mic into buffer
		try {
			minSize = AudioRecord.getMinBufferSize(samplingFrequency,
					AudioFormat.
					CHANNEL_CONFIGURATION_MONO,
					AudioFormat.ENCODING_PCM_16BIT);
			AudioRecord audioInput = new AudioRecord(MediaRecorder.AudioSource.MIC, 
					samplingFrequency,
					AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioFormat.ENCODING_PCM_16BIT,
					minSize);
			short[] buffer = new short[minSize];
			audioInput.startRecording();
			audioInput.read(buffer, 0, minSize);
			audioInput.stop();
			audioInput.release();
			return buffer;
		} catch (NullPointerException e) {
			return null;
		}
		catch (RuntimeException e) {
			return null;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		getMagnitudes();
		sendMessage();
	}
	
	public void sendMessage() {
		Intent audioEvent = new Intent("AudioEvent");
		LocalBroadcastManager.getInstance(this).sendBroadcast(audioEvent);
	}

}
