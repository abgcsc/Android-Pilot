package csc120.lab6.FFT;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class Complex represents a complex number a+bi, where a is the real component
 * and b is the imaginary component. It provides useful mathematical operations
 * concerning complex numbers.
 * @author
 *
 */
public class Complex implements Parcelable {
	
	private double a; // the real component
	private double b; // the imaginary component
	
	/**
	 * Creates a new complex number equal to real+imag*i.
	 * @param real The real component of the new Complex object.
	 * @param imag The imaginary component of the new Complex object.
	 */
	public Complex (double real, double imag){
		a = real;
		b = imag;
	}
	
	/**
	 * Creates a copy of the given complex number.
	 * @param other The Complex object to be copied.
	 */
	public Complex (Complex other) {
		this(other.getReal(), other.getImaginary());
	}
	
	/**
	 * @return The real component of this Complex number.
	 */
	public double getReal(){
		return a;
	}
	
	/**
	 * @return The imaginary component of this Complex number.
	 */
	public double getImaginary(){
		return b;
	}
	
	/**
	 * Returns the sine of this complex number.
	 * @return The sine of this complex number.
	 */
	public Complex sin(){
		return new Complex (Math.sin(a)*Math.cosh(b), Math.cos(a)*Math.sinh(b));
	}
	
	/**
	 * Returns the cosine of this complex number.
	 * @return The cosine of this complex number.
	 */
	public Complex cos(){
		return new Complex (Math.cos(a)*Math.cosh(b), Math.sin(a)*Math.sinh(b));
	}
	
	/**
	 * Returns the tangent of this complex number.
	 * @return The tangent of this complex number.
	 */
	public Complex tan(){
		return sin().dividedBy(cos());
	}
	
	/**
	 * Calculates the square root of this complex number.
	 * Formula from "How to Find the Square Root of a Complex Number." Stanley Rabinowitz.
	 * @return The square root of this complex number.
	 */
	public Complex sqrt(){
		return new Complex (1/Math.sqrt(2)*Math.sqrt(Math.sqrt(a*a+b)+a), 
				1/Math.sqrt(2)*Math.sqrt(Math.sqrt(a*a+b)-a));
	}
	
	/**
	 * Returns e^(a+bi)
	 * @return e^(a+bi)
	 */
	public Complex exp(){
		return new Complex (Math.exp(a)*Math.cos(b), Math.exp(a)*Math.sin(b));
	}

	/**
	 * Android function
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	/**
	 * Android function
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(a);
		dest.writeDouble(b);
	}
	
	/**
	 * Android construct
	 */
	public static final Parcelable.Creator<Complex> CREATOR
		= new Parcelable.Creator<Complex>() {
		public Complex createFromParcel(Parcel in) {
			return new Complex (in);
		}

		@Override
		public Complex[] newArray(int size) {
			return new Complex[size];
		}
	};
	
	/**
	 * Android function
	 * @param in A Parcel condensed from a Complex object.
	 */
	public Complex (Parcel in) {
		a = in.readDouble();
		b = in.readDouble();
	}
}
