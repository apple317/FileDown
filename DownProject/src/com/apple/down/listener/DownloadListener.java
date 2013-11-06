package com.apple.down.listener;

import com.apple.down.assit.DownLoadInfo;

public interface DownloadListener {
	public void onAddDownload(DownLoadInfo info);
	public void onUpdateDownload(DownLoadInfo info);
	public void onDeleteDownload(DownLoadInfo info, int errorOccured);
	public void onDownloadMessage(DownLoadInfo info, int downStatue);
}
