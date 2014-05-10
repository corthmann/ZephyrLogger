package dk.corthmann.zephyrlogger.fragment;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import dk.corthmann.zephyrlogger.EventSource;
import dk.corthmann.zephyrlogger.R;
import dk.corthmann.zephyrlogger.ZephyrLoggerContext;
import dk.corthmann.zephyrlogger.hxm.HrmReading;

public class DetailedFragment extends Fragment implements Observer {
	private ZephyrLoggerContext mContext;
	private EventSource mData;
	
	private View layout;
	
    public static DetailedFragment newInstance(String content) {
    	DetailedFragment fragment = new DetailedFragment();

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
    	layout = inflater.inflate(R.layout.activity_main, container, false);
    	    	
        return layout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

	@Override
	public void update(Observable arg0, Object data) {
		final HrmReading h = (HrmReading) data;
		layout.post(new Runnable(){
			public void run(){
				displayHrmReading(h);
			}
		});
	}
	
	/****************************************************************************
	 * Some utility functions to control the formatting of HxM fields into the 
	 * activity's view
	 ****************************************************************************/	
	private void displayHrmReading(HrmReading h){
		display ( R.id.stx,  h.stx );
		display ( R.id.msgId,  h.msgId );
		display ( R.id.dlc,  h.dlc );
		display ( R.id.firmwareId,   h.firmwareId );
		display ( R.id.firmwareVersion,   h.firmwareVersion );
		display ( R.id.hardwareId,   h.hardWareId );
		display ( R.id.hardwareVersion,   h.hardwareVersion );
		display ( R.id.batteryChargeIndicator,  h.batteryIndicator );
		display ( R.id.heartRate, h.heartRate );
		display ( R.id.heartBeatNumber,  h.heartBeatNumber );
		display ( R.id.hbTimestamp1,   h.hbTime1 );
		display ( R.id.hbTimestamp2,   h.hbTime2 );
		display ( R.id.hbTimestamp3,   h.hbTime3 );
		display ( R.id.hbTimestamp4,   h.hbTime4 );
		display ( R.id.hbTimestamp5,   h.hbTime5 );
		display ( R.id.hbTimestamp6,   h.hbTime6 );
		display ( R.id.hbTimestamp7,   h.hbTime7 );
		display ( R.id.hbTimestamp8,   h.hbTime8 );
		display ( R.id.hbTimestamp9,   h.hbTime9 );
		display ( R.id.hbTimestamp10,   h.hbTime10 );
		display ( R.id.hbTimestamp11,   h.hbTime11 );
		display ( R.id.hbTimestamp12,   h.hbTime12 );
		display ( R.id.hbTimestamp13,   h.hbTime13 );
		display ( R.id.hbTimestamp14,   h.hbTime14 );
		display ( R.id.hbTimestamp15,   h.hbTime15 );
		display ( R.id.reserved1,   h.reserved1 );
		display ( R.id.reserved2,   h.reserved2 );
		display ( R.id.reserved3,   h.reserved3 );
		display ( R.id.distance,   h.distance );
		display ( R.id.speed,   h.speed );
		display ( R.id.strides,  (int)h.strides );
		display ( R.id.reserved4,  h.reserved4 );
		display ( R.id.reserved5,  h.reserved5 );
		display ( R.id.crc,  h.crc );
		display ( R.id.etx,  h.etx );   
	}
	
	/*
	 * display a byte value
	 */
	private void display  ( int nField, byte d ) {   
		String INT_FORMAT = "%x";
		String s = String.format(INT_FORMAT, d);
		display( nField, s  );
	}

	/*
	 * display an integer value
	 */
	private void display  ( int nField, int d ) {   
		String INT_FORMAT = "%d";
		String s = String.format(INT_FORMAT, d);
		display( nField, s  );
	}

	/*
	 * display a long integer value
	 */
	private void display  ( int nField, long d ) {   
		String INT_FORMAT = "%d";
		String s = String.format(INT_FORMAT, d);
		display( nField, s  );
	}

	/*
	 * display a character string
	 */
	private void display ( int nField, CharSequence  str  ) {
		TextView tvw = (TextView) layout.findViewById(nField);
		if ( tvw != null )
			tvw.setText(str);
	}
	
}
