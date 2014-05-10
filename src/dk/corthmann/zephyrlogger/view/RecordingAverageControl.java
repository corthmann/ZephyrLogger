package dk.corthmann.zephyrlogger.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;
import dk.corthmann.zephyrlogger.R;
import dk.corthmann.zephyrlogger.hxm.HrmReading;

public class RecordingAverageControl extends RelativeLayout{
	// Views
	TextView mHeartRate;
	TextView mSpeed;
	TextView mDistance;
	TextView mStrides;
	// Sum and Counts
	int heartRateCount, speedCount, distanceCount;
	int heartRateSum;
	int speedSum;
	long distanceTotal, previousDistance;
	int stridesTotal, previousStrides;
	
	public RecordingAverageControl(Context context) {
		super(context);
		init(context);
	}

	public RecordingAverageControl(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
		
	private void init(Context context){
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.recording_average_control, this, true);
		loadViews();
		heartRateSum = 0; heartRateCount = 0;
		speedSum = 0; speedCount = 0;
		distanceTotal = 0; previousDistance = 0;
		stridesTotal = 0; previousStrides = 0;
	}

	private void loadViews() {
		mHeartRate = (TextView)findViewById(R.id.average_heart_rate_text);
		mSpeed = (TextView)findViewById(R.id.average_speed_text);
		mDistance = (TextView)findViewById(R.id.average_distance_text);
		mStrides = (TextView)findViewById(R.id.average_strides_text);
	}
	
	/* Setters */
	public void setRecordingAverage(HrmReading h){
		setHeartRate(h.heartRate);
		setSpeed(h.speed);
		setDistance(h.distance);
		setStrides(h.strides);
	}
	
	public void setHeartRate(int heartRate){
		heartRateSum += heartRate; heartRateCount++;
		mHeartRate.setText((heartRateSum / heartRateCount)+" bpm");
	}
	
	public void setSpeed(long speed){
		int kmh = Math.round((float) (speed*(1.0/256)*3.6));
		speedSum += kmh; speedCount++;
		mSpeed.setText((speedSum / speedCount)+" km/h");
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
	
	public void setStrides(byte strides){
		// range: 0-255
		int st = strides;
		// Handle rollover
		if(st < previousStrides){
			stridesTotal += st + (255 % previousStrides);
		} else {
			stridesTotal += (st-previousStrides);
		}
		previousStrides = st;
//		String INT_FORMAT = "%x";
//		String s = String.format(INT_FORMAT, strides);
		mStrides.setText(stridesTotal+"");
	}
}
