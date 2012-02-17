package csc120.lab6.FFT;


public class FastFourierTransform {
	private static int[] bitReverse = new int [8192]; // stores the reversed-bit index of each index
	
	/**
	 * Performs the radix-2 Cooley-Tukey fft.
	 * @param input
	 * @return The fft of the given Complex array.
	 */
	public static Complex[] fft(Complex[] input) {
        int size = input.length;

        // base case
        if (size == 1) {
        	return new Complex[] { input[0] };
        }

        if ((size % 2) == 1) { 
        	throw new RuntimeException("Input array's size not a power of 2."); 
        }

        // fft of even half of terms
        Complex[] half = new Complex[size/2];
        int i = 0;
        for (; i < size/2; i++) {
            half[i] = input[2*i];
        }
        Complex[] even = fft(half);

        // fft of odd half of terms 
        for (i = 0; i < size/2; i++) {
            half[i] = input[2*i+1];
        }
        Complex[] odd = fft(half);

        Complex[] output = new Complex[size];
        for (int k = 0; k < size/2; k++) {
        	Complex twiddle = new Complex (0, 2*Math.PI*k/size);
            output[k] = even[k].plus(twiddle.exp().times(odd[k]));
            output[k + size/2] = even[k].minus(twiddle.exp().times(odd[k]));
        }
        return output;
    }
	
	/**
	 * Shifts all elements to their final positions of an FFT in the input array.
	 * 
	 * @param input
	 * @param size
	 * @param floor
	 */
	private static void shuffle(Complex[] input, int size, int floor){
		
	}
	
	private static void buildBitReverse() {
		int size = bitReverse.length;
		for(int i = 0; i < size; i++) {
			bitReverse[i] = Integer.reverse(i);
		}
	}
}
