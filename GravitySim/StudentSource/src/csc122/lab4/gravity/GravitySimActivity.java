package csc122.lab4.gravity;

import csc122.lab4.gravity.GravCanvas;
import csc122.lab4.gravity.GravCanvas.GravThread;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GravitySimActivity extends Activity implements OnClickListener {
	private GravThread mThread;
	private GravCanvas surface;
	private static Bundle instanceState = new Bundle();
	private static boolean flag = false; // whether we have paused or not
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		Log.d("GravActivity", "onCreate");
        super.onCreate(savedInstanceState);
        /**
         * STUDENT:
         * Here is where you should associate this Activity with your layout
         * and grab references to any buttons and the GravCanvas and GravThreads.
         * You should set this to be your buttons' OnClickListener.
         */
    }    
    
	/**
	 * STUDENT:
	 * You should call this function in order to grab the the local GravCanvas' GravThread.
	 * @param thread
	 */
    public void setThread(GravThread thread) {
		mThread = thread;
	}

	@Override
	public void onClick(View v) {
		/**
		 * STUDENT:
		 * Here is where you should handle button clicks.
		 */
	}
	
	/**
	 * Android function - Called when this activity is paused.
	 */
	@Override
	public void onPause() {
		super.onPause();
		Log.d("GravActivity", "onPause");
	}
	/**
	 * Android function - Called when this activity starts or resumes.
	 */
	@Override
	public void onResume() {
		super.onResume();
		Log.d("GravActivity", "onResume");
		surface.setActivity(this);
	}
	
	/**
	 * Android function - Called when this activity is destroyed or finished.
	 */
	@Override
	public void onDestroy(){
		super.onDestroy();
		mThread.setRunning(false);
		Log.d("GravActivity", "onDestroy");
	}
		
}