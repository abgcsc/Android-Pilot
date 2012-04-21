package csc122.lab4.gravity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * STUDENT: This class mainly just provides breathing room if the user doesn't want to worry 
 * about hitting a button or planet. In addition, it allows you to reset the simulator if you 
 * back out of GravitySimActivity.
 * @author 
 *
 */
public class MainActivity extends Activity implements OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		Button start = (Button) this.findViewById(R.id.startBttn);
		start.setOnClickListener(this);
	}

	/**
	 * Start the Gravity Simulator activity.
	 */
	@Override
	public void onClick(View v) {
		Intent sim = new Intent (this, GravitySimActivity.class);
		this.startActivity(sim);		
	}
}
