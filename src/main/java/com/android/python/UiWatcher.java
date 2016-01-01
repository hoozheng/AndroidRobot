package com.android.python;

public class UiWatcher {
	private String watcherName = "";
	private String watcherPath = "";
	private String watcherFunc = "";
	
	public UiWatcher(String watcherName, String watcherPath, String watcherFunc){
		this.watcherName = watcherName;
		this.watcherPath = watcherPath;
		this.watcherFunc = watcherFunc;
	}
	public String getWatcherName() {
		return watcherName;
	}

	public String getWatcherPath() {
		return watcherPath;
	}

	public String getWatcherFunc() {
		return watcherFunc;
	}
}
