package com.example.controllers;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.example.services.GPSTracker;

public class TimerHelper {
	
	Timer timer;
	TimerTask timerTask;
    final Handler handler = new Handler();
	
	public void startTimer(final Context mContext) {
		// TODO Auto-generated method stub
		timer = new Timer();
		
		//initialize the TimerTask's job
		initializeTimerTask(mContext);
		
		//schedule the timer, after the first 30000ms the TimerTask will run every 10000ms
		timer.schedule(timerTask, 20000, 22000);
	}

	public void initializeTimerTask(final Context mContext) {
		// TODO Auto-generated method stub
		timerTask = new TimerTask() {			
			@Override
			public void run() {
				// TODO Auto-generated method stub				
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
//						String distDestination = Double.toString(GPSTracker.theDistanceFromDestination);
//						String distOrigin = Double.toString(GPSTracker.theDistanceFromOrigin);
						
						String distDestination = Double.toString(GPSTracker.theDistanceFromDestination);
						String distOrigin = Double.toString(GPSTracker.theDistanceFromOrigin);
						
						//show the toast
						int duration = Toast.LENGTH_SHORT;  
//						Toast toastDestination = Toast.makeText(mContext, "Distance to Dest. (timer task): " + distDestination, duration);
//						toastDestination.show();
//						Toast toastOrigin = Toast.makeText(mContext, "Distance to Dest. (timer task): " + distOrigin, duration);
//						toastOrigin.show();
						
						Toast toastOrigin = Toast.makeText(mContext, "distDestination" + distDestination + "distOrigin" + distOrigin, duration);
						toastOrigin.show();
						//gpsTracker2.showSettingsAlertPassMessage("Distance is (timer task): " + dist);

					}
				});
			}
		};
	}


}
