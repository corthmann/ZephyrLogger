package dk.corthmann.zephyrlogger.view;

import dk.corthmann.zephyrlogger.R;
import dk.corthmann.zephyrlogger.hxm.HrmReading;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BasicStatusControl extends RelativeLayout {
	TextView mHeartRate;
	TextView mSpeed;
	TextView mDistance;
	long distanceTotal, previousDistance;
	
	public BasicStatusControl(Context context) {
		super(context);
		init(context);
	}

	public BasicStatusControl(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
		
	private void init(Context context){
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.basic_status_control, this, true);
		distanceTotal = 0; previousDistance = 0;
		loadViews();
	}

	private void loadViews() {
		mHeartRate = (TextView)findViewById(R.id.heart_rate_text);
		mSpeed = (TextView)findViewById(R.id.speed_text);
		mDistance = (TextView)findViewById(R.id.distance_text);
	}
	
	/* Setters */
	public void setBasicStatus(HrmReading h){
		setHeartRate(h.heartRate);
		setSpeed(h.speed);
		setDistance(h.distance);
	}
	
	public void setHeartRate(int heartRate){
		mHeartRate.setText(heartRate+" bpm");
	}
	
	public void setSpeed(long speed){
		int kmh = Math.round((float) (speed*(1.0/256)*3.6));
		mSpeed.setText(kmh+" kmh");
	}
	
	public void setDistance(long distance){
		// Handle rollover
		distance = (long)Math.floor(distance * (1.0/16)); // convert to meters
		if(distance < previousDistance){
			distanceTotal += distance + (256 % previousDistance);
		} else {
			distanceTotal += (distance-previousDistance);
		}
		previousDistance = distance;
		String INT_FORMAT = "%d";
		String s = String.format(INT_FORMAT, distanceTotal);
		mDistance.setText(s+" m");
	}
}
