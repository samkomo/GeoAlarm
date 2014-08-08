package com.example.geoalarm;

import com.example.models.AlarmModel;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Build;

public class AlarmDetailsActivity extends Activity {

	private AlarmModel alarmDetails;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm_details);

		alarmDetails = new AlarmModel();
		
		getActionBar().setTitle("Create New Alarm");
	    getActionBar().setDisplayHomeAsUpEnabled(true);
	    
	    final LinearLayout ringToneContainer = (LinearLayout) findViewById(R.id.alarm_ringtone_container);
	    ringToneContainer.setOnClickListener(new OnClickListener() {
	     
	        @Override
	        public void onClick(View v) {
	            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
	            startActivityForResult(intent , 1);
	        }
	    });
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
	        switch (requestCode) {
		        case 1: {
		        	alarmDetails.alarmTone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

		        	TextView txtToneSelection = (TextView) findViewById(R.id.alarm_label_tone_selection);
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

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.alarm_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	

}
