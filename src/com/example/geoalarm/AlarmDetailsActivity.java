package com.example.geoalarm;

import com.example.controllers.AlarmManagerHelper;
import com.example.controllers.CustomSwitch;
import com.example.controllers.TimerDistanceHelper;
import com.example.models.AlarmDBHelper;
import com.example.models.AlarmModel;
import com.example.models.GlobalVars;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AlarmDetailsActivity extends Activity {
	
	private AlarmDBHelper dbHelper = new AlarmDBHelper(this);
	
	private AlarmModel alarmDetails;
	
	private TimePicker timePicker;
	private EditText edtName;
	private EditText edtItems;
	private CustomSwitch chkWeekly;
	private CustomSwitch chkSunday;
	private CustomSwitch chkMonday;
	private CustomSwitch chkTuesday;
	private CustomSwitch chkWednesday;
	private CustomSwitch chkThursday;
	private CustomSwitch chkFriday;
	private CustomSwitch chkSaturday;
	private TextView txtToneSelection;
	private Button btn_origin, btn_destination;
	private SeekBar locRadiusSeakerBar = null;
	private TextView radiusLabel;
	
	public static long id;
	public static int p=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_ACTION_BAR);

		setContentView(R.layout.activity_alarm_details);
		
		GPSLocationDestination.actionBarCheck(getApplicationContext());
		
		id = getIntent().getExtras().getLong("id");
		
		if (id != -1) { //alarm exists, so you are editing
			getActionBar().setTitle("Edit Alarm");
		} else {
			getActionBar().setTitle("Create New Alarm");
		}

		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		initialize();
		
		onClickListenerForGPSButtons();
		
		timePicker = (TimePicker) findViewById(R.id.alarm_details_time_picker);
		edtName = (EditText) findViewById(R.id.alarm_details_name);
		edtItems = (EditText) findViewById(R.id.alarm_details_items);
		chkWeekly = (CustomSwitch) findViewById(R.id.alarm_details_repeat_weekly);
		chkSunday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_sunday);
		chkMonday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_monday);
		chkTuesday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_tuesday);
		chkWednesday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_wednesday);
		chkThursday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_thursday);
		chkFriday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_friday);
		chkSaturday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_saturday);
		txtToneSelection = (TextView) findViewById(R.id.alarm_label_tone_selection);
		locRadiusSeakerBar = (SeekBar) findViewById(R.id.set_radius);
		radiusLabel = (TextView) findViewById(R.id.radius_label);
		
		
		
		
		if (id == -1) {
			alarmDetails = new AlarmModel();
		} else {
			alarmDetails = dbHelper.getAlarm(id);
			
			String [] origin = GlobalVars.createArray(alarmDetails.loc_origin, "#");
			String [] destination = GlobalVars.createArray(alarmDetails.loc_destination, "#");
			

			GlobalVars.lat_origin = Double.parseDouble(origin[0]);
			GlobalVars.lon_origin = Double.parseDouble(origin[1]);
			GlobalVars.lat_destination =  Double.parseDouble(destination[0]);
			GlobalVars.lon_destination = Double.parseDouble(destination[1]);
			
			Toast.makeText(this, alarmDetails.loc_origin  + " +++++ " + alarmDetails.loc_destination, Toast.LENGTH_LONG).show();

			timePicker.setCurrentMinute(alarmDetails.timeMinute);
			timePicker.setCurrentHour(alarmDetails.timeHour);
			
			edtName.setText(alarmDetails.name);
			edtItems.setText(alarmDetails.items);
			
			chkWeekly.setChecked(alarmDetails.repeatWeekly);
			chkSunday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.SUNDAY));
			chkMonday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.MONDAY));
			chkTuesday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.TUESDAY));
			chkWednesday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.WEDNESDAY));
			chkThursday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.THURSDAY));
			chkFriday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.FRIDAY));
			chkSaturday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.SATURDAY));

			txtToneSelection.setText(RingtoneManager.getRingtone(this, alarmDetails.alarmTone).getTitle(this));
			
			locRadiusSeakerBar.setProgress(alarmDetails.loc_radius);
		}

		final LinearLayout ringToneContainer = (LinearLayout) findViewById(R.id.alarm_ringtone_container);
		ringToneContainer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
				startActivityForResult(intent , 1);
			}
		});
		
		locRadiusSeakerBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

		    @Override
		    public void onStopTrackingTouch(SeekBar seekBar) {
		        // TODO Auto-generated method stub
		    }

		    @Override
		    public void onStartTrackingTouch(SeekBar seekBar) {
		        // TODO Auto-generated method stub
		    }

		    @Override
		    public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
		        // TODO Auto-generated method stub
		        p=progress;
		        radiusLabel.setText("Trigger Radius: "+p +" Meters");
		        
		    }
		});
	}
	
	private void onClickListenerForGPSButtons() {
		// TODO Auto-generated method stub
		btn_origin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(AlarmDetailsActivity.this, GPSLocationDestination.class);
				
//				Toast.makeText(getApplicationContext(), "ORIGIN clicked..", Toast.LENGTH_LONG).show();
				GlobalVars.isOrigin = true;
				startActivity(intent);
			}
		});
		
		btn_destination.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				Intent intent = new Intent(AlarmDetailsActivity.this, GPSLocationDestination.class);
				
//				Toast.makeText(getApplicationContext(), "DESTINATION", Toast.LENGTH_LONG).show();
				GlobalVars.isOrigin = false;
				startActivity(intent);
			}
		});
	}

	private void initialize() {
		// TODO Auto-generated method stub
		timePicker = (TimePicker) findViewById(R.id.alarm_details_time_picker);
		edtName = (EditText) findViewById(R.id.alarm_details_name);
		chkWeekly = (CustomSwitch) findViewById(R.id.alarm_details_repeat_weekly);
		chkSunday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_sunday);
		chkMonday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_monday);
		chkTuesday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_tuesday);
		chkWednesday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_wednesday);
		chkThursday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_thursday);
		chkFriday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_friday);
		chkSaturday = (CustomSwitch) findViewById(R.id.alarm_details_repeat_saturday);
		txtToneSelection = (TextView) findViewById(R.id.alarm_label_tone_selection);
		
		btn_origin = (Button) findViewById(R.id.pick_origin_point);
		btn_destination = (Button) findViewById(R.id.pick_destination_point);
		locRadiusSeakerBar = (SeekBar) findViewById(R.id.set_radius);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK) {
	        switch (requestCode) {
		        case 1: {
		        	alarmDetails.alarmTone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
		        	txtToneSelection.setText(RingtoneManager.getRingtone(this, alarmDetails.alarmTone).getTitle(this));
		            break;
		        }
		        default: {
		            break;
		        }
	        }
	    }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.alarm_details, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
			case android.R.id.home: {
				finish();
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
			case R.id.action_save_alarm_details: {
				updateModelFromLayout();
				
				AlarmManagerHelper.cancelAlarms(this);
				
				if (alarmDetails.id < 0) {
					dbHelper.createAlarm(alarmDetails);
				} else {
					dbHelper.updateAlarm(alarmDetails);
				}
				
				AlarmManagerHelper.setAlarms(this);
				
				//start timer
				TimerDistanceHelper distanceTimer = new TimerDistanceHelper();
				distanceTimer.startTimer(getApplicationContext());
				
				setResult(RESULT_OK);
//				Intent intent = new Intent(AlarmDetailsActivity.this, AlarmListActivity.class);
//				intent.putExtra("id", id);
//				startActivityForResult(intent, 0);
				finish();
								
			}
		}

		return super.onOptionsItemSelected(item);
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
		.setIcon(R.drawable.ic_launcher)
		.setCancelable(true)
		.setNegativeButton("OK", null)
		.show();
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
		.setNegativeButton("OK", null)
		.show();
	}
	
	private void updateModelFromLayout() {		
		alarmDetails.timeMinute = timePicker.getCurrentMinute().intValue();
		alarmDetails.timeHour = timePicker.getCurrentHour().intValue();
		alarmDetails.name = edtName.getText().toString();
		alarmDetails.items = edtItems.getText().toString();
		alarmDetails.loc_origin = GlobalVars.lat_origin+"#"+GlobalVars.lon_origin+"#";
		alarmDetails.loc_destination = GlobalVars.lat_destination+"#"+GlobalVars.lon_destination+"#";
		alarmDetails.loc_radius = p;
		alarmDetails.repeatWeekly = chkWeekly.isChecked();	
		alarmDetails.setRepeatingDay(AlarmModel.SUNDAY, chkSunday.isChecked());	
		alarmDetails.setRepeatingDay(AlarmModel.MONDAY, chkMonday.isChecked());	
		alarmDetails.setRepeatingDay(AlarmModel.TUESDAY, chkTuesday.isChecked());
		alarmDetails.setRepeatingDay(AlarmModel.WEDNESDAY, chkWednesday.isChecked());	
		alarmDetails.setRepeatingDay(AlarmModel.THURSDAY, chkThursday.isChecked());
		alarmDetails.setRepeatingDay(AlarmModel.FRIDAY, chkFriday.isChecked());
		alarmDetails.setRepeatingDay(AlarmModel.SATURDAY, chkSaturday.isChecked());
		alarmDetails.isEnabled = true;
	}
	
}