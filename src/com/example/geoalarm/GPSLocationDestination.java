package com.example.geoalarm;

import java.lang.reflect.Field;

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
import com.example.models.AlarmDBHelper;
import com.example.models.AlarmModel;
import com.example.models.GlobalVars;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.Toast;

public class GPSLocationDestination extends Activity{
	
	GoogleMap map;

	GPSTracker gpsTracker;
	MarkerOptions locator_new, location_existing_origin, location_existing_dest;
	
	boolean marker_added = false;
	LatLng newLatLng = null;
	LatLng center_position;
	
	private ProgressDialog pDialog;

	public static double latitude, longitude;
	
	private AlarmModel alarmDetails;
	
	boolean alarm_exists;
	
	private AlarmDBHelper dbHelper = new AlarmDBHelper(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		
		setContentView(R.layout.maps_gps);
		
		actionBarCheck(getApplicationContext());
		
		if (GlobalVars.isOrigin) {
			getActionBar().setTitle(GlobalVars.title_pick_origin);
		} else if (!GlobalVars.isOrigin){
			getActionBar().setTitle(GlobalVars.title_pick_dest);
		}
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		initialise();
				
		populateLatsAndLongs();
		
		myMapListener();	
		
	}
	
	public static void actionBarCheck(Context ctxt) {
		// TODO Auto-generated method stub
		// Force show overflow menu, if this is left as the default then
	     //the overflow menu items will be attached to the hardware button for menu
		try {
            ViewConfiguration config = ViewConfiguration.get(ctxt);
            //field is of type java.lang.reflect.Field 
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // y ou can choose to display the exception
        }
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
		
		ifNewAlarmOrNot();
		
			map.setOnMapClickListener(new OnMapClickListener() {				
				@Override
				public void onMapClick(LatLng arg0) {
					// TODO Auto-generated method stub
					
					BitmapDescriptor icon_drag_new_location = BitmapDescriptorFactory.fromResource(R.drawable.marker);
					
					if (marker_added) { //if this is the 1st time to click on map i.e add marker location (origin or destination)...
						
	
						
					 if (!alarm_exists) { //alarm is new, so you are starting, so location NOT exists
							Log.i("newnewnewn", "taken as new");
							locator_new = new MarkerOptions()
												.position(arg0)
												.title("DRAG origin")
												.snippet("Drag me 1")
												.icon(icon_drag_new_location);
						
							map.addMarker(locator_new).setDraggable(true);	
						}
					 else if (alarm_exists) { //alarm not new, so clear map first
						 	map.clear();
							
							locator_new = new MarkerOptions()
													.position(arg0)
													.title("DRAG origin")
													.snippet("Drag me 1")
													.icon(icon_drag_new_location);
													
							map.addMarker(locator_new).setDraggable(true);
					}
						
						
						
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

	private boolean ifNewAlarmOrNot() {
		// TODO Auto-generated method stub
		/**if id != -1 i.e not new, then take the previous location and 
		 * create a amarker for it
		 * 
		 * Also check if its origin or destination clicked
		 * 
		 * **/
		
		alarm_exists = false;
		BitmapDescriptor icon_drag_new_location = BitmapDescriptorFactory.fromResource(R.drawable.marker);
		
		if (AlarmDetailsActivity.id != -1) { //alarm exists, so you are editing, so location exists
			alarm_exists = true;
			alarmDetails = dbHelper.getAlarm(AlarmDetailsActivity.id);
			
			if (GlobalVars.isOrigin) { //origin clicked, pick origin location & create marker
												
				String [] origin = GlobalVars.createArray(alarmDetails.loc_origin, "#");
				
				Log.i("existingORIGIN", Double.parseDouble(origin[0]) + "and " + Double.parseDouble(origin[1]));
				
				//create a latlong value
				LatLng exist_origin = new LatLng(Double.parseDouble(origin[0]), Double.parseDouble(origin[1]));
				
				//create a marker
				location_existing_origin = new MarkerOptions()
												.position(exist_origin)
												.title("Origin")
												.snippet("Drag me origin")
												.icon(icon_drag_new_location);
						
				map.addMarker(location_existing_origin).setDraggable(true);								
				
				
			} else { //destination clicked
				
				String [] destination = GlobalVars.createArray(alarmDetails.loc_destination, "#");
				Log.i("existingDESTINATION", Double.parseDouble(destination[0]) + "and " + Double.parseDouble(destination[1]));
				//create a latlong value
				LatLng exist_origin = new LatLng(Double.parseDouble(destination[0]), Double.parseDouble(destination[1]));
				
				//create a marker
				location_existing_dest = new MarkerOptions()
												.position(exist_origin)
												.title("Destination")
												.snippet("Drag to destination")
												.icon(icon_drag_new_location);
						
				map.addMarker(location_existing_dest).setDraggable(true);									
				
			}
		}
		return alarm_exists;
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
			
		case R.id.action_help:
			showHelpDialog();
			return true;
		
		case R.id.action_done:
			
			//get the location of the marker clicked..
			getFinalLocationClicked();
						
			finish();
			return true;
		
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void showAboutDialog() {
		// TODO Auto-generated method stub
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Geo Alarm is a geographical-aware alarm. \n" +
				"You are given the choice to select a point of origin " +
				"and destination from Google Maps and a radius outside which your alarm will " +
				"be triggered to remind you of the items in your alarm.\n" +
				"This happens on top of the usual alarm setup.")
		.setTitle("ABOUT ")
		.setCancelable(true)
		.setNegativeButton("OK", null)
		.setIcon(R.drawable.ic_launcher)
		.setPositiveButton("Help", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				showHelpDialog();
			}
		}).show();
	}
	
	
	


	private void showHelpDialog() {
		// TODO Auto-generated method stub
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("1. To set a new alarm, click on the plus (+) icon at the homepage menu bar.\n" +
				"2. To edit an existing alarm, click on the alarm from the list and edit its items as you desire.\n" +
				"3. To add a location (origin/destination), tap on the desired location on the map. Click 'Done' once done.\n" +
				"4. To add a location by dragging themarker, long click on the marker for 2secs then start dragging.\n" +
				"5. To delete an alarm, long click on the alarm then select 'OK' on the dialogue box that appears.\n")
		.setTitle("HELP ")
		.setIcon(R.drawable.ic_launcher)
		.setCancelable(true)
		.setNegativeButton("OK", null)
		.show();
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

			Toast.makeText(this, "ORIGIN:: Lat is: " + GlobalVars.lat_origin + " Lon is: " + GlobalVars.lon_origin, Toast.LENGTH_LONG).show();
			Log.i("DESTINATION: ", "Lat is: " + GlobalVars.lon_origin + " Lon is: " + GlobalVars.lon_origin);

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