package csc120.lab6.FFT;

import android.os.Parcel;
import android.os.Parcelable;

public class Complex implements Parcelable {
	
	private double a;
	private double b;
	
	public Complex (double real, double imag){
		a = real;
		b = imag;
	}
	
	public Complex (Complex other) {
		this(other.getReal(), other.getImaginary());
	}
	
	public double getReal(){
		return a;
	}
	
	public double getImaginary(){
		return b;
	}
	
	public double getMagnitude() {
		return Math.sqrt(a*a+b*b);
	}
	
	public Complex plus(Complex rhs){
		return new Complex (rhs.a + a, rhs.b + b);		
	}
	
	public Complex minus(Complex rhs){
		return new Complex (a - rhs.a , b - rhs.b);		
	}
	
	public Complex times(Complex rhs){
		return new Complex (a*rhs.a - b*rhs.b , a*rhs.b+b*rhs.a);		
	}
	
	public Complex dividedBy(Complex rhs){
		Complex rational = new Complex (rhs.a, -rhs.b);
		Complex denom = rhs.times(rational);
		Complex numer = times(rational);
		return new Complex (numer.a/denom.a, numer.b/denom.a);		
	}
	
	/**
	* Formula from "How to Find the Square Root of a Complex Number." Stanley Rabinowitz.
	*/
	public Complex sqrt(){
		return new Complex (1/Math.sqrt(2)*Math.sqrt(Math.sqrt(a*a+b)+a), 
				1/Math.sqrt(2)*Math.sqrt(Math.sqrt(a*a+b)-a));
	}
	
	public Complex sin(){
		return new Complex (Math.sin(a)*Math.cosh(b), Math.cos(a)*Math.sinh(b));
	}
	
	public Complex cos(){
		return new Complex (Math.cos(a)*Math.cosh(b), Math.sin(a)*Math.sinh(b));
	}
	
	public Complex tan(){
		return sin().dividedBy(cos());
	}
	
	/**
	 * Returns e^(a+bi)
	 * @return
	 */
	public Complex exp(){
		return new Complex (Math.exp(a)*Math.cos(b), Math.exp(a)*Math.sin(b));
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		//dest.setDataPosition(1);
		dest.writeDouble(a);
		dest.writeDouble(b);
	}
	
	public static final Parcelable.Creator<Complex> CREATOR
		= new Parcelable.Creator<Complex>() {
		public Complex createFromParcel(Parcel in) {
			return new Complex (in);
		}

		@Override
		public Complex[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Complex[size];
		}
	};
	
	public Complex (Parcel in) {
		//in.setDataPosition(1);
		a = in.readDouble();
		b = in.readDouble();
	}
}
