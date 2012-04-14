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
	private double dy = 0; // the y velocity
	private double elapsed = 0; // storage for an elapsed time (perhaps since last move)
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
	 * Calculates the density (mass/area) of this planetoid.
	 * @return The density of the planetoid.
	 */
	public double getDensity() {
		return mass / getArea();
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
	 * Android function - Something to do with Parcels
	 */
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Android function - Flattens Planetoid into a Parcel.
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(diameter);
		dest.writeDouble(mass);
		dest.writeDouble(x);
		dest.writeDouble(y);
		dest.writeDouble(dx);
		dest.writeDouble(dy);
	}
	
	/**
	 * Android construct - see below
	 */
	public static final PlanetoidCreator CREATOR = new PlanetoidCreator();

	/**
	 * Android construct - Responsible for converting Planetoid to and from Parcelable at appropriate times.
	 */
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
