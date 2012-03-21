package csc122.lab4.gravity;

import csc122.lab4.gravity.GravCanvas;
import csc122.lab4.gravity.GravCanvas.GravThread;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GravitySimActivity extends Activity implements OnClickListener {
	private GravThread mThread;
	private GravCanvas surface;
	private Button lock;
	private Button play;
	private Button quit;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simulator);
        surface = (GravCanvas) this.findViewById(R.id.gravCanvas);
        mThread = surface.getThread();
        lock = (Button) this.findViewById(R.id.lockBttn);
        lock.setOnClickListener(this);
        play = (Button) this.findViewById(R.id.pauseBttn);
        play.setOnClickListener(this);
        quit = (Button) this.findViewById(R.id.quitBttn);
        quit.setOnClickListener(this);
        if(savedInstanceState != null) {
        	mThread.restart(savedInstanceState);
        }
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
		mThread.pause();
		play.setText("Play");
		mThread.setRunning(false);
	}
	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mThread.quit(outState);
	}
	
	
}