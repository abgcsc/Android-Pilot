/**************************************************************************
Programmer: Richard Simmons
Date: April 13, 2012
Name: CameraActivity.java
Description: Captures image with android camera. Allows to edit or save.
Version: Complete Source
**************************************************************************/

package image.editor.lab2;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


public class CameraActivity extends Activity {

	//constant used to ensure picture was taken
	private static final int TAKE_PICTURE = 1;
	
	//creates the view for the image
	ImageView imageView; 
	
	//used to call the camera app, and the edit activity
	Intent intent;
	
	//checks to see if pic has been captured
	boolean picCaptured = false;
	
	//used to store image on the imageView
	Bitmap bitmap;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        }
    
    //used to store captured image
    private Uri imageUri;
    
    
    //method to capture image
    public void takePicture(View view) {
    	
        
        //sets the file path to store the captured image
    	String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(0));
        String imageFileName = timeStamp + "_Pic";
    	File photo = new File(Environment.getExternalStorageDirectory(),  imageFileName);
        
        
    	/*STUDENTS CODE
            
         Initialize the intent variable.
         Code the intent.putExtra() method.
         
          STUDENTS CODE*/
        
        
    	//sets imageUri as the photo file path
    	imageUri = Uri.fromFile(photo);
    	
    	//starts the camera
        startActivityForResult(intent, TAKE_PICTURE);
        
    }
    
    
    //called when camera returns image
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
    	
    	//gets the data returned
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	//checks to make sure the result returned is from the camera
    	switch (requestCode) {
        case TAKE_PICTURE:
            
        	//checks to make sure activity succeeded
        	if (resultCode == Activity.RESULT_OK) {
               
        		//gets photo
        		Uri selectedImage = imageUri;
                imageView = (ImageView) findViewById(R.id.imageView1);
                imageView.setDrawingCacheEnabled(true);
                imageView.buildDrawingCache(true);
                
                //draws photo onto the imageView
                try {
                     
                     //used to reduce size of bitmap
                     Options options = new Options();
                     options.inSampleSize = 2;
                     bitmap = BitmapFactory.decodeFile(selectedImage.getPath(), options);

                     
                    //puts the bitmap on the imageView 
                    imageView.setImageBitmap(bitmap);
                    
                    //informs user of file name
                    Toast.makeText(this, selectedImage.toString(),
                            Toast.LENGTH_SHORT).show();
                    
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                            .show();
                    Log.e("Camera", e.toString());
                }
                
                //used to determine if a picture has been captured yet
                picCaptured=true;
                
            }
        	
        }
    }
    
   
    //method to start edit activity
    public void editPicture(View view){
        
    	
    	/* STUDENTS CODE
         
            Initialize the intent variable.
            Set the class the intent needs to start a new activity.
            Pass the picture to the intent to send to editActivity.
            Start the intent if picCaptured is set to true.
         
           STUDENTS CODE*/
    	
    	
    }
    
    
    //method to save captured picture to gallery
    public void savePicture(View view){
    	
    	MediaStore.Images.Media.insertImage(getContentResolver(), imageView.getDrawingCache(true), imageUri.getPath(), "app_picture");
		Toast.makeText(this, "Image Save to Gallery", Toast.LENGTH_SHORT)
        .show();
		imageView.setDrawingCacheEnabled(false);
    	
    }
    
    
    //method to exit
    public void exit(View view){
    	
    	this.finish();
    	
    }
}