package com.lab.gps;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.lab.gps.nfc.NFCMessageParser;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Vibrator;

public class HelloGoogleMapsActivity extends MapActivity {
   
	NFCMessageParser messageParser;
	MapView mapView;
	List<Overlay> mapOverlays;
	Drawable drawable;
	HelloItemizedOverlay itemizedoverlay;
	Vibrator vibrator;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        
        messageParser = new NFCMessageParser();
        
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        mapOverlays = mapView.getOverlays();
    	drawable = this.getResources().getDrawable(R.drawable.androidmarker);
    	itemizedoverlay = new HelloItemizedOverlay(drawable, this);
    	
    	vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        
        ResolveIntent(getIntent());
        
    }
    
    void ResolveIntent(Intent intent)
    {
    	if(messageParser.parseIntent(intent))
    	{
        	//GeoPoint point = new GeoPoint(32525760, -92644710);
        	GeoPoint point = new GeoPoint((int)(messageParser.getLatitude()*1e6), (int)(messageParser.getLongitude()*1e6));
        	OverlayItem overlayitem = new OverlayItem(point, "Point Information", messageParser.getOverlayString());
        
        	// Place the "blip" on the screen
        	itemizedoverlay.addOverlay(overlayitem);
        	mapOverlays.add(itemizedoverlay);
        	mapView.postInvalidate();
        	
        	// Center the screen on the "blip" and zoom in on it.
        	mapView.getController().setCenter(point);
        	mapView.getController().setZoom(18);
        	
        	vibrator.vibrate(200);
    	}
    }
    
    @Override
    protected boolean isRouteDisplayed() {
    	return false;
    }
    
    public void onBackPressed()
    {
    	System.out.println("User pressed the back button.");
    	
    	moveTaskToBack(true);
    }
    
    public void onNewIntent (Intent intent)
    {
    	System.out.println("A new intent was called.");
    	
    	setIntent(intent);
    	ResolveIntent(getIntent());
    }
}	