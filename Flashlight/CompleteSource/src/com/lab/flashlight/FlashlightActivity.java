package com.lab.flashlight;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class FlashlightActivity extends Activity implements OnTouchListener{
	
	private Camera camera; // Object for interacting with the camera
	private Parameters camParams; // Object to hold settings for camera
	private boolean isFlashlightOn; // boolean for whether the camera is on or off
	private View mainView; // Object for the main view of the app (you will not edit this)
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Check to see if we have a camera and have permission to use it.
        // If we do, try to get a handle to it so we can use it
        boolean myBool = this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if(myBool == true)
        {
        	try
        	{
        		camera = Camera.open(); // Open the camera so we can use it
        		camParams = camera.getParameters();	// Get current settings of the camera
        	} catch(RuntimeException e)
        	{
        		this.finish(); // We have caught an exception, so we will exit the program
        	}
        	
        	isFlashlightOn = false; // flashlight is off initially
        }
        else // Raise an error window if we don't have access to the camera/ camera with flash not there
        {
        	AlertDialog alert = new AlertDialog.Builder(this).create();
        	alert.setTitle("Oh, NO!!");
        	alert.setMessage("No camera with flash present, or check AndroidManifest.xml to make sure you have permission to use the camera!!!");
        	alert.show();
        }
        
        // You don't need to worry about this code
        // It is responsible for setting up touch screen functionality
        // and setting the background
        mainView = (View)findViewById(R.id.main);
        if(mainView != null)
        {
        	mainView.setOnTouchListener(this);
        	mainView.setBackgroundDrawable(getResources().getDrawable(R.drawable.light_off));
        }
    }
    
    // Turns on the flashlight
    public void turnFlashlightOn()
    {
    	camParams.setFlashMode(Parameters.FLASH_MODE_TORCH);
		camera.setParameters(camParams);
		camera.startPreview();
		
		isFlashlightOn = true;
    }
    
    // Turns off the flashlight
    public void turnFlashlightOff()
    {
    	camParams.setFlashMode(Parameters.FLASH_MODE_OFF);
		camera.setParameters(camParams);
		camera.stopPreview();
		
		isFlashlightOn = false;
    }
    
    // This method checks if the flashlight is on or off.
    // If it is on, it calls the method to turn off the flashlight and
    // changes the background.  Vice versa if the flashlight is off.
    // You just need to focus on making the code for the turnFlashlightOn/Off methods
    // and don't have to add any code into this method.
    public void flashlightClicked(View view)
    {
    	if(isFlashlightOn == true)
    	{
    		turnFlashlightOff(); // Make sure you define the code for this method!
    		
    		// Changes the background to the off bulb
    		mainView.setBackgroundDrawable(getResources().getDrawable(R.drawable.light_off));
    	}
    	else
    	{
    		turnFlashlightOn(); // Make sure you define the code for this method!
    		
    		// Changes the background to the on bulb
    		mainView.setBackgroundDrawable(getResources().getDrawable(R.drawable.light_on));
    	}
    }
    
    // This method is called by the android application when
    // the user touches the screen.  MotionEvent is the event
    // that triggered this method to be called (ex. user touched the screen).
    // If the user is touching down on the screen flashLightClicked method should be called
    public boolean onTouch(View v, MotionEvent event)
    {
    	// If we are just touching down, turn the light on or off
    	if(event.getAction() == MotionEvent.ACTION_DOWN)
    	{
    		flashlightClicked(v);
    	}
    	
    	return true;
    }
}