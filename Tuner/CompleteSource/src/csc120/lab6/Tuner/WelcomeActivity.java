package csc120.lab6.Tuner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class WelcomeActivity extends Activity implements OnClickListener {

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent start = new Intent(this, TunerActivity.class);
		this.startActivity(start);
	}
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.welcome);
		Button start = (Button) this.findViewById(R.id.startBttn);
		start.setOnClickListener(this);
	}
}
