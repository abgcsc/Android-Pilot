package csc120.lab6.Tuner;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import csc120.lab6.FFT.*;

public class SoundAnalyzer extends IntentService{
	private static int samplingFrequency = 44100;
	private int minSize = 4096;
	private final Binder mBinder = new LocalBinder();
	private static Complex[] frequencies = new Complex [8192];
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
   	 	@Override
   	 	public void onReceive(Context context, Intent intent) {
   	 		Log.d("SoundAnalyzer Receiver", "Got message.");
   	 	}
   };

	public static Complex[] getFreqMagnitudes(){
		return frequencies;
	}
	
	public static int getSamplingFrequency() {
		return samplingFrequency;
	}
	
	public SoundAnalyzer() {
		super("FFT");
		// TODO Auto-generated constructor stub
	}
	
	public void getMagnitudes() {
		if(TunerActivity.getAudioFlag() == 0) {
			try {
				short[] rawAudio = getAudio();
				int bufferSize = rawAudio.length;
				Complex[] cRawAudio = new Complex [bufferSize];
				for(int i = 0; i < bufferSize; i++){
					cRawAudio[i] = new Complex ((double) rawAudio[i], 0);
				}
				frequencies = FastFourierTransform.fft(cRawAudio);
				TunerActivity.b.putParcelableArray("Frequencies", frequencies);
				TunerActivity.b.putDouble("SampleRate", samplingFrequency);
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
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}
	
	@Override
	public boolean onUnbind(Intent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("AudioShutdown"));
	}
	
	@Override
	public void onDestroy() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
		super.onDestroy();
	}

		/*// TODO Auto-generated method stub
		if(TunerActivity.getAudioFlag() == 0) {
			getMagnitudes();
			TunerActivity.setAudioFlag(1);
		}*/

	public class LocalBinder extends Binder {
        SoundAnalyzer getService() {
            return SoundAnalyzer.this;
        }
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
