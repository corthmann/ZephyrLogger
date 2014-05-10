package dk.corthmann.zephyrlogger.fragment;

import java.util.Observable;
import java.util.Observer;

import dk.corthmann.zephyrlogger.EventSource;
import dk.corthmann.zephyrlogger.R;
import dk.corthmann.zephyrlogger.ZephyrLoggerContext;
import dk.corthmann.zephyrlogger.hxm.HrmReading;
import dk.corthmann.zephyrlogger.view.BasicStatusControl;
import dk.corthmann.zephyrlogger.view.BluetoothIndicatorControl;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class StatusFragment extends Fragment implements Observer {
	
	private ZephyrLoggerContext mContext;
	private EventSource mData;
	
	private View layout;
	private BasicStatusControl basicStatus;
	private BluetoothIndicatorControl bluetoothIndicator;
	
    public static StatusFragment newInstance(String content) {
    	StatusFragment fragment = new StatusFragment();

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
    	layout = inflater.inflate(R.layout.status, container, false);
    	
    	// Load compound controls
    	basicStatus = (BasicStatusControl)layout.findViewById(R.id.basic_status);
    	bluetoothIndicator = (BluetoothIndicatorControl)layout.findViewById(R.id.bluetooth_indicator);
    	
    	bluetoothIndicator.setBluetoothStatus(mContext.isConnectedToZephyr());
    	
        return layout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

	@Override
	public void update(Observable observable, Object data) {
		final HrmReading h = (HrmReading) data;
		basicStatus.post(new Runnable(){
			public void run(){
				basicStatus.setBasicStatus(h);
			}
		});
		bluetoothIndicator.post(new Runnable() {
			public void run() {
				if(bluetoothIndicator.getBluetoothStatus() != mContext.isConnectedToZephyr()){
					bluetoothIndicator.setBluetoothStatus(mContext.isConnectedToZephyr());
				}
			}
		});
	}
}
