package csc120.lab6.FFT;


public class FastFourierTransform {
	private static final double log2 = Math.log(2);
	private static int[] bitReverse; // stores the reversed-bit index of each index
	private static Complex[][] twiddles; // stores the twiddle factors, pre-computed
	
	/**
	 * Initialize the FastFourierTransform class for use. Should only be called once for each input size.
	 * Size must be a power of 2.
	 *
	 * @param size A power of 2, the size of the arrays on which the FFT will operate.
	 */
	public static void initialize(int size) {
		bitReverse = buildBitReverse(size);
		twiddles = computeTwiddles(size);
	}
	
	/**
	 * Performs a fast Fourier transform in place on the given array.
	 * 
	 * @param input The array on which a fast Fourier transform is desired. After completion, it will contain the results.
	 */
	public static void fft(Complex[] input) {
        int size = input.length;
        
        int logSize = (int) (Math.log((double) size) / log2);

        if (Integer.bitCount(size) != 1) { 
        	throw new RuntimeException("Input array's size not a power of 2."); 
        }
        
        shuffle(input);
        int subSize = 1;
        for(int j = 0; j < logSize; j++) {
	        for(int k = 0; k < size; ) {
	        	int ceiling = k+subSize;
		        for(int i = k; i < ceiling; i++){
	        		Complex temp = new Complex (input[i]);
	        		input[i] = temp.plus(twiddles[j][i % subSize].times(input[i+subSize]));
	        		input[i+subSize] = temp.minus(twiddles[j][i % subSize].times(input[i+subSize]));
	        		
	        	}
		        k = ceiling+subSize;
	        }
	        subSize<<=1; // multiply by 2
        }
    }
	
	/**
	 * Shifts all elements to their final positions of an FFT in the input array. Modifies given array. May be undone with a repeat call.
	 * 
	 * @param input
	 * @param size
	 * @param floor
	 */
	private static void shuffle(Complex[] input){
		int ceiling = input.length/2;
		for(int i = 0; i < ceiling; i++) {
			Complex temp = input[i];
			input[i] = input[bitReverse[i]];
			input[bitReverse[i]] = temp;
		}
	}
	
	/**
	 * For each index of an array of length size, calculates the reverse-bit index.
	 * @param size The number (range) of indices to be reversed.
	 * @return An array containing the reverse-bit index for each index of the array. That is, array[i] == reverseBits(i)
	 */
	public static int[] buildBitReverse(int size) {
		if (Integer.bitCount(size) != 1) { 
        	throw new RuntimeException("Input array's size not a power of 2."); 
        }
		int logSize = (int) (Math.log((double) size) / log2);
		int[] bits = new int [size];
		for(int i = 0; i < size; i++) {
			bits[i] = reverseBits(i, logSize);
		}
		return bits;
	}
	
	/**
	 * Reverse the lower bitSize bits of the given integer.
	 * @param in
	 * @param bitSize
	 * @return
	 */
	public static int reverseBits(int in, int bitSize){
		if(bitSize > 32) bitSize = 32;
		if(bitSize > 0) {
			int reversed = 0;
			int shift = 1 << (bitSize-1);
			for(int j = 0; j < bitSize; j++) {
				reversed += (shift)*(in % 2);
				shift>>>=1;
				in >>>= 1;
			}
			return reversed;
		}
		return in;
	}
	
	/**
	 * Rotate the lower bitSize bits of the given integer to the right by 1 bit.
	 * @param in
	 * @param bitSize
	 * @return
	 */
	public static int rotateRight(int in, int bitSize){
		if(bitSize > 32) { bitSize = 32;}
		if (bitSize > 1) {
			int carry = in % 2;
			int shift = 1;
			for(int j = 1; j < bitSize; j++) {
				in = in & ~shift;
				shift<<=1;
				in+=((shift&in)>>>1);
			}
			if(carry == 0) {
				in = in & ~shift;
			}
			else {
				in = in | shift;
			}
		}
		return in;
	}
	
	/**
	 * Rotate the lower bitSize bits of the given integer to the left by 1 bit.
	 * @param in
	 * @param bitSize
	 * @return
	 */
	public static int rotateLeft(int in, int bitSize){
		if(bitSize > 32) { bitSize = 32;}
		if (bitSize > 1) {
			int shift = 1 << (bitSize-1);
			int carry = Integer.bitCount(in & shift); 
			for(int j = 1; j < bitSize; j++) {
				in = in & ~shift;
				shift>>>=1;
				in+=((shift&in)<<1);
			}
			if(carry == 0) {
				in = in & ~carry;
			}
			else {
				in = in | carry;
			}
		}
		return in;
	}
	
	/**
	 * Computes the twiddle factors for sizes 1, 2, 4, 8, ... size/2.
	 * @param size The full size of the input array. Must be a power of 2.
	 * @return A 2D array of twiddle factors.
	 */
	public static Complex[][] computeTwiddles(int size) {
		if (Integer.bitCount(size) != 1) { 
        	throw new RuntimeException("Input array's size not a power of 2."); 
        }
		int logSize = (int) (Math.log((double) size) / log2);
		int subSize = 1;
		Complex[][] twiddles = new Complex[logSize][];
		for(int i = 0; i < logSize; i++) {
			twiddles[i] = new Complex[subSize];
			for(int k = 0; k < subSize; k++) {
				twiddles[i][k] = new Complex (0, - 2 *k * Math.PI / (subSize*2)).exp();
			}
			subSize*=2;
		}
		
		return twiddles;
	}
	
}
