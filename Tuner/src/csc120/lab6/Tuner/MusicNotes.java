package csc120.lab6.Tuner;

import java.util.HashMap;

public class MusicNotes {
	public static final String[] standardScale = 
		{"A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"};
	private double[] notes;
	
	/**
	 * Constructs a MusicNotes based upon a 10-octave 12-note chromatic scale
	 * with the base note corresponding to A4 = 440 Hz.
	 */
	public MusicNotes(){
		this(440);
	}
	
	/**
	 * Constructs a MusicNotes object based upon the given base note.
	 * A 10-octave 12-note chromatic scale is assumed with the base note 
	 * corresponding to A4.
	 * 
	 * @param baseNote The base note frequency from which all others are derived, in Hz.
	 */
	public MusicNotes(double baseNote){
		this(baseNote, 48);
	}
	
	/**
	 * Constructs a MusicNotes object based upon the given base note.
	 * A 12 note chromatic scale is assumed. For example, MusicNotes(440, 48)
	 * constructs a scale based upon A4 = 440 Hz.
	 * 
	 * @param baseNote The base note frequency from which all others are derived, in Hz.
	 * @param noteIndex The index of the note taken from the standard chromatic scale, e.g. 48 for A4.
	 */
	public MusicNotes(double baseNote, int noteIndex) {
		notes = new double[120];
		double step = Math.pow((double) 2, (double) 1/ (double) 12);
		notes[noteIndex] = baseNote;
		for(int i = noteIndex-1; i >= 0; i--) {
			notes[i] = notes[i+1]/step;
		}
		for(int i = noteIndex+1; i < 120; i++) {
			notes[i] = notes[i-1]*step;
		}
	}
	
	public double[] getNotes(){
		return notes;
	}
	
	public String getNote(int index){
		int octave = index/12;
		int noteIndex = index%12;
		return standardScale[noteIndex] + octave;
	}

}
