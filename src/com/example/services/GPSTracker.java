package com.example.services;

import com.example.models.GlobalVars;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
 
public class GPSTracker extends Service implements LocationListener {
 
    private final Context mContext;
 
    // flag for GPS status
    boolean isGPSEnabled = false;
 
    // flag for network status
    boolean isNetworkEnabled = false;
 
    // flag for GPS status
    boolean canGetLocation = false;
 
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    
	GPSTracker gpsTracker;
	double gpsLatitude, gpsLongitude;
	public static float[] results = new float[5];
	public static double mStartLat;
	public static double mStartLon;
	public static double myGPSLat;
	public static double myGPSLon;
	public static String distance_from_php = null;
	
	public static double theDistance;
 
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
 
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
 
    // Declaring a Location Manager
    protected LocationManager locationManager;
 
    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }
 
    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);
 
            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
 
            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
 
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
 
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        return location;
    }
     
    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GPSTracker.this);
        }       
    }
     
    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
         
        // return latitude
        return latitude;
    }
     
    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
         
        // return longitude
        return longitude;
    }
     
    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }
     
    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
      
        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");
  
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
  
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
  
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
  
        // Showing Alert Message
        alertDialog.show();
    }
    
    /**
     * Function to show alert dialog with passed message
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlertPassMessage(String message){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
      
        // Setting Dialog Title
        alertDialog.setTitle("Alert:Distance changed");
  
        // Setting Dialog Message
        alertDialog.setMessage(message);
      
        // on pressing cancel button
        alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
  
        // Showing Alert Message
        alertDialog.show();
    }
 
    @Override
    public void onLocationChanged(Location location) {
    	
    	gpsTracker = new GPSTracker(GPSTracker.this);
    	
//    	get the new location, poll for the lat and longitude, get distance, calculate with
    	mStartLat = GlobalVars.lat_origin;
    	mStartLon = GlobalVars.lon_origin;
//    	myGPSLat = location.getLatitude();
//    	myGPSLon = location.getLongitude();
    	myGPSLat = getLatitude();
    	myGPSLon = getLongitude();
    	results = new float[5];
    	
    	
    	Location.distanceBetween(myGPSLat, myGPSLon, mStartLat, mStartLon, results);
    	
    	
    	Location loc = new Location("LocationOrigin");
    	loc.setLatitude(mStartLat);
    	loc.setLongitude(mStartLon);
    	
    	Log.i("VALUES used:GPS: ", "lat: " + myGPSLat + " lon: " + myGPSLon);
    	Log.i("VALUES used:current location: ", "lat: " + mStartLat + " lon: " + mStartLon);
    	    	
//    	theDistance = (location.distanceTo(loc))/1000;
    	
    	theDistance = CalculationByDistance(mStartLat, mStartLon, myGPSLat, myGPSLon);
//    	theDistance = CalculationByDistance(-1.038147, 37.082634, -1.281269, 36.822214);
    	

    }
    
    public double CalculationByDistance(double initialLat, double initialLong, double finalLat, double finalLong){
        /*PRE: All the input values are in radians!*/

        double earthRadius = 6371; //In Km if you want the distance in km
        
        double lat1 = initialLat;
        	if(lat1 < 0){ //ie negative
        		lat1 = (-1*lat1);
        	}
        double lon1 = initialLong;
        double lat2 = finalLat;
	        if(lat2 < 0){ //ie negative
	        	lat2 = (-1*lat2);
	    	}
        double lon2 = finalLong;
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);

        double deltaLonRad = Math.toRadians(lon2 - lon1);
        
        double latitude1 = Math.toRadians(Double.valueOf(Location.convert(lat1, Location.FORMAT_DEGREES)) );
    	double longitude1 = Math.toRadians(Double.valueOf(Location.convert(lon1, Location.FORMAT_DEGREES)) );
    	double latitude2 = Math.toRadians(Double.valueOf(Location.convert(lat2, Location.FORMAT_DEGREES)) );
    	double longitude2 = Math.toRadians(Double.valueOf(Location.convert(lon2, Location.FORMAT_DEGREES)) );
    	
//	   double distance = (6371 * Math.acos
//				( Math.cos( latitude1 ) * 
//				Math.cos( latitude2 ) * 
//				Math.cos( longitude1 - longitude2 ) + 
//				Math.sin( latitude1 * Math.sin( latitude2 ) )
//				)
//		);
    	
    	/**
    	double distance = Math.acos(Math.sin(latitude1) * Math.sin(latitude2) + Math.cos(latitude1) * Math.cos(latitude2) * Math.cos(longitude1 - longitude2));
    	if(distance < 0) {
    		distance = distance + Math.PI;
        }
    	
    	Math.round(distance * 6371);
    	
    	**/
    	
    	double distance = (6371 * Math.acos
    			( Math.cos( latitude1 ) * 
    					Math.cos( latitude2 ) * 
    					Math.cos( longitude1 - longitude2 ) + 
    				Math.sin( latitude1 ) * Math.sin( latitude2 ) ) 
    				) ;
//        double distance = (6371 * Math.acos
//        							( Math.cos( Math.toRadians(lat1) ) * 
//			        					Math.cos( Math.toRadians(lat2) ) * 
//			        					Math.cos( Math.toRadians(lon1) - Math.toRadians(lon2) ) + 
//			        					Math.sin( Math.toRadians(lat1) * Math.sin( Math.toRadians(lat2) ) )
//        							)
//        					);
    	
//    	double latitude1 = Double.valueOf(Location.convert(loc1.getLatitude(), Location.FORMAT_DEGREES)) ;
    	
    	
    	
        
//        double distance = Math.acos(Math.sin(lat1Rad) * Math.sin(lat2Rad) + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.cos(deltaLonRad)) * earthRadius;
        return distance; //distance in meters

    }    
 
    @Override
    public void onProviderDisabled(String provider) {
    }
 
    @Override
    public void onProviderEnabled(String provider) {
    }
 
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
 
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    
   
 
}