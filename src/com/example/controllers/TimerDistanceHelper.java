package com.example.controllers;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.models.AlarmDBHelper;
import com.example.models.AlarmModel;
import com.example.models.GlobalVars;
import com.example.services.AlarmService;
import com.example.services.GPSTracker;

public class TimerDistanceHelper {
	
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String ITEMS = "items";
	public static final String TIME_HOUR = "timeHour";
	public static final String TIME_MINUTE = "timeMinute";
	public static final String TONE = "alarmTone";

	Timer timer;
	TimerTask timerTask;
    final Handler handler = new Handler();
    
    GPSTracker gpsTracker;
	double gpsLatitude, gpsLongitude;
	public static float[] results = new float[5];
	public static double mStartLat;
	public static double mStartLon;
	public static double mEndLat;
	public static double mEndLon;
	public static double myGPSLat;
	public static double myGPSLon;
	public static String distance_from_php = null;
	
	public static double theDistanceFromOrigin, theDistanceFromDestination;

	public void startTimer(Context context) {
		// TODO Auto-generated method stub
		timer = new Timer();
		
		//initialize the TimerTask's job
		initializeTimerTask(context);
		
		//schedule the timer, after the first 30000ms the TimerTask will run every 10000ms
		timer.schedule(timerTask, 20000, 22000);
	}
	
	public double timerFunction(Context ctxt){
	   	
	   	
	   	GPSTracker track = new GPSTracker(ctxt);
	   	
	   	AlarmDBHelper dbHelper = new AlarmDBHelper(ctxt);
//	   	AlarmModel aModel = new AlarmModel();
//	   	aModel = dbHelper.getAlarm(0);
	   	

	   	List<AlarmModel> alarms =  dbHelper.getAlarms();
		
		for (AlarmModel alarm : alarms) {
			if (alarm.isEnabled && alarm.loc_radius > 10) {
				Log.i("From Database", alarm.loc_origin);
				
//			   	get the new location, poll for the lat and longitude, get distance, calculate with
			   	mStartLat = GlobalVars.lat_origin;
			   	mStartLon = GlobalVars.lon_origin;
			   	mEndLat = GlobalVars.lat_destination;
			   	mEndLon = GlobalVars.lon_destination;
			   	myGPSLat =  track.getLocation().getLatitude();
			   	myGPSLon = track.getLocation().getLongitude();
			   	results = new float[5];
			   	
			   	
			   	Location.distanceBetween( mStartLat, mStartLon, myGPSLat, myGPSLon, results);
			   	
			   	
			   	Location loc = new Location("LocationOrigin");
			   	loc.setLatitude(mStartLat);
			   	loc.setLongitude(mStartLon);
			   	
			   	Log.i("ORIGIN Vals:GPS: ", "lat: " + myGPSLat + " lon: " + myGPSLon);
			   	Log.i("ORIGIN Vals Origin: ", "lat: " + mStartLat + " lon: " + mStartLon);
			   	
			   	Log.i("DEST Vals:GPS: ", "lat: " + myGPSLat + " lon: " + myGPSLon);
			   	Log.i("DEST Vals Origin: ", "lat: " + mEndLat + " lon: " + mEndLon);
			   	    	    	
			   	theDistanceFromOrigin = track.CalculationByDistance(mStartLat, mStartLon, myGPSLat, myGPSLon);
//			   	theDistance = CalculationByDistance(-1.038147, 37.082634, -1.281269, 36.822214);
			   	theDistanceFromDestination = track.CalculationByDistance(mEndLat, mEndLon, myGPSLat, myGPSLon);
			   	
			   	Log.i("VALUES; theDistance is: ", "distance = " + theDistanceFromOrigin);
			   	Log.i("VALUES; theDistance is: ", "distance = " + theDistanceFromDestination);
			   	
			   	Log.i("loc_radius: ", "distance = " + alarm.loc_radius);
			   	
			   	if ( theDistanceFromOrigin > alarm.loc_radius ) {
			   		Log.i("loc_radius: ", "distance = " + alarm.loc_radius);
//			   		PendingIntent pIntent = createPendingIntent(ctxt, alarm);
			   		Intent intent = new Intent(ctxt, AlarmScreen.class);
					intent.putExtra(ID, alarm.id);
					intent.putExtra(NAME, alarm.name);
					intent.putExtra(ITEMS, alarm.items);
					intent.putExtra(TIME_HOUR, alarm.timeHour);
					intent.putExtra(TIME_MINUTE, alarm.timeMinute);
					intent.putExtra(TONE, alarm.alarmTone.toString());
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					ctxt.startActivity(intent);
					
					Log.i("Should show intent ", "distance = " + theDistanceFromDestination);
				}
			}
		}
	   	
	   	

		
	   	
	   	return theDistanceFromOrigin;
		   
	   }

	private static PendingIntent createPendingIntent(Context context, AlarmModel model) {
		Intent intent = new Intent(context, AlarmService.class);
		intent.putExtra(ID, model.id);
		intent.putExtra(NAME, model.name);
		intent.putExtra(ITEMS, model.items);
		intent.putExtra(TIME_HOUR, model.timeHour);
		intent.putExtra(TIME_MINUTE, model.timeMinute);
		intent.putExtra(TONE, model.alarmTone.toString());
		
		
		return PendingIntent.getService(context, (int) model.id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	private void initializeTimerTask(final Context ctxt) {
		// TODO Auto-generated method stub
		timerTask = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Double dist = timerFunction(ctxt);
						
						//show the toast
//						int duration = Toast.LENGTH_SHORT;  
//						Toast toast = Toast.makeText(this, "Distance to Dest. (timer task): " + dist, duration);
//						toast.show();	
						Log.i("Orin distance", String.valueOf(dist));
						//gpsTracker2.showSettingsAlertPassMessage("Distance is (timer task): " + dist);

					}
				});
			}
		};
	}
}
