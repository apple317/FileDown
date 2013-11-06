package com.apple.down.listener;

public interface Download_State {
	public static final int DOWNLOAD_GOING = 1;
	public static final int DOWNLOAD_WATING = 2;
	public static final int DOWNLOAD_PAUSED = 3;
	public static final int DOWNLOAD_DONE = 4;
	public static final int DOWNLOAD_NONE = 5;
	public static final int DOWNLOAD_DELE = 6;
	public static final int DOWNLOAD_LIMIT = 7;
	public static final int DOWNLOAD_ERROR = 8;
}
