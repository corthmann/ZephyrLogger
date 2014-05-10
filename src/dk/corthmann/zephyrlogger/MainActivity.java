/*   
 * Copyright 2013 (C) Christian Orthmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Copyright (C) 2010 Pye Brook Company, Inc.
 *               http://www.pyebrook.com
 *               info@pyebrook.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * This software uses information from the document
 *
 *     'Bluetooth HXM API Guide 2010-07-22'
 *
 * which is Copyright (C) Zephyr Technology, and used with the permission
 * of the company. Information on Zephyr Technology products and how to 
 * obtain the Bluetooth HXM API Guide can be found on the Zephyr
 * Technology Corporation website at
 * 
 *      http://www.zephyr-technology.com
 * 
 *
 */

package dk.corthmann.zephyrlogger;

import android.os.Bundle;
import android.view.Menu;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.TabPageIndicator;

import dk.corthmann.zephyrlogger.R;
import dk.corthmann.zephyrlogger.fragment.DetailedFragment;
import dk.corthmann.zephyrlogger.fragment.RecordingFragment;
import dk.corthmann.zephyrlogger.fragment.StatusFragment;
import dk.corthmann.zephyrlogger.hxm.HrmReading;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuInflater;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {
	
	private static final String[] CONTENT = new String[] { "Basic", "Record", "Detailed" };
	private static final int[] ICONS = new int[] {
		R.drawable.perm_group_overview,
		R.drawable.perm_group_statistics,
		R.drawable.perm_group_history,
	};
	FragmentPagerAdapter fragmentAdapter;

	private ZephyrLoggerContext mContext;
	private EventSource mData;

	/*
	 *  TAG for Debugging Log
	 */
	private static final String TAG = "ZephyrLogger";

	/*
	 * Name of the connected device, and it's address
	 */
	private String mHxMName = null;
	private String mHxMAddress = null;

	/*
	 * Local Bluetooth adapter
	 */
	private BluetoothAdapter mBluetoothAdapter = null;

	/*
	 * Member object for the chat services
	 */
	private HxmService mHxmService = null;

	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	public File exportData(HrmReading m) {
		// Get the directory for the user's downloads directory.
		File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		File file = new File(path, "Zephyr_"+mContext.getRecordingTag()+"_data.txt");
		try {
			if(!file.exists()){
				file.createNewFile();
			}
			FileWriter writer = new FileWriter(file, true);
			writer.write(System.currentTimeMillis()+","+m.toString()+"\n");
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file;
	}
	
	/*
	 * Our onCreate() needs to setup the main activity that we will use to        
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e(TAG, "onCreate");		
		mContext = (ZephyrLoggerContext)getApplicationContext();
		mData = mContext.getEventSource();

		setContentView(R.layout.simple_tabs);
		
		fragmentAdapter = new ZephyrLoggerAdapter(getSupportFragmentManager());

		ViewPager pager = (ViewPager)findViewById(R.id.pager);
		pager.setAdapter(fragmentAdapter);

		TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
		indicator.setViewPager(pager);
		
		initZeohyr();
	}
	
	class ZephyrLoggerAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
		private final List<Fragment> fragments;
		
		public ZephyrLoggerAdapter(FragmentManager fm) {
			super(fm);
			this.fragments = createFragments();
		}
		
		private List<Fragment> createFragments(){
			List<Fragment> f = new ArrayList<Fragment>();
			for(int position=0; position < CONTENT.length; position++){
				Fragment fragment = null;
				switch(position) {
				case 0:
					fragment = StatusFragment.newInstance(CONTENT[position % CONTENT.length]);
					break;
				case 1:
					fragment = RecordingFragment.newInstance(CONTENT[position % CONTENT.length]);
					break;
				case 2:
					fragment = DetailedFragment.newInstance(CONTENT[position % CONTENT.length]);
					break;
				default:
					fragment = StatusFragment.newInstance(CONTENT[position % CONTENT.length]);
				}
				f.add(fragment);
			}
			return f;
		}
		
		@Override
		public Fragment getItem(int position) {
			return this.fragments.get(position);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return CONTENT[position % CONTENT.length].toUpperCase();
		}

		@Override public int getIconResId(int index) {
			return ICONS[index];
		}

		@Override
		public int getCount() {
			return CONTENT.length;
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "onStart");

		/*
		 * Check if there is a bluetooth adapter and if it's enabled,
		 * error messages and status updates as appropriate
		 */
		if (mBluetoothAdapter != null ) {
			// If BT is not on, request that it be enabled.
			// setupChat() will then be called during onActivityResult     
			if (!mBluetoothAdapter.isEnabled()) {
//				mStatus.setText(R.string.btNotEnabled);
				Log.d(TAG, "onStart: Blueooth adapter detected, but it's not enabled");
			}
		} else {
//			mStatus.setText(R.string.noBluetooth);
			Log.d(TAG, "onStart: No blueooth adapter detected, it needs to be present and enabled");
		}

	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");

		// Performing this check in onResume() covers the case in which BT was
		// not enabled during onStart(), so we were paused to enable it...
		// onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
		if (mHxmService != null) {
			// Only if the state is STATE_NONE, do we know that we haven't started already
			if (mHxmService.getState() == R.string.HXM_SERVICE_RESTING) {
				// Start the Bluetooth scale services
				mHxmService.start();
			}
		}
	}

	@Override
	public synchronized void onPause() {
		super.onPause();
		Log.e(TAG, "- ON PAUSE -");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.e(TAG, "-- ON STOP --");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Stop the Bluetooth chat services
		if (mHxmService != null) mHxmService.stop();
		Log.e(TAG, "--- ON DESTROY ---");
	}
	
	/* ZEPHYR STUFF */
	private void initZeohyr(){
		/*
         *  Get the default bluetooth adapter, if it fails there is not much we can do
         *  so show the user a message and then close the application
         */
	    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	    
	    /*
	     *  If the adapter is null, then Bluetooth is not supported
	     */
	    if (mBluetoothAdapter == null) {
	            /*
	             * Blutoooth needs to be available on this device, and also enabled.  
	             */
	        Toast.makeText(this, "Bluetooth is not available or not enabled", Toast.LENGTH_LONG).show();
	            //mStatus.setText(R.string.noBluetooth);
	        
	     } else {
	            /*
	             * Everything should be good to go so let's try to connect to the HxM
	             */
	            if (!mBluetoothAdapter.isEnabled()) {
	    	        Toast.makeText(this, R.string.btNotEnabled, Toast.LENGTH_LONG).show();
	                    //mStatus.setText(R.string.btNotEnabled);
	                    Log.d(TAG, "onStart: Blueooth adapter detected, but it's not enabled");
	            } else {
	                    //mStatus.setText(R.string.connecting);
	                    connectToHxm(); //TODO: make sure to retry this call if the connection fails.
	            }
	     }
	}
	
	/*
	 * connectToHxm() sets up our service loops and starts the connection
	 * logic to manage the HxM device data stream 
	 */
	private void connectToHxm() {
		/*
		 * Update the status to connecting so the user can tell what's happening
		 */
//		mStatus.setText(R.string.connecting);

		/*
		 * Setup the service that will talk with the Hxm
		 */
		if (mHxmService == null) 
			setupHrm();

		/*
		 * Look for an Hxm to connect to, if none is found tell the user
		 * about it
		 */
		if ( getFirstConnectedHxm() ) {
			BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mHxMAddress);
			mHxmService.connect(device);    // Attempt to connect to the device
		} else {
//			mStatus.setText(R.string.nonePaired);           
		}

	}

	/*
	 * Loop through all the connected bluetooth devices, the first one that 
	 * starts with HXM will be assumed to be our Zephyr HxM Heart Rate Monitor,
	 * and this is the device we will connect to
	 * 
	 * returns true if a HxM is found and the global device address has been set 
	 */
	private boolean getFirstConnectedHxm() {

		/*
		 * Initialize the global device address to null, that means we haven't 
		 * found a HxM to connect to yet        
		 */
		mHxMAddress = null;     
		mHxMName = null;


		/*
		 * Get the local Bluetooth adapter
		 */
		BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();

		/*
		 *  Get a set of currently paired devices to cycle through, the Zephyr HxM must
		 *  be paired to this Android device, and the bluetooth adapter must be enabled
		 */
		Set<BluetoothDevice> bondedDevices = mBtAdapter.getBondedDevices();

		/*
		 * For each device check to see if it starts with HXM, if it does assume it
		 * is the Zephyr HxM device we want to pair with      
		 */
		if (bondedDevices.size() > 0) {
			for (BluetoothDevice device : bondedDevices) {
				String deviceName = device.getName();
				if ( deviceName.startsWith("HXM") ) {
					/*
					 * we found an HxM to try to talk to!, let's remember its name and 
					 * stop looking for more
					 */
					mHxMAddress = device.getAddress();
					mHxMName = device.getName();
					Log.d(TAG,"getFirstConnectedHxm() found a device whose name starts with 'HXM', its name is "+mHxMName+" and its address is ++mHxMAddress");
					break;
				}
			}
		}

		/*
		 * return true if we found an HxM and set the global device address
		 */
		return (mHxMAddress != null);
	}
	
	private void setupHrm() {
		Log.d(TAG, "setupScale:");

		// Initialize the service to perform bluetooth connections
		mHxmService = new HxmService(this, mHandler);
	}
	
	/**
	 * Sends a message.
	 * @param message  A string of text to send.
	 */
	// The Handler that gets information back from the hrm service
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case R.string.HXM_SERVICE_MSG_STATE: 
				Log.d(TAG, "handleMessage():  MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case R.string.HXM_SERVICE_CONNECTED:
					if ((mHxMName != null)) { //(mStatus != null) && 
//						mStatus.setText(R.string.connectedTo);
//						mStatus.append(mHxMName);
						mContext.setConnectedToZephyr(true);
					}
					break;

				case R.string.HXM_SERVICE_CONNECTING:
//					mStatus.setText(R.string.connecting);
					break;

				case R.string.HXM_SERVICE_RESTING:
//					if (mStatus != null ) {
//						mStatus.setText(R.string.notConnected);
//					}
					mContext.setConnectedToZephyr(false);
					break;
				}
				break;

			case R.string.HXM_SERVICE_MSG_READ: {
				/*
				 * MESSAGE_READ will have the byte buffer in tow, we take it, build an instance
				 * of a HrmReading object from the bytes, and then display it into our view
				 */
				byte[] readBuf = (byte[]) msg.obj;
				HrmReading hrm = new HrmReading( readBuf );
				// Notify the fragments that new data should be presented on the screen.
				mData.triggerObservers(hrm);//notifyObservers(hrm);
//				displayHrmReading(hrm);

				if(isExternalStorageWritable() && mContext.isRecording()){
					exportData(hrm);
				}
				break;
			}

			case R.string.HXM_SERVICE_MSG_TOAST:
				Toast.makeText(getApplicationContext(), msg.getData().getString(null),Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult " + requestCode);
		switch(requestCode)
		{  

		case 1:
			break;

		}             
	}
	
}
