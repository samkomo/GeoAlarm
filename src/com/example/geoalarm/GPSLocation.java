package com.example.geoalarm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.controllers.GPSTracker;
import com.example.geoalarm.R;
import com.example.models.GlobalVars;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class GPSLocation extends Activity{
	
	GoogleMap map;
//	private static LatLng TRM;
//	private static LatLng Junction;
//	private static LatLng Tajmal;
//	private static LatLng TMall;
	GPSTracker gpsTracker;
	MarkerOptions locator_new;
	
	boolean marker_added = false;
	LatLng newLatLng = null;
	LatLng center_position;
	
//	private static final String url = MyPassedValues.url_malls_navdrawer;
	private ProgressDialog pDialog;
//	JSONParser jparser = new JSONParser();
//	JSONArray  items;
//	public static CustomListViewMallsAdapter jSONAdapter;
//	
//	
//	public static String KEY_IMAGE = Home.KEY_IMAGE, 
//							KEY_ID = Home.KEY_ID,
//								KEY_NAME = Home.KEY_NAME,
//									KEY_LONGITUDE = Home.KEY_LONGITUDE,
//										KEY_LATITUDE = Home.KEY_LATITUDE,
//											KEY_SNIPPET = Home.KEY_SNIPPET;
	
//	private double latitude = 0.00, longitude = 0.00;
	public static double latitude, longitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps_gps);
		
		initialise();
		
		setTitle("Mall Locations");
		
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
						
						//add marker for your GS location
						map.addMarker(new MarkerOptions()
											.position(center_position)
											.title("ME")
											.snippet("I am here")
											.icon(icon_gps_current_location));
						
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
    	
    	JSONObject json;
    	ArrayList<HashMap<String, String>> arrayListAllMarkerDetails = new ArrayList<>();
    	   	
    	
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(GPSLocation.this);
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
    			Log.e("log_tag", "Error kupitisha data when getting data for markers" + e.toString());
    		}
    		
    		
    		return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
           

            BitmapDescriptor icon_gps_current_location = BitmapDescriptorFactory.fromResource(R.drawable.marker_gps);
            BitmapDescriptor icon_drag_new_location = BitmapDescriptorFactory.fromResource(R.drawable.marker);
            center_position = new LatLng(latitude, longitude);
			
			//add marker for your GS location
			map.addMarker(new MarkerOptions()
								.position(center_position)
								.title("ME")
								.snippet("I am here")
								.icon(icon_gps_current_location));	
			
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
		gpsTracker = new GPSTracker(GPSLocation.this);
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
		
		case R.id.action_done:
			Toast.makeText(this, "Take location of new marker dragged and use in service", Toast.LENGTH_LONG).show();
			
			Intent intent = new Intent(GPSLocation.this, AlarmDetailsActivity.class);
			long id_global = AlarmDetailsActivity.id;
			intent.putExtra("id", id_global);
			
			//get the location of the marker clicked..
			getFinalLocationClicked();
			
			startActivityForResult(intent, 0);
			
			return true;
		
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void getFinalLocationClicked() {
		// TODO Auto-generated method stub
		LatLng latlongfianl = locator_new.getPosition();
		long latitude_picked = (long) latlongfianl.latitude;
		long longitude_picked = (long) latlongfianl.longitude;
		
	}

	//menu items 
	//END
	

}
