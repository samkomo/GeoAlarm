package com.example.geoalarm;

import com.example.controllers.AlarmListAdapter;
import com.example.controllers.AlarmManagerHelper;
import com.example.models.AlarmDBHelper;
import com.example.models.AlarmModel;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AlarmListActivity extends ListActivity {

	private AlarmListAdapter mAdapter;
	private AlarmDBHelper dbHelper = new AlarmDBHelper(this);
	private Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = this;
		
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		
		setContentView(R.layout.activity_alarm_list);
		
		GPSLocationDestination.actionBarCheck(getApplicationContext());
		
		//HELP here
		showAboutDialog();

		mAdapter = new AlarmListAdapter(this, dbHelper.getAlarms());
		
		setListAdapter(mAdapter);
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
		.setIcon(R.drawable.ic_launcher)
		.setNegativeButton("OK", null)
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
		.setCancelable(true)
		.setIcon(R.drawable.ic_launcher)
		.setNegativeButton("OK", null)
		.show();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.alarm_list, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.action_add_new_alarm: {
				startAlarmDetailsActivity(-1);
				break;
			}
			case R.id.action_about: {
				showAboutDialog();
				break;
			}
			case R.id.action_help: {
				showHelpDialog();
				break;
			}
			case R.id.action_review: {
				showReview();
				break;
			}
		}

		return super.onOptionsItemSelected(item);
	}
	
	private void showReview() {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
				AlertDialog.Builder alert = new AlertDialog.Builder(this);

				LinearLayout ll = new LinearLayout(this);
				ll.setOrientation(1);
				
				final EditText input_comment = new EditText(this);
				final EditText input_yourname = new EditText(this);
				
				input_comment.setHint("Comment/feedback here..");
				input_yourname.setHint("Name here..");
//				ll.addView(input_comment_label);
				ll.addView(input_comment);
//				ll.addView(input_yourname_label);
				ll.addView(input_yourname);
				
				alert.setView(ll);
				alert.setTitle("Feedback");
				alert.setIcon(R.drawable.ic_launcher);
				

				alert.setPositiveButton("Post", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					if (input_comment.length() < 1) {
						Toast.makeText(getApplicationContext(), "Not posted. Blank comment", Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(getApplicationContext(), "Thank you! Feedback sent", Toast.LENGTH_LONG).show();
					}
					
					 
				  }
				});

				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				  public void onClick(DialogInterface dialog, int whichButton) {
				   
				  }
				});

				alert.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK) {
	        mAdapter.setAlarms(dbHelper.getAlarms());
	        mAdapter.notifyDataSetChanged();
	    }
	}
	
	public void setAlarmEnabled(long id, boolean isEnabled) {
		AlarmManagerHelper.cancelAlarms(this);
		
		AlarmModel model = dbHelper.getAlarm(id);
		model.isEnabled = isEnabled;
		dbHelper.updateAlarm(model);
		
		AlarmManagerHelper.setAlarms(this);
	}

	public void startAlarmDetailsActivity(long id) {
		Intent intent = new Intent(this, AlarmDetailsActivity.class);
		intent.putExtra("id", id);
		startActivityForResult(intent, 0);
	}
	
	public void deleteAlarm(long id) {
		final long alarmId = id;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Please confirm")
		.setTitle("Delete set?")
		.setCancelable(true)
		.setNegativeButton("Cancel", null)
		.setPositiveButton("Ok", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//Cancel Alarms
				AlarmManagerHelper.cancelAlarms(mContext);
				//Delete alarm from DB by id
				dbHelper.deleteAlarm(alarmId);
				//Refresh the list of the alarms in the adaptor
				mAdapter.setAlarms(dbHelper.getAlarms());
				//Notify the adapter the data has changed
				mAdapter.notifyDataSetChanged();
				//Set the alarms
				AlarmManagerHelper.setAlarms(mContext);
			}
		}).show();
	}
	
	
}
