package com.example.geoalarm;

import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.services.GPSTracker;
import com.example.geoalarm.R;
import com.example.models.GlobalVars;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

public class GPSLocationDestination extends Activity{
	
	GoogleMap map;

	GPSTracker gpsTracker;
	MarkerOptions locator_new;
	
	boolean marker_added = false;
	LatLng newLatLng = null;
	LatLng center_position;
	
	private ProgressDialog pDialog;

	public static double latitude, longitude;
	
	Timer timer;
	TimerTask timerTask;
    final Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		
		setContentView(R.layout.maps_gps);
		
		getActionBar().setTitle("Pick point of destination");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		initialise();
				
		populateLatsAndLongs();
		
		myMapListener();	
		
	}

	private void populateLatsAndLongs() {
		// TODO Auto-generated method stub
		
		getGPSLocation();
		
		GetLatLongValues asyncTask = new GetLatLongValues();
		asyncTask.execute();
		
	}

	private void myMapListener() {
		// TODO Auto-generated method stub
		newLatLng = null;
		marker_added = true;
		
			map.setOnMapClickListener(new OnMapClickListener() {				
				@Override
				public void onMapClick(LatLng arg0) {
					// TODO Auto-generated method stub
					
					BitmapDescriptor icon_drag_new_location = BitmapDescriptorFactory.fromResource(R.drawable.marker);
					BitmapDescriptor icon_gps_current_location = BitmapDescriptorFactory.fromResource(R.drawable.marker_gps);
					
					if (marker_added) { //if this is the 1st time to click on map i.e add marker location (origin or destination)...
						
						locator_new = new MarkerOptions()
												.position(arg0)
												.title("DRAG origin")
												.snippet("Drag me 1")
												.icon(icon_drag_new_location);
						
						map.addMarker(locator_new).setDraggable(true);		
						
						marker_added = false;
						
					} else if (!marker_added) { //if map is clicked again just to move th location marker...
						Log.i("is it 1st time??", "NO");
					
						map.clear();
						
						locator_new = new MarkerOptions()
												.position(arg0)
												.title("DRAG origin")
												.snippet("Drag me 1")
												.icon(icon_drag_new_location);
												
						map.addMarker(locator_new).setDraggable(true);
					}					
				}	
			});		
	}

	private void initialise() {
		// TODO Auto-generated method stub
		map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
				
	}
	
	//from here
	 /**
     * Async task class to get json by making HTTP call
     * */
    private class GetLatLongValues extends AsyncTask<Void, Void, Void> {
    	    	
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(GPSLocationDestination.this);
            pDialog.setMessage("Loading map, please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
 
        }
 
        @Override
        protected Void doInBackground(Void... arg0) {
    		
    		try {
    			
                return null;
    			
    		} catch (Exception e) {
    			// TODO: handle exception
    			Log.e("log_tag_dest", "Error kupitisha data when getting data for markers" + e.toString());
    		}
    		
    		
    		return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
                       
            center_position = new LatLng(latitude, longitude);
            
            map.setMyLocationEnabled(true);
			
			CameraPosition cameraPosition = new CameraPosition.Builder()
								.target(center_position) // Sets the center of the map to
										// Mountain View
								.zoom(10) // Sets the zoom
								// .bearing(90) // Sets the orientation of the camera to
								// east
								// .tilt(30) // Sets the tilt of the camera to 30 degrees
								.build(); // Creates a CameraPosition from the builder
			
			map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	
			// Dismiss the progress dialog
		    if (pDialog.isShowing())
		        pDialog.dismiss();
            
        }//end of onPostExecute
 
    }
	//to here

	public void getGPSLocation() {
		// TODO Auto-generated method stub
		gpsTracker = new GPSTracker(GPSLocationDestination.this);
		if (gpsTracker.canGetLocation()) { //gps enabled...
			
			latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
             
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show(); 
		} else {
			
			gpsTracker.showSettingsAlert();

		}
	}
	
	
	//menu items 
	//START
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.alarm_donepickinglocation, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		
		case android.R.id.home: 
			finish();
			return true;
		
		case R.id.action_done:
//			Toast.makeText(this, "Take location of new marker dragged and use in service", Toast.LENGTH_LONG).show();
			
			//get the location of the marker clicked..
			getFinalLocationClicked();
			
			Log.i("Distance is:: (newphpphp) (distanceTO) ", Double.toString(GPSTracker.theDistanceFromDestination));
			
			//*** store Double.toString(GPSTracker.theDistance) in SQLITE
			//*** Start timerTask to calculcate distance
			//SET TIMER
			startTimer();
			
			Intent intent = new Intent(GPSLocationDestination.this, AlarmDetailsActivity.class);
			long id_global = AlarmDetailsActivity.id;
			intent.putExtra("id", id_global);
			
			startActivityForResult(intent, 0);
			finish();
			return true;
		
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void startTimer() {
		// TODO Auto-generated method stub
		timer = new Timer();
		
		//initialize the TimerTask's job
		initializeTimerTask();
		
		//schedule the timer, after the first 30000ms the TimerTask will run every 10000ms
		timer.schedule(timerTask, 20000, 22000);
	}

	private void initializeTimerTask() {
		// TODO Auto-generated method stub
		timerTask = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						String dist = Double.toString(GPSTracker.theDistanceFromDestination);
						
						//show the toast
						int duration = Toast.LENGTH_SHORT;  
						Toast toast = Toast.makeText(getApplicationContext(), "Distance to Dest. (timer task): " + dist, duration);
						toast.show();						
						//gpsTracker2.showSettingsAlertPassMessage("Distance is (timer task): " + dist);

					}
				});
			}
		};
	}

	private void getFinalLocationClicked() {
		// TODO Auto-generated method stub
		if (GlobalVars.isOrigin) {
			
			LatLng latlongfianl = locator_new.getPosition();
			double latitude_picked = (double) latlongfianl.latitude;
			GlobalVars.lat_origin = latitude_picked;
			double longitude_picked = (double) latlongfianl.longitude;
			GlobalVars.lon_origin = longitude_picked;
			
			
			Toast.makeText(this, "ORIGIN:: Lat is: " + GlobalVars.lat_origin + " Lon is: " + GlobalVars.lon_origin, Toast.LENGTH_LONG).show();
			Log.i("DESTINATION: ", "Lat is: " + GlobalVars.lat_origin + " Lon is: " + GlobalVars.lon_origin);
		} else {
			LatLng latlongfianl = locator_new.getPosition();
			double latitude_picked = (double) latlongfianl.latitude;
			GlobalVars.lat_destination = latitude_picked;
			double longitude_picked = (double) latlongfianl.longitude;
			GlobalVars.lon_destination = longitude_picked;
			
			Toast.makeText(this, "DESTINATION:: Lat is: " + GlobalVars.lat_destination + " Lon is: " + GlobalVars.lon_destination, Toast.LENGTH_LONG).show();
			Log.i("DESTINATION: ", "Lat is: " + GlobalVars.lat_destination + " Lon is: " + GlobalVars.lon_destination);

		}
		
	}

	//menu items 
	//END
	

}
