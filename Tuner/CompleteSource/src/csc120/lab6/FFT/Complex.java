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
	 * Returns the magnitude sqrt(a*a+b*b) of this Complex number.
	 * @return The magnitude of this Complex number
	 */
	public double getMagnitude() {
		return Math.sqrt(getSquareMagnitude());
	}
	
	/**
	 * Returns the square magnitude a*a+b*b of this Complex number.
	 * @return The square of the magnitude of this Complex number.
	 */
	public double getSquareMagnitude() {
		return getReal()*getReal()+getImaginary()*getImaginary();
	}
	
	/**
	 * Calculates the sum of this Complex number and another.
	 * @param other	The Complex number to be added to this one.
	 * @return		The sum of two Complex numbers, this plus other.
	 */
	public Complex plus(Complex other) {
		return new Complex(getReal()+other.getReal(), getImaginary()+other.getImaginary());
	}
	
	/**
	 * Calculates the difference of this Complex number and another.
	 * @param other	The Complex number to be subtracted from this one.
	 * @return		The difference of two Complex numbers, this minus other.
	 */
	public Complex minus(Complex other) {
		return new Complex(getReal()-other.getReal(), getImaginary()-other.getImaginary());
	}
	
	/**
	 * Calculates the product of this Complex number and another.
	 * @param other	The Complex number to be multiplied by this one.
	 * @return		The product of two Complex numbers, this times other.
	 */
	public Complex times(Complex other) {
		return new Complex(getReal()*other.getReal()-getImaginary()*other.getImaginary(), getImaginary()*other.getReal()+a*other.getImaginary());
	}
	
	/**
	 * Calculates the division of this Complex number and another.
	 * @param other	The Complex number that will divide this one (the denominator).
	 * @return		The division of two Complex numbers, this divided by other.
	 */
	public Complex dividedBy(Complex other) {
		Complex denom = new Complex (other.getReal(), other.getImaginary()); // conjugate
		Complex numer = times(denom);
		denom = other.times(denom); // now entirely real
		return new Complex(numer.getReal()/denom.getReal(), numer.getImaginary()/denom.getReal());
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
