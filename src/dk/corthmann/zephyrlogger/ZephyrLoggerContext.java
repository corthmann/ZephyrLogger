package dk.corthmann.zephyrlogger;

import java.util.Observable;

import android.app.Application;

public class ZephyrLoggerContext extends Application {
	
	private EventSource dataEvent;
	private boolean isRecording;
	private boolean _connectedToZephyr;
	private String recordingTag;
	
	@Override
	public void onCreate(){
		super.onCreate();
		dataEvent = new EventSource();
		setRecording(false);
		setConnectedToZephyr(false);
		setRecordingTag(null);
	}
	
	public EventSource getEventSource(){
		return dataEvent;
	}

	public boolean isRecording() {
		return isRecording;
	}

	public void setRecording(boolean isRecording) {
		this.isRecording = isRecording;
	}
	
	public boolean isConnectedToZephyr() {
		return _connectedToZephyr;
	}

	public void setConnectedToZephyr(boolean _connectedToZephyr) {
		this._connectedToZephyr = _connectedToZephyr;
	}

	public String getRecordingTag() {
		return recordingTag;
	}

	public void setRecordingTag(String recordingTag) {
		this.recordingTag = recordingTag;
	}
}
