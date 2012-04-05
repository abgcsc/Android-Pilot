package csc120.lab6.Tuner;

public class MusicNotes {
	private static final double log2 = Math.log(2);
	public static final String[] standardScale = 
		{"A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"};
	private double[] notes; // will store all of the notes' frequencies
	private double[] logNotes; // will store all of the notes' frequencies in log scale for faster processing
	
	
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
		logNotes = new double[120];
		double step = Math.pow((double) 2, (double) 1/ (double) 12);
		notes[noteIndex] = baseNote;
		logNotes[noteIndex] = Math.log(notes[noteIndex]) / log2;
		for(int i = noteIndex-1; i >= 0; i--) {
			notes[i] = notes[i+1]/step;
			logNotes[i] = logNotes[i+1] - ((double) 1/ (double) 12);
		}
		for(int i = noteIndex+1; i < 120; i++) {
			notes[i] = notes[i-1]*step;
			logNotes[i] = logNotes[i-1] + ((double) 1/ (double) 12);
		}
	}
	
	/**
	 * Returns all of the notes (in frequency form).
	 * @return Return all of the notes.
	 */
	public double[] getNotes(){
		return notes;
	}
	
	/**
	 * Returns all of the notes (in log frequency form).
	 * @return Return all of the logarithms base 2 notes.
	 */
	public double[] getLogNotes(){
		return logNotes;
	}
	
	/**
	 * Returns the note (e.g. A4) associated with the given index.
	 * @param index The index of the desired note.
	 * @return A string containing the name of the note associated with the given index.
	 */
	public String getNote(int index){
		int octave = index/12;
		int noteIndex = index%12;
		return standardScale[noteIndex] + octave;
	}
	
	/**
	 * Finds the index of the nearest note to the given frequency.
	 * @param frequency The frequency for which the nearest note is desired
	 * @return The index of the closest note.
	 */
	public int getNearestNoteIndex(double frequency) {
		double logFreq = Math.log(frequency) / log2;
		int closest = 0;
		// find closest note on a log scale
		for(int i = 1; i < logNotes.length; i++) {
			if(Math.abs(logNotes[i] - logFreq) < Math.abs(logNotes[closest] - logFreq)) {
				closest = i;
			}
		}
		return closest;
	}

}
