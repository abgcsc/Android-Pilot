package com.lab.gps.nfc;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.util.Log;

import com.lab.gps.nfc.record.TextRecord;

import java.util.ArrayList;
import java.util.List;

public class NFCMessageParser
{

    static final String TAG = "ViewTag";

    /**
     * This activity will finish itself in this amount of time if the user
     * doesn't do anything.
     */
    static final int ACTIVITY_TIMEOUT_MS = 1 * 1000;
    
    double latitude;
    double longitude;
    String overlayString;

    public NFCMessageParser()
    {
    }
    
    // Called when we receive the intent that we have received NFC tage
    public boolean parseIntent(Intent intent) 
    {
        // Parse the intent
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) 
        {
        	// This gets some raw data that we send to parseRecords()
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;
            if (rawMsgs != null) 
            {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) 
                {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } 
            else 
            {
                // Unknown tag type
                byte[] empty = new byte[] {};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[] {record});
                msgs = new NdefMessage[] {msg};
            }
            
            parseRecords(msgs);
            
            return true;
        } 
        else 
        {
            Log.e(TAG, "Unknown intent " + intent);
            return false;
        }
    }

    // We parse out the data (TextRecords) since we are just receiving plain text
    void parseRecords(NdefMessage[] msgs)
    {
        if (msgs == null || msgs.length == 0) 
        {
            return;
        }
        
        NdefRecord[] records = msgs[0].getRecords();
        List<TextRecord> elements = new ArrayList<TextRecord>();
        
        // Read out each record from the msg we received
        // The parse method does some magic work to read the
        // data since the nfc stores plain text in a special way
        for (NdefRecord record : records) 
        {
        	if (TextRecord.isText(record)) 
        	{
                elements.add(TextRecord.parse(record));
            }
        }
        
        // Go through each record (the data portion of the ndef message)
        // and print out the data we received
        final int size = elements.size();
        for (int i = 0; i < size; i++) 
        {
            TextRecord record = elements.get(i);
            
            String temp_string = ((TextRecord)record).getText();

            ScavengerInfo si = new ScavengerInfo(temp_string);
            
            latitude = si.getLatitude();
            longitude = si.getLongitude();
            overlayString = si.getOverlayString();
        }
    }
    
    public double getLatitude()
    {
    	return latitude;
    }
    
    public double getLongitude()
    {
    	return longitude;
    }
    
    public String getOverlayString()
    {
    	return overlayString;
    }
}
