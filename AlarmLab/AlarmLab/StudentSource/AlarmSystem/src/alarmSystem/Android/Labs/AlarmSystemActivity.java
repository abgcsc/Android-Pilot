package alarmSystem.Android.Labs;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.RadioButton;

//STUDENTS CODE
//import libraries needed for media playback/recording



public class AlarmSystemActivity extends Activity {
    /** Called when the activity is first created. */
   
	//STUDENTS CODE
	//creates media class instances
	
	
	//creates radio buttons
	RadioButton sound1, sound2, sound3;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
       
        //STUDENTS CODE
        //initialize media class instances
        
        
        //connects radio buttons to the three different sounds
        sound1 = (RadioButton)findViewById(R.id.sound1RB);
        sound2 = (RadioButton)findViewById(R.id.sound2RB);
        sound3 = (RadioButton)findViewById(R.id.sound3RB);
        
        //sets default radio button to button 1
        sound1.setChecked(true);
        
    }
    
    
    
    public void listen() throws IOException {
    	
    	//sets the file path needed to store recorded file
    	File sampleDir = Environment.getExternalStorageDirectory();
    	File audiofile = null;
    	audiofile = File.createTempFile("temp", ".3gp", sampleDir);
    	
    	//STUDENTS CODE
    	//set all required recording functions
    	
    	
    	//STUDENTS CODE
    	//start recording
    	
    	
    	//STUDENTS CODE - this could be a cool problem - how to check for sound using getMaxAmplitdue
    	//STUDENTS CODE - also, trying to decide what should be the threshold (3000 in this case)
    	//check for sound and sound alarm accordingly
    
    		
    	
    	
    	
   	
    }
    
    public void activate(View view) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException, InterruptedException {
    	
    	//asks user if they really want to activate alarm
    	final AlertDialog alert = new AlertDialog.Builder(this).create();
    	alert.setTitle("Are you sure you want to activate?");
    	alert.setButton("Yes", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				
				try {
					//stops alarm if already sounding
					if(mp.isPlaying()==true){
			    		mp.stop();
						mp.prepare();
					}
					//calls function to listen
					listen();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			}
		});
    	alert.setButton2("Cancel", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				
				return;
				
			}
		});
    	//displays alert dialog
    	alert.show();
    	
    	
    }
    
    public void soundAlarm(View view)throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {
    	
    	//STUDENTS CODE
    	//sound alarm
    	
    	
    }
    
    public void setSound(View view)throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {
    	
    	//creates media player instances with the sound files
    	if(sound1.isChecked()==true){
    		mp.reset();
    		mp = MediaPlayer.create(this, R.raw.caralarm1);
    	}
    	if(sound2.isChecked()==true){
    		mp.reset();
    		mp = MediaPlayer.create(this, R.raw.beeperalarm);
    	}
    	if(sound3.isChecked()==true){
    		mp.reset();
    		mp = MediaPlayer.create(this, R.raw.alarm);
    	}
    }
    
}

