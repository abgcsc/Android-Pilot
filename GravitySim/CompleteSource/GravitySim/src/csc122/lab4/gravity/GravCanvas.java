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
		
		private ArrayList<Drawable> collapsers; // images of ordered stages of collapse into a black hole
		
		private Drawable blackHole; // image of a black hole 
		
		private Planetoid selected; // the selected planet, if any; selected planets exert gravity on others but do not themselves move
		
		private Bitmap background;
		
		private static final double GRAVITATIONAL_CONST = 1000;
		
		private HashMap<Integer, Planetoid> motions; // used to keep track of successive motion events
		
		private SurfaceHolder mSurfaceHolder;
		
		private Handler mHandler;
		
		private PState running = PState.PAUSED;
		
		private boolean isRunning = false;
		
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
			collapsers = new ArrayList<Drawable> ();
			motions = new HashMap<Integer, Planetoid> ();
			
			Resources res = context.getResources();
			planets.add(res.getDrawable(R.drawable.planet1));
			//planets.add(res.getDrawable(R.drawable.planet2));
			//planets.add(res.getDrawable(R.drawable.planet3));
			//collapsers.add(res.getDrawable(R.drawable.collapse1));
			//collapsers.add(res.getDrawable(R.drawable.collapse2));
			selected = new Planetoid(res.getDrawable(R.drawable.selected));
			background = BitmapFactory.decodeResource(res, R.drawable.background);
			
			//planetoids.add(new Planetoid (planets.get(0), 10, 10));
			//planetoids.get(0).setDiameter(100);
			//planetoids.get(0).setMass(74);
			
			//planetoids.add(new Planetoid (planets.get(0), 120, 200));
			//planetoids.get(1).setDiameter(75);
			//planetoids.get(1).setMass(77);
			
			//planetoids.add(new Planetoid (planets.get(0), 200, 300));
			//planetoids.get(2).setDiameter(32);
			//planetoids.get(2).setMass(31);
			
			running = PState.RUNNING;
			isRunning = false;
			previous = System.currentTimeMillis()+100;
			Log.d("GravThread", "New thread");
		}
		
		/**
		 * Is the View (GravCanvas) locked from touch events?
		 * @return
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
			onTouch(0, 0, 0, 0, 0, MotionEvent.ACTION_CANCEL);
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
			
			double timeLapse = ((double) now - (double) previous) / (double) 1000; // number of seconds elapsed since last frame
			previous = now;
			if(isRunning) {
				int size = planetoids.size();
				for (int i = 0; i < size; i++) {
					Planetoid p = planetoids.get(i);
					// apply forces exerted on and by planetoid p
					for(int j = i+1; j < size; j++) {
						applyGravity(p, planetoids.get(j), timeLapse);
					}
					// move planetoid p
					p.move();
					// check for collisions
					for(int k = 0; k < i; k++) {
						Planetoid o = planetoids.get(k);
						if(p.getDistanceTo(o) < (p.getDiameter()+o.getDiameter())/2) { // collision detected, merge planetoids
							//if(o.isSelected()) {
								o.merge(p);
								planetoids.remove(i);
								size--;
								i--;
								break;
							/**}
							else {
								p.merge(o);
								p3.remove();
							}*/
						}
					}
				}
			}
		}
		
		/**
		 * Applies gravitational force between two given planetoids.
		 * @param p1
		 * @param p2
		 */
		private void applyGravity(Planetoid p1, Planetoid p2, double timeLapse) {
			double rSquared = p1.getSDistanceTo(p2);
			// get direction components of force, initially directed at p2
			double dx = p2.getX() - p1.getX();
			double dy = p2.getY() - p1.getY();
			double Fmag = GRAVITATIONAL_CONST*p1.getMass()*p2.getMass()/rSquared;
			// get components of the force, initially directed at p2
			double fx = 0;
			double fy = Fmag;
			if(dx != 0) {
				double angle = Math.atan(dy/dx);
				if((dx < 0 && dy > 0) || (dx < 0 && dy < 0)) {
					angle+= Math.PI;
				}
				fx = Fmag*Math.cos(angle);
				fy = Fmag*Math.sin(angle);
				//assert(fx*dx > 0 && fy*dy > 0);
			}
			// apply the forces
			p2.applyForce(-fx, -fy, timeLapse);
			p1.applyForce(fx, fy, timeLapse);
		}
		
		/**
		 * Draw the scene, including background and all planetoids.
		 * @param canvas 	The canvas on which the scene will be drawn.
		 */
		private void draw(Canvas canvas) {
			try {
				canvas.drawBitmap(background, 0, 0, null);
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
			//Paint color = new Paint ();
			//color.setColor(Color.GRAY);
			//canvas.drawCircle((float) given.getX(), (float) given.getY(), (float) (given.getDiameter()/2.0), color);
			/**
			tempDraw.setBounds((int) (given.getX()- given.getDiameter()/2), 
					(int) (given.getY() + given.getDiameter()/2), 
					(int) (given.getY()+given.getDiameter()/2), 
					(int) (given.getX()- given.getDiameter()/2));
			tempDraw.draw(canvas);
			canvas.restore();*/
			//Log.d("GravThread.drawPlanetoid", "Planetoid drawn");
		}
		
		public void setScalingFactors(float xScale, float yScale, float px, float py) {
			synchronized (mSurfaceHolder) {
				xViewScale = xScale;
				yViewScale = yScale;
				xPivot = px;
				yPivot = py;
			}
		}
		
		/**
		 * Applies the effects of a user's touch on the screen.
		 * @param pointerId	The id of the motion event.
		 * @param x			The x location of the motion
		 * @param y 		The y location of the motion
		 * @param dx 		The x velocity of the motion.
		 * @param dy		The y velocity of the motion.
		 * @param action	Special property of the motion, indicator of cancellation or ending.
		 */
		public void onTouch(int pointerId, double x, double y, double dx, double dy, int action) {
			if(!isLocked()) {
				synchronized (mSurfaceHolder) {
					if(action != MotionEvent.ACTION_CANCEL) {
						boolean touchedExisting = false;
						if(motions.containsKey(pointerId) == false) { // new motion
							Planetoid temp = new Planetoid (null, x, y);
							for(Planetoid p : planetoids) {
								if(p.getDistanceTo(temp) < p.getDiameter()/2) {
									touchedExisting = true;
									p.select();
									motions.put(pointerId, p);
									p.setVelocity(0, 0);
									break;
								}
							}
							if(!touchedExisting) { // new touch at an unoccupied spot, create new planetoid
								Log.d("GravThread.onTouch", "New planetoid created");
								Planetoid n = new Planetoid (planets.get(mRandom.nextInt() % planets.size()), x, y);
								n.select();
								planetoids.add(n);
								motions.put(pointerId, n);
							}
						}
						motions.get(pointerId).setPosition(x, y);
						if(!touchedExisting) {
							motions.get(pointerId).setDiameter(motions.get(pointerId).getDiameter()+0.5);
							motions.get(pointerId).setMass(motions.get(pointerId).getMass()+0.5);
						}
						if(action == MotionEvent.ACTION_UP) {
							motions.get(pointerId).setMomentum(5*dx, 5*dy);
						}
					}
					else {
						clearMotions();
					}
				}
			}
		}
		
		/**
		 * Clear the motion pointers and selected planetoids that we have been tracking.
		 */
		public void clearMotions() {
			try {
				synchronized (mSurfaceHolder) {
					for(int i = 0; i < motions.size(); i++) {
						motions.get(i).deselect();
					}
				}
			}
			catch (NullPointerException e) {
			}
			motions.clear();
			//motions = new HashMap<Integer, Planetoid> ();
		}
	}
	
	private Context mContext;
	
	private TextView status;
	
	private GravThread mThread;
	
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
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		//mThread.setScalingFactors((float)this.getWidth()*this.getScaleX(), (float)this.getHeight()*this.getScaleY(), this.getPivotX(), this.getPivotY());
		mThread.setScalingFactors((float) this.getWidth(), (float) this.getHeight(), 0, 0);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		mThread.start();
		Log.d("GravCanvas", "surfaceCreated");
	}
	
	/**
	 * 
	@Override
	public void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		if(gainFocus == false) {
			mThread.pause();
		}
	}*/

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
	 * 
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d("GravCanvas.onTouchEvent", "Touch event recorded");
		pointerCount = event.getPointerCount();
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			mTracker.clear();
			mThread.clearMotions();
		}
		mTracker.addMovement(event);
		mTracker.computeCurrentVelocity(1000, 50);
		for(int i = 0; i < pointerCount; i++) {
			mThread.onTouch(event.getPointerId(i), event.getX(i), event.getY(i), 
					mTracker.getXVelocity(event.getPointerId(i)), 
					mTracker.getYVelocity(event.getPointerId(i)),
					event.getAction());
		}
		if(event.getAction() == MotionEvent.ACTION_UP) {
			mTracker.clear();
			mThread.clearMotions();
		}
		return true;
	}
	
}