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
	private Button lock;
	private Button play;
	private Button quit;
	private static Bundle instanceState = new Bundle();
	private static boolean flag = false; // whether we have paused or not
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		Log.d("GravActivity", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simulator);
        surface = (GravCanvas) this.findViewById(R.id.gravCanvas);
        mThread = surface.getThread();
        lock = (Button) this.findViewById(R.id.lockBttn);
        lock.setOnClickListener(this);
        play = (Button) this.findViewById(R.id.pauseBttn);
        play.setOnClickListener(this);
        quit = (Button) this.findViewById(R.id.quitBttn);
        quit.setOnClickListener(this);/**
        if(savedInstanceState != null) {
        	mThread.restart(savedInstanceState);
    		Log.d("GravActivity", "Restored");
        }*/
    }    

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case (R.id.lockBttn):
			if(mThread.isLocked()) {
				lock.setText("Lock");
				mThread.unlock();
			}
			else {
				lock.setText("Unlock");
				mThread.lock();
			}
			break;
		case (R.id.pauseBttn):
			if(mThread.isPaused()) {
				play.setText("Pause");
				mThread.play();
			}
			else {
				play.setText("Play");
				mThread.pause();
			}
			break;
		case (R.id.quitBttn):
			this.finish();
			break;
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		play.setText("Play");
		lock.setText("Unlock");
		Log.d("GravActivity", "onPause");
		mThread.quit(instanceState);
		flag = true;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		play.setText("Play");
		lock.setText("Lock");
		Log.d("GravActivity", "onResume");
		if(flag == true) {
			mThread.restart(instanceState);
			flag = false;
		}
		
	}
	
	/**
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mThread.quit(outState);
		Log.d("GravActivity.onSaveInstanceState", "Saved");
	}
	
	@Override
	public void onRestoreInstanceState(Bundle inState) {
		super.onRestoreInstanceState(inState);
		mThread.restart(inState);
		Log.d("GravActivity.onRestoreInstanceState", "Restored");
	}
	
	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		if(savedInstanceState != null) {
			mThread.restart(savedInstanceState);
			Log.d("GravActivity.onPostCreate", "Restored");
		}
	}*/
	
	
}