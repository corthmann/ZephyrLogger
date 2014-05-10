package dk.corthmann.zephyrlogger.view;

import dk.corthmann.zephyrlogger.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BluetoothIndicatorControl extends RelativeLayout {

	ImageView mIndicator;
	TextView mText;
	boolean bluetoothStatus;
	
	public BluetoothIndicatorControl(Context context) {
		super(context);
		init(context);
	}

	public BluetoothIndicatorControl(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
		
	private void init(Context context){
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.bluetooth_control, this, true);
		bluetoothStatus = false;
		loadViews();
	}

	private void loadViews() {
		mIndicator = (ImageView)findViewById(R.id.bluetooth_status);
		mText = (TextView)findViewById(R.id.bluetooth_text);		
	}
	
	public void setBluetoothStatus(boolean isConnected){
		bluetoothStatus = isConnected;
		mIndicator.setImageResource((isConnected ? R.drawable.bluetooth_on : R.drawable.bluetooth_off)); //setBackgroundResource
		mText.setText((isConnected ? R.string.zephyr_connected : R.string.zephyr_not_connected));
	}
	
	public boolean getBluetoothStatus(){
		return bluetoothStatus;
	}
}
