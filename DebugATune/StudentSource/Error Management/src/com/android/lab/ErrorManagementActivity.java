package com.android.lab;

import java.util.ArrayList;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ErrorManagementActivity extends Activity {
    private Button play;
    private ErrorManagement a;
    
    private final int duration = 5; // seconds
    private final int sampleRate = 8000;
    private final int numSamples = duration * sampleRate;
    private byte generatedSnd[] = new byte[2 * numSamples];//the prepped tones to send to the audio driver
    private int pos=0; //to insert next note
    private String[] mario=	{	"E",	"E",	"-",	"E",	"-",	"C",	"E",	"-",	"G",	"-",	"G",	"C",	"-",	"G", 	"E",	"-"};
	private int[] dur=		{	4,		4,		4,		4,		4,		4,		4,		4,		4,		4,		2,		4,		4,		2, 		4,		4};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        play=(Button) findViewById(R.id.button1);
        a=new ErrorManagement();
        init();
    	check();
    	prepare();
    }
    
    @Override
    public void onStart()
    {
    	super.onStart();
    	play.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, numSamples, AudioTrack.MODE_STATIC);
		        audioTrack.write(generatedSnd, 0, generatedSnd.length);
		        audioTrack.play();
			}
		});
    }
    
    @Override
    public void onPause()
    {
    	super.onStart();
    	
    }
    
    @Override
    public void onResume()
    {
    	super.onResume();
    }
    
    @Override
    public void onRestart()
    {
    	super.onRestart();
    	
    }
    
    @Override
    public void onStop()
    {
    	super.onStop();
    }
    
    @Override
    public void onDestroy()
    {
    	super.onDestroy();
    }
    
    /*END LIFECYCLE CODE------------------------------------------------------------*/
    /**
     * Places frequencies into the students array
     */
    private void init()
    {
    	ArrayList<Integer> al=new ArrayList<Integer>();
    	al.add(523);//C
    	al.add(587);//D
    	al.add(659);//E
    	al.add(698);//F
    	al.add(784);//G
    	al.add(880);//A
    	al.add(988);//B
    	a.fillFreq(al);    	
    }
    /**
     * checks to see if the students array added the correct number of elements, and if it properly reports size
     */
    private void check()
    {
    	if(a.getSize()!=7)
    	{
    		//one of the size methods didnt check out
    	}
    	a.clearAll();
    	if(a.getSize()!=0)
    	{
    		//clear didnt clear everything, or the size method didnt handle the loop properly
    	}
    	init();//reset the
    	if(a.getSize()!=7)
    	{
    		//one of the size methods didnt check out again
    	}
    }
    
    /**
     * builds the audio bitstream to be sent to the driver for playback
     */
    private void prepare()
    {
    	for(int i=0;i<mario.length;i++)
    	{
    		if(mario[i].equals("-"))
    		{
    			gen4Rest();
    		}
    		else{
    			switch(dur[i])
    			{
    			case 1:gen1(mario[i]);break;
    			case 2:gen2(mario[i]);break;
    			case 4:gen4(mario[i]);break;
    			default:gen8(mario[i]);
    			}
    		}
    	}
    }
    
    private void gen8(String freq)
    {
    	double sample[]=new double[1000];//the new note to generate
    	for(int i=0;i<1000;i++)
    	{
    		sample[i]=Math.sin(2 * Math.PI * i / (sampleRate/a.getFreq(freq)));//calculate values so it makes a wave
    	}
    	// convert to 16 bit pcm sound array
        // assumes the sample buffer is normalized.
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
			// in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[pos+idx++] = (byte) (val & 0x00ff);//alternates bytes to make wave form
            generatedSnd[pos+idx++] = (byte) ((val & 0xff00) >>> 8);//
        }
    	pos+=idx;//tally the eighth note so we dont overlap notes
    }
    
    private void gen4(String freq)
    {
    	double sample[]=new double[2000];//the new note to generate
    	for(int i=0;i<2000;i++)
    	{
    		sample[i]=Math.sin(2 * Math.PI * i / (sampleRate/a.getFreq(freq)));//calculate values so it makes a wave
    	}
    	// convert to 16 bit pcm sound array
        // assumes the sample buffer is normalized.
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
			// in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[pos+idx++] = (byte) (val & 0x00ff);//alternates bytes to make wave form
            generatedSnd[pos+idx++] = (byte) ((val & 0xff00) >>> 8);//
        }
    	pos+=idx;//tally the quarter note so we dont overlap notes
    }
    
    private void gen2(String freq)
    {
    	double sample[]=new double[4000];//the new note to generate
    	System.out.print("0");
    	for(int i=0;i<4000;i++)
    	{
    		sample[i]=Math.sin(2 * Math.PI * i / (sampleRate/a.getFreq(freq)));//calculate values so it makes a wave
    	}
    	System.out.print("1");
    	// convert to 16 bit pcm sound array
        // assumes the sample buffer is normalized.
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
			// in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[pos+idx++] = (byte) (val & 0x00ff);//alternates bytes to make wave form
            generatedSnd[pos+idx++] = (byte) ((val & 0xff00) >>> 8);//
        }
        System.out.print("2");
    	pos+=idx;//tally the half so we dont overlap notes
    }
    
    private void gen1(String freq)
    {
    	double sample[]=new double[8000];//the new note to generate
    	for(int i=0;i<8000;i++)
    	{
    		sample[i]=Math.sin(2 * Math.PI * i / (sampleRate/a.getFreq(freq)));//calculate values so it makes a wave
    	}
    	// convert to 16 bit pcm sound array
        // assumes the sample buffer is normalized.
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
			// in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[pos+idx++] = (byte) (val & 0x00ff);//alternates bytes to make wave form
            generatedSnd[pos+idx++] = (byte) ((val & 0xff00) >>> 8);//
        }
    	pos+=idx;//tally the whole note so we dont overlap notes
    }
    
    private void gen4Rest()
    {
        int idx = 0;
        for (int i=0;i<2000;i++) {
            // scale to maximum amplitude
            final short val = 0;
			// in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[pos+idx++] = (byte) (val & 0x00ff);//alternates bytes to make wave form
            generatedSnd[pos+idx++] = (byte) ((val & 0xff00) >>> 8);//
        }
    	pos+=idx;//tally the quarter note so we dont overlap notes
    }
}