package dk.corthmann.zephyrlogger;

import java.util.Observable;

import dk.corthmann.zephyrlogger.hxm.HrmReading;

import android.content.Context;
import android.util.Log;

public class EventSource extends Observable  {
	private static final int UPDATE_INTERVAL = 10000; // every 20 seconds
	private static final String TAG = "EventSource";
	private static HrmReading current;
 
    public EventSource() {
 
    }
 
    // Create a method to update the Observerable's flag to true for changes and
    // notify the observers to check for a change. These are also a part of the
    // secret sauce that makes Observers and Observables communicate
    // predictably.
    public void triggerObservers(HrmReading data) {
    	setChanged();
    	notifyObservers(data);
    }
}
