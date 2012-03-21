package csc122.lab4.gravity;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable; 

public class Planetoid implements Parcelable{
	private Drawable image; // the image of the planetoid
	private double diameter = 2; // the size
	private double mass = 2; // the mass
	private double x = 0; // the x position
	private double y = 0; // the y position
	private double dx = 0; // the x velocity
	private double idx = 0; // initial (previous) x velocity
	private double dy = 0; // the y velocity
	private double idy = 0; // initial (previous) y velocity
	private double elapsed = 0;
	private boolean isSelected = false;
	
	public Planetoid (Drawable image)
	{
		this.image = image;				
	}
	
	public Planetoid (Drawable image, double ix, double iy) 
	{
		this.image = image;
		x = ix;
		y = iy;
	}
	
	public Planetoid (Drawable image, Parcel saved) {
		this.image = image;
		saved.setDataPosition(0);
		diameter = saved.readDouble();
		mass = saved.readDouble();
		x = saved.readDouble();
		y = saved.readDouble();
		dx = saved.readDouble();
		dy = saved.readDouble();
	}
	
	public Planetoid(Parcel saved) {
		saved.setDataPosition(0);
		diameter = saved.readDouble();
		mass = saved.readDouble();
		x = saved.readDouble();
		y = saved.readDouble();
		dx = saved.readDouble();
		dy = saved.readDouble();
	}
	
	public Planetoid(Planetoid orig)
	{
		this.image = orig.image;
		x = orig.x;
		y = orig.y;
		dx = orig.dx;
		dy = orig.dy;
		diameter = orig.diameter;
		mass = orig.mass;
		isSelected = orig.isSelected;
	}

	public double getDiameter() {
		return diameter;
	}
	
	public void setDiameter(double diameter) {
		this.diameter = diameter;
	}
	
	public double getMass() {
		return mass;
	}
	public void setMass(double mass) {
		this.mass = mass;
	}
	public Drawable getImage() {
		return image;
	}
	
	public void setImage(Drawable image) {
		this.image = image;
	}
	
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public double getDx() {
		return dx;
	}
	public double getDy() {
		return dy;
	}
	public void setVelocity(double dx, double dy) {
		this.dx = dx;
		this.dy = dy;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void select() {
		isSelected = true;
	}
	public void deselect() {
		isSelected = false;
	}
	/**
	 * Calculates the area of space occupied by this planetoid.
	 * @return The area of the planetoid.
	 */
	public double getArea() {
		return Math.pow(diameter/2, 2)*Math.PI;
	}
	/**
	 * Adjusts the planetoid's diameter so that its area is equal to nArea.
	 * @param nArea 	The new area of the planetoid that is desired.
	 */
	public void setArea(double nArea) {
		setDiameter(2.0*Math.sqrt(nArea/Math.PI));
	}
	/**
	 * Calculates the density (mass/area) of this planetoid.
	 * @return The density of the planetoid.
	 */
	public double getDensity() {
		return mass / getArea();
	}
	/**
	 * Applies a force for a given duration on the planetoid, altering its velocity.
	 * @param fx 	The force component in the x direction.
	 * @param fy 	The force component in the y direction.
	 * @param timeLapse	The duration for which the force is applied.
	 */
	public void applyForce(double fx, double fy, double timeLapse) {
		elapsed = timeLapse;
		double impulse = timeLapse / mass; // not really equivalent to physics impulse, just a name
		dx+=fx*impulse;
		dy+=fy*impulse;
	}
	/**
	 * Sets the velocity of the planetoid to the appropriate value to match the given momentum
	 * @param mx
	 * @param my
	 */
	public void setMomentum(double mx, double my) {
		dx = mx / mass;
		idx = dx;
		dy = my / mass;
		idy = dy;
	}
	/**
	 * Move the planetoid along its current trajectory for the latest time step.
	 */
	public void move() {
		if(!isSelected()) {
			x = x + (idx+dx)*elapsed/2.0;
			y = y + (idy+dy)*elapsed/2.0;
			idx = dx;
			idy = dy;
		}
	}
	/**
	 * Get the square Euclidean distance from this planetoid to another.
	 * @param other The other planetoid.
	 * @return The square of the Euclidean distance.
	 */
	public double getSDistanceTo(Planetoid other) {
		return ((x-other.getX())*(x-other.getX()) + (y-other.getY())*(y-other.getY()));
	}
	/**
	 * Get the Euclidean distance from this planetoid to another
	 * @param other	The other planetoid.
	 * @return The Euclidean distance.
	 */
	public double getDistanceTo(Planetoid other) {
		return Math.sqrt(getSDistanceTo(other));
	}
	/**
	 * Merge this planetoid with another. This operation is commutative, 
	 * i.e. the result is the same if planetoids are reversed. The smaller planetoid 
	 * is effectively absorbed in an inelastic collision. This treatment of the 
	 * merge is purely arbitrary. Note that the two planetoids do not necessarily 
	 * need to be touching prior to the merge.
	 * @param other The planetoid with which this will be merged.
	 */
	public void merge(Planetoid other) {
		double oMass = getMass(); // old mass
		setMass(oMass+other.getMass());
		setMomentum(oMass*getDx()+other.getMass()*other.getDx(), oMass*getDy()+other.getMass()*other.getDy());
		double ix = x;
		double iy = y;
		// if neither or both selected; do anyway
		if(other.getDiameter() > getDiameter()) {
			setArea(getArea()/2+other.getArea());
			x = other.getX();
			y = other.getY();
		}
		else {
			setArea(getArea()+other.getArea()/2);
		}
		if (other.isSelected == true) {// other selected
			x = other.getX();
			y = other.getY();
		}
		else if (isSelected == true){ // this selected, other not
			x = ix;
			y = iy;
		}
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(diameter);
		dest.writeDouble(mass);
		dest.writeDouble(x);
		dest.writeDouble(y);
		dest.writeDouble(dx);
		dest.writeDouble(dy);
	}
	
	public static final PlanetoidCreator CREATOR = new PlanetoidCreator();

	public static class PlanetoidCreator implements Parcelable.Creator<Planetoid> {
		@Override
		public Planetoid createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new Planetoid(source);
		}

		@Override
		public Planetoid[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Planetoid[size];
		}
	}
		
}
