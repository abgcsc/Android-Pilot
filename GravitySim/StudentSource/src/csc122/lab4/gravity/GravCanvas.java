package csc122.lab4.gravity;

import csc122.lab4.gravity.Planetoid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

/**
 * The GravCanvas class is a custom view element that is capable of running an interactive gravity simulator.
 * @author 
 *
 */
@SuppressWarnings("unused")
public class GravCanvas extends SurfaceView implements SurfaceHolder.Callback {
	
	// state of the program
	public enum PState {
		RUNNING, PAUSED
	}
	
	// state of the gravity simulator for touch events
	enum GState {
		UNLOCKED, LOCKED
	}
	
	/**
	 * STUDENT: When adding functions to this class, be sure to put the body of the function inside of the following 
	 * form:
	 * 
	 * synchronized (mSurfaceHolder) {  
	 * 		//your code goes here//  
	 * }
	 * 
	 * Since functions in this class may run on different threads, this is necessary to prevent concurrent modifications.
	 * For example, you may try to delete a Planetoid while it is being used by something else.
	 * Concurrent modifications will cause your program to crash.
	 * @author 
	 *
	 */
	class GravThread extends Thread {
		// STUDENT: use this Arraylist to store any Planetoids you create
		private ArrayList<Planetoid> planetoids; //  the planetoids with associated image, location, and velocity
		
		// STUDENT: your Planetoids should get their Drawable images from this ArrayList
		private ArrayList<Drawable> planets; // the planet images
		
		// the selected planet, if any; selected planets exert gravity on others but do not themselves move
		private Planetoid selected; // drawn in place of a selected planet's normal image
		
		private Bitmap background; // the background image
		
		// STUDENT: initialize this variable to a nonzero value
		private static final double GRAVITATIONAL_CONST = 0; 
				
		private SurfaceHolder mSurfaceHolder; // holds the surface upon which things are drawn
		
		private Handler mHandler; // does something Android-y
		
		private Context mContext; // same as above
		
		private PState running = PState.PAUSED; // whether the thread is running
		
		private boolean isRunning = false; // whether the thread is playing or paused
		
		private GState locked = GState.UNLOCKED; // whether the screen is locked from touch events
		
		private Random mRandom = new Random (); // random number generator..why is it here?
		
		long previous; // starting time of previous frame
		
		// these variables are used to scale the Canvas drawing coordinates to the screen
		private float xViewScale;
		private float yViewScale;
		private float xPivot;
		private float yPivot;
		
		/**
		 * Construct a GravThread.
		 * @param surfaceHolder
		 * @param context
		 * @param handler
		 */
		public GravThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
			mSurfaceHolder = surfaceHolder;
			mContext = context;
			mHandler = handler;
			
			planetoids = new ArrayList<Planetoid> ();
			planets = new ArrayList<Drawable> ();
			
			/**
			 * STUDENT: Image resources are loaded here. Images are found in /res/drawable and must be
			 * imported into the project. Their names must be lowercase. planet1, selected, and background
			 * or your own substitutes are required images. You are free to add more, but be mindful of
			 * memory usage.
			 */
			Resources res = context.getResources();
			planets.add(res.getDrawable(R.drawable.planet1));
			selected = new Planetoid(res.getDrawable(R.drawable.selected));
			background = BitmapFactory.decodeResource(res, R.drawable.background);
			
			//STUDENT: example construction of hard-coded planet for testing
			//planetoids.add(new Planetoid (planets.get(0), 10, 10));
			//planetoids.get(0).setDiameter(100);
			//planetoids.get(0).setMass(74);
			
			running = PState.RUNNING; // the thread is running
			isRunning = false; // but it isn't playing
			previous = System.currentTimeMillis()+100; // give a little bit of time to setup
			Log.d("GravThread", "New thread");
		}
		
		/**
		 * Copy over the data from the other GravThread, which has presumably already been run (and cannot be restarted)
		 * @param other The other GravThread whose contents we want (its planetoids).
		 */
		public void clone(GravThread other) {
			planetoids = other.planetoids;
		}
				
		/**
		 * Is the View (GravCanvas) locked from touch events?
		 * @return The status of the View's lock.
		 */
		public boolean isLocked() {
			if(locked == GState.LOCKED) {
				return true;
			}
			return false;
		}
		
		/**
		 * Lock the View from touch events.
		 */
		public void lock() {
			locked = GState.LOCKED;
		}
		
		/**
		 * Unlock the View to touch events.
		 */
		public void unlock() {
			locked = GState.UNLOCKED;
		}
		
		/**
		 * Is the gravitational interaction and planetoid movement paused?
		 * @return
		 */
		public boolean isPaused() {
			return !isRunning;
		}
		
		/**
		 * Pause all automatic movement.
		 */
		public void pause() {
			isRunning = false;
		}
		
		/**
		 * Resume automatic movement.
		 */
		public void play() {
			isRunning = true;
			previous = System.currentTimeMillis()+100;
		}
		
		public void setRunning(boolean state)
		{
			if(state == true) {
				running = PState.RUNNING;
			}
			else {
				running = PState.PAUSED;
			}
		}
		
		/**
		 * Idle callback (effectively) for the GravThread.
		 * Makes repeated calls to draw and calculate gravitation as often as possible.
		 */
		@Override
		public void run(){
			Log.d("GravThread", "Started running");
			while (running == PState.RUNNING) {
				Canvas canvas = null;
				try {
					canvas = mSurfaceHolder.lockCanvas(null);
					synchronized (mSurfaceHolder) {
						gravitate();
						draw(canvas);
					}
				} finally {
					if(canvas != null) {
						mSurfaceHolder.unlockCanvasAndPost(canvas);
					}
				}
			}
			Log.d("GravThread", "Finished running");
		}
		
		/**
		 * Stop running, save the state into the given Bundle.
		 * Pauses and locks the screen.
		 * @param map
		 * @return
		 */
		public Bundle quit(Bundle map) {
			synchronized (mSurfaceHolder) {
				map.putParcelableArrayList("Planetoids", planetoids);
				pause();
				lock();
				Log.d("GravThread.quit", "Quit");
			}
			return map;
		}
		
		/**
		 * Restores a previous thread state.
		 * Unlocks the screen, but does not affect playing/pausing animation.
		 * @param savedState
		 */
		public void restart(Bundle savedState) {
			synchronized (mSurfaceHolder) {
				ArrayList<Planetoid> savedPlanetoids = (ArrayList<Planetoid>) savedState.getParcelableArrayList("Planetoids").clone();
				planetoids = savedPlanetoids;
				unlock();
				Log.d("GravThread.restart", "Restarted");
			}
		}
		
		/**
		 * Updates the locations of all of the planetoids and their respective velocities.
		 */
		private void gravitate(){
			// STUDENT: use these time variables to determine how much time has elapsed since the last round of interaction
			long now = System.currentTimeMillis();
			
			if(previous > now) return; // cannot move in the past
			
			/**
			 * STUDENT: 
			 * This is where you should calculate gravitational interactions,
			 * apply forces to each Planetoid,
			 * move them,
			 * and check for/resolve collisions.
			 */
		}
				
		/**
		 * Draw the scene, including background and all planetoids.
		 * @param canvas 	The canvas on which the scene will be drawn.
		 */
		private void draw(Canvas canvas) {
			try {
				Bitmap temp = Bitmap.createScaledBitmap(background, canvas.getWidth(), canvas.getHeight(), true);
				canvas.drawBitmap(temp, 0, 0, null);
				for(Planetoid p : planetoids) {
					drawPlanetoid(p, canvas);
				}
			} catch (NullPointerException ne) {
				Log.e("GravThread.draw", "NullPointerException");
			}
		}
		
		/**
		 * Draws the given planetoid on the given canvas at the correct size and location.
		 * @param given 	The planetoid to be drawn.
		 * @param canvas	The canvas on which the planetoid will be drawn.
		 */
		public void drawPlanetoid(Planetoid given, Canvas canvas){
			Bitmap temp;
			if(!given.isSelected()) {
				temp = Bitmap.createScaledBitmap(((BitmapDrawable) given.getImage()).getBitmap(), (int) given.getDiameter(), (int) given.getDiameter(), true);		
			}
			else {
				temp = Bitmap.createScaledBitmap(((BitmapDrawable) selected.getImage()).getBitmap(), (int) given.getDiameter(), (int) given.getDiameter(), true);
			}
			canvas.save();
			canvas.scale(xViewScale/canvas.getWidth(), yViewScale/canvas.getHeight(), xPivot, yPivot);
			canvas.drawBitmap(temp, (float) (given.getX()-given.getDiameter()/2.0), (float) (given.getY()-given.getDiameter()/2.0), null);
			/**
			 * STUDENT:
			 * Here is an example for drawing a solid gray circle instead of the Planetoid image. See the Color class for other colors.
			 * Paint color = new Paint ();
			 * color.setColor(Color.GRAY);
			 * canvas.drawCircle((float) given.getX(), (float) given.getY(), (float) (given.getDiameter()/2.0), color);
			 */
			//Log.d("GravThread.drawPlanetoid", "Planetoid drawn");
		}
		
		/**
		 * Sets scaling factors so that the GravThread can draw to the correct coordinates in the View.
		 * @param xScale
		 * @param yScale
		 * @param px
		 * @param py
		 */
		public void setScalingFactors(float xScale, float yScale, float px, float py) {
			synchronized (mSurfaceHolder) {
				xViewScale = xScale;
				yViewScale = yScale;
				xPivot = px;
				yPivot = py;
			}
		}
		
		/**
		 * STUDENT: A function that handles touch events should go somewhere before that next bracket.
		 */		
	} // this one
	
	private Context mContext; // it does something
	
	private TextView status; // not really necessary
	
	private GravThread mThread; // the local thread, controls animation. very necessary
	
	private GravitySimActivity mActivity; // the activity holding this GravCanvas. see surfaceCreated() for more details
	
	/**
	 * Default constructor, effectively. Not likely to be called.
	 * @param context
	 */
	public GravCanvas(Context context) {
		super(context);
		construct(context);
	}
	
	/**
	 * Constructor necessary to function as a View.
	 * @param context
	 * @param attributeSet
	 */
	public GravCanvas(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		construct(context);
	}
	
	/**
	 * Another constructor necessary to function as a View.
	 * @param context
	 * @param attributeSet
	 * @param defStyle
	 */
	public GravCanvas(Context context, AttributeSet attributeSet, int defStyle) {
		super(context, attributeSet, defStyle);
		construct(context);
		
	}

	/**
	 * Performs actions necessary for initialization, such as creation of the inner GravThread.
	 * @param context
	 */
	public void construct(Context context) {
		SurfaceHolder holder = getHolder();
		mContext = context;
		holder.addCallback(this);
		mThread = new GravThread (holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {
                status.setVisibility(m.getData().getInt("viz"));
                status.setText(m.getData().getString("text"));
            }
        });
        setFocusable(true); // make sure we get key events
	}
	
	/**
	 * Get this GravCanvas' inner GravThread.
	 * @return
	 */
	public GravThread getThread() {
		return mThread;
	}
	
	/**
	 * Sets the activity that is holding this GravCanvas in a layout.
	 * @param activity The activity calling this method.
	 */
	public void setActivity(GravitySimActivity activity) {
		mActivity = activity;
	}
	
	/**
	 * The screen was rotated. 
	 * STUDENT: It is recommended that you disable screen rotations on your phone if this 
	 * function ever gets called, as it may cause display problems.
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		mThread.setScalingFactors((float) this.getWidth(), (float) this.getHeight(), 0, 0);
	}

	/**
	 * Create the surface upon which things will be drawn and the user will touch.
	 * It may also be required to restart the thread when the screen turns off and back on
	 * or in various other scenarios.
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.d("GravCanvas", "surfaceCreated");
		boolean retry = true;
		while(retry) {
			try {
				mThread.start(); // try to start the thread
				retry = false;
			} catch (IllegalThreadStateException e) { // the thread had already been started, cannot be started again
				GravThread old = mThread;
				mThread = new GravThread(this.getHolder(), mContext, new Handler() {
		            @Override
		            public void handleMessage(Message m) {
		                status.setVisibility(m.getData().getInt("viz"));
		                status.setText(m.getData().getString("text"));
		            }
		        });
				mThread.clone(old);
				mActivity.setThread(mThread); // update the activity to the new thread
			}
		}
	}
	
	/**
	 * Terminate thread, cannot interact with surface again.
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
        while (retry) {
            try {
            	mThread.setRunning(false);
                mThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
        Log.d("GravCanvas", "surfaceDestroyed");
	}

	/**
	 * Callback for when this View is touched by the user.
	 * @return	True if the motion event was handled (consumed), false otherwise (same touch event will arrive next time).
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d("GravCanvas.onTouchEvent", "Touch event recorded");
		/**
		 * STUDENT:
		 * Here is where you should handle touch events.
		 * Necessary variables should be dispatched to the local GravThread
		 * in a custom function that handles touch events.
		 * The VelocityTracker should do its approximations here with each
		 * new event.
		 */
		return false; // change this to true if you want to get the next MotionEvent.
	}
	
}