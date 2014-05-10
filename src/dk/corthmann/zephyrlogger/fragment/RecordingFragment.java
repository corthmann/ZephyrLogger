package dk.corthmann.zephyrlogger.fragment;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import dk.corthmann.zephyrlogger.EventSource;
import dk.corthmann.zephyrlogger.R;
import dk.corthmann.zephyrlogger.ZephyrLoggerContext;
import dk.corthmann.zephyrlogger.hxm.HrmReading;
import dk.corthmann.zephyrlogger.view.RecordingAverageControl;

public class RecordingFragment extends Fragment implements Observer {
	private View layout;
	private ZephyrLoggerContext mContext;
	private EventSource mData;

	private Button toggleRecordButton;
	private RecordingAverageControl recordingAverage;
	private TextView timer;
	private long startTime;

	public static RecordingFragment newInstance(String content) {
		RecordingFragment fragment = new RecordingFragment();

		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = (ZephyrLoggerContext) activity.getApplicationContext();
		mData = mContext.getEventSource();
	}

	@Override
	public void onStart() {
		super.onStart();
		mData.addObserver(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate Layout
		layout = inflater.inflate(R.layout.recording, container, false);

		// Load button and attach listener
		toggleRecordButton = (Button)layout.findViewById(R.id.toggle_record_button);
		toggleRecordButton.setOnClickListener(toggleRecordHandler);

		// Load timer
		timer = (TextView)layout.findViewById(R.id.timer_text);

		// Load compound controls
		recordingAverage = (RecordingAverageControl)layout.findViewById(R.id.recording_average);

		return layout;
	}

	View.OnClickListener toggleRecordHandler = new View.OnClickListener() {
		public void onClick(View v) {
			toggleRecordButton.post(new Runnable(){
				public void run(){
					// set recording status
					mContext.setRecording(
							(mContext.isRecording() ? false : true)
							);

					if(mContext.isRecording()){ // Start watch !
						startTime = System.currentTimeMillis();
						timer.setText(currentTimeString());
						mContext.setRecordingTag(startTime+"");
						
					}
					
					// set button text
					toggleRecordButton.setText(
							(mContext.isRecording() ? R.string.record_button_stop : R.string.record_button_start)
							);
				}
			});
		}
	};

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void update(Observable obs, Object data) {		
		final HrmReading h = (HrmReading) data;
		recordingAverage.post(new Runnable(){
			public void run(){
				recordingAverage.setRecordingAverage(h);
			}
		});
		// Update timer. Connecting the timer to the HrmReadings increases performance,
		// but means that the timer halts if the bluetooth connection is broken.
		if(mContext.isRecording()){
			timer.post(new Runnable(){
				public void run(){
					timer.setText(currentTimeString());
				}
			});
		}
	}

	public String currentTimeString() {
		long interval = System.currentTimeMillis() - startTime;
		int tens = (int) interval;
		int seconds = (int) interval / 1000;
		int minutes = seconds / 60;
		int hours = minutes / 60;
		tens = tens % 10;
		seconds = seconds % 60;
		return String.format("%d:%02d:%02d.%d", hours, minutes, seconds, tens);
	}

}
