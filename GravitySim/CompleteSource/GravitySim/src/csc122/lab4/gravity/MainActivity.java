package csc122.lab4.gravity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		Button start = (Button) this.findViewById(R.id.startBttn);
		start.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent sim = new Intent (this, GravitySimActivity.class);
		this.startActivity(sim);		
	}
}
