package csc120.lab6.Tuner;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class inverseSeekBar extends ProgressBar {
	
	public inverseSeekBar(Context context, AttributeSet attrs, int defStyle) {
	   super(context, attrs, defStyle);
	   // TODO Auto-generated constructor stub
	}
	
	public inverseSeekBar(Context context, AttributeSet attrs) {
	   super(context, attrs);
	   // TODO Auto-generated constructor stub
	}
	
	public inverseSeekBar(Context context) {
	   super(context);
	   // TODO Auto-generated constructor stub
	}
	
	@Override
	protected synchronized void onDraw(Canvas canvas) {
	   // TODO Auto-generated method stub
	   canvas.save(); 
       float py = this.getHeight()/2.0f;
       float px = this.getWidth()/2.0f;
       canvas.rotate(180, px, py); 

       //draw the text with the matrix applied. 
       super.onDraw(canvas); 

       //restore the old matrix. 
       canvas.restore(); 
	}
}


