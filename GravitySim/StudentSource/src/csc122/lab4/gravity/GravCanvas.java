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
	
	class GravThread extends Thread {
		
		private ArrayList<Planetoid> planetoids; //  the planetoids with associated image, location, and velocity
		
		private ArrayList<Drawable> planets; // the planet images
		
		private Planetoid selected; // the selected planet, if any; selected planets exert gravity on others but do not themselves move
		
		private Bitmap background; // the background image
		
		// STUDENT: initialize this variable to a nonzero value
		private static final double GRAVITATIONAL_CONST = 0; 
				
		private SurfaceHolder mSurfaceHolder; // holds the surface upon which things are drawn
		
		private Handler mHandler; // does something Android-y
		
		private Context mContext;
		
		private PState running = PState.PAUSED;
		
		private boolean isRunning = false; // whether the thread is playing or paused
		
		private GState locked = GState.UNLOCKED;
		
		private Random mRandom = new Random ();
		
		long previous; // starting time of previous frame
		
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
			
			Resources res = context.getResources();
			planets.add(res.getDrawable(R.drawable.planet1));
			selected = new Planetoid(res.getDrawable(R.drawable.selected));
			background = BitmapFactory.decodeResource(res, R.drawable.background);
			
			//STUDENT: example construction of hard-coded planet for testing
			//planetoids.add(new Planetoid (planets.get(0), 10, 10));
			//planetoids.get(0).setDiameter(100);
			//planetoids.get(0).setMass(74);
			
			running = PState.RUNNING;
			isRunning = false;
			previous = System.currentTimeMillis()+100;
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
				//setRunning(false);
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
				//setRunning(true);
				Log.d("GravThread.restart", "Restarted");
			}
		}
		
		/**
		 * Updates the locations of all of the planetoids and their respective velocities.
		 */
		private void gravitate(){
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
				//temp =((BitmapDrawable) given.getImage()).getBitmap();			
			}
			else {
				temp = Bitmap.createScaledBitmap(((BitmapDrawable) selected.getImage()).getBitmap(), (int) given.getDiameter(), (int) given.getDiameter(), true);
				//temp =((BitmapDrawable) selected.getImage()).getBitmap();
			}
			//Drawable tempDraw = new BitmapDrawable(Bitmap.createScaledBitmap(temp, (int) given.getDiameter(), (int) given.getDiameter(), true));
			canvas.save();
			canvas.scale(xViewScale/canvas.getWidth(), yViewScale/canvas.getHeight(), xPivot, yPivot);
			canvas.drawBitmap(temp, (float) (given.getX()-given.getDiameter()/2.0), (float) (given.getY()-given.getDiameter()/2.0), null);
			/**
			 * STUDENT:
			 * Here is an example for drawing a solid gray circle instead of the Planetoid image, if you desire different colors.
			 * Paint color = new Paint ();
			 * color.setColor(Color.GRAY);
			 * canvas.drawCircle((float) given.getX(), (float) given.getY(), (float) (given.getDiameter()/2.0), color);
			 */
			
			//Log.d("GravThread.drawPlanetoid", "Planetoid drawn");
		}
		
		/**
		 * Sets scaling factors so that the GravThread can draw to the correct coordinates in the view.
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
		
	}
	
	private Context mContext;
	
	private TextView status;
	
	private GravThread mThread;
	
	private GravitySimActivity mActivity;
	
	private VelocityTracker mTracker = VelocityTracker.obtain();
	
	private int pointerCount;
	
	public GravCanvas(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public GravCanvas(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		construct(context);
	}
	
	public GravCanvas(Context context, AttributeSet attributeSet, int defStyle) {
		super(context, attributeSet, defStyle);
		construct(context);
		
	}

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
		//mThread.setScalingFactors((float)this.getWidth()*this.getScaleX(), (float)this.getHeight()*this.getScaleY(), this.getPivotX(), this.getPivotY());
        setFocusable(true); // make sure we get key events
	}
	
	public GravThread getThread() {
		return mThread;
	}
	
	public void setActivity(GravitySimActivity activity) {
		mActivity = activity;
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		//mThread.setScalingFactors((float)this.getWidth()*this.getScaleX(), (float)this.getHeight()*this.getScaleY(), this.getPivotX(), this.getPivotY());
		mThread.setScalingFactors((float) this.getWidth(), (float) this.getHeight(), 0, 0);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.d("GravCanvas", "surfaceCreated");
		boolean retry = true;
		while(retry) {
			try {
				mThread.start();
				retry = false;
			} catch (IllegalThreadStateException e) {
				GravThread old = mThread;
				mThread = new GravThread(this.getHolder(), mContext, new Handler() {
		            @Override
		            public void handleMessage(Message m) {
		                status.setVisibility(m.getData().getInt("viz"));
		                status.setText(m.getData().getString("text"));
		            }
		        });
				mThread.clone(old);
				mActivity.setThread(mThread);
			}
		}
	}
	
	/**
	 * Terminate thread, cannot interact with surface again
	 *
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
		return false;
	}
	
}