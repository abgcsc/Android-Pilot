    /**************************************************************************
Programmer: Richard Simmons
Date: April 13, 2012
Name: EditActivity.java
Description: Edits an image passed from CameraActivity.java. Allows to save
image to gallery. Then return to CameraActivity.
Version: Complete Source
**************************************************************************/
package image.editor.lab2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;


public class EditActivity extends Activity {
	
	//used for file path, bitmap, and imageView of the image passed in
	Uri imageUri;
	Bitmap bitmap;
	ImageView imageView = null;
	
	//used for the editing radio buttons
	RadioButton invert, bandw, sepia;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
     // Set the layout for this activity
        setContentView(R.layout.edit);
        
        //sets the radio buttons to their id
        invert = (RadioButton) findViewById(R.id.invert);
        sepia = (RadioButton) findViewById(R.id.sepia);
        bandw = (RadioButton) findViewById(R.id.bandw);
        
        
        /* STUDENTS CODE
         
            Store the retrieved picture into imageUri.
         
           STUDENTS CODE*/
        
        
        Uri selectedImage = imageUri;
        imageView = (ImageView) findViewById(R.id.imageView1);
        
        //enables caching of image in the imageView to keep track of last edited image
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
            
            //informs the user picture loaded successfully
            Toast.makeText(this, "Picture Loaded",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                    .show();
            Log.e("Camera", e.toString());
        }
       
        
    }
	
	//method used to set image to sepia filter
	public void setSepia(View view){
		
		sepia.setChecked(true);
        
		//matrices needed to convert to sepia
        final ColorMatrix matrixA = new ColorMatrix();
        final ColorMatrix matrixB = new ColorMatrix();
        
        //makes image B&W
        matrixA.setSaturation(0);

        //applies scales for RGB color values
        matrixB.setScale(1f, .95f, .82f, 1.0f);
        
        //combines to above matrices to make sepia
        matrixA.setConcat(matrixB, matrixA);

        //creates the filter
        final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrixA);
        
        //sets the filter on the imageView
        imageView.setColorFilter(filter);
        
		
	}
	
	public void setBandW(View view){
		
		bandw.setChecked(true);
		
		//matrix needed to convert to B&W
		final ColorMatrix matrixA = new ColorMatrix();
		
		//makes image B&W
		matrixA.setSaturation(0);
		
		//creates the filter
		final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrixA);
		
		//sets the filter on the imageView
		imageView.setColorFilter(filter);
		
	}
	
	public void setInvert(View view){
		
		invert.setChecked(true);
		
		//matrix values needed to invert colors
		float[] mx = { 
				 0.0f,  0.5f,  0.5f,  0.0f,  0.0f, 
				 0.5f,  0.0f,  0.5f,  0.0f,  0.0f, 
				 0.5f,  0.5f,  0.0f,  0.0f,  0.0f, 
				 0.0f,  0.0f,  0.0f,  1.0f,  0.0f 
				}; 
		
		
		//creates matrix needed to invert colors
		final ColorMatrix matrixA = new ColorMatrix(mx);
		
		//creates the filter
		final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrixA);
		
		//sets the filter on the imageView
		imageView.setColorFilter(filter);
		
	}
	
	
	//method used to call the rotate function
	public void rotate(View view){
	
		//calls rotate function, which returns the rotated bitmap
		bitmap=rotateImage(bitmap,90);
		
		//puts the rotated bitmap onto the imageView
		imageView.setImageBitmap(bitmap);

	}
	
	
	//method used to rotate bitmap
	public static Bitmap rotateImage(Bitmap src, float degree){
		
		//creates matrix needed to rotate bitmap
		Matrix matrix = new Matrix();
		
		//rotates to specified degree
		matrix.postRotate(degree);
		
		//creates and returns rotated bitmap
		return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
		
	}
	
	
	//reverts back to original image
	public void revert(View view){
		
		//removes color filter
		imageView.setColorFilter(null);
		sepia.setChecked(false);
		invert.setChecked(false);
		bandw.setChecked(false);
		
		
	}
	
	
	//method to save edited picture to gallery and return
	public void save(View view){
		
		MediaStore.Images.Media.insertImage(getContentResolver(), imageView.getDrawingCache(true), imageUri.getPath(), "app_picture");
		Toast.makeText(this, "Image Saved to Gallery", Toast.LENGTH_SHORT)
        .show();
		imageView.setDrawingCacheEnabled(false);
		this.finish();
		
	}
	
	
	//recycles bitmap when activity finishes
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	 
	    bitmap.recycle();
	    System.gc();
	}
	
	
	
}