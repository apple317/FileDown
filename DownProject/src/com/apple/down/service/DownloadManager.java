package com.apple.down.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import com.apple.down.assit.DownLoadInfo;
import com.apple.down.listener.DownloadListener;
import com.apple.down.listener.Download_State;
import com.apple.down.utils.Contants;
import com.apple.down.utils.Md5GenUtils;
import com.apple.down.utils.NetworkUtils;
import com.apple.down.utils.SharedPreferencesMgr;
import com.apple.down.utils.Task;
import com.apple.down.utils.ThreadPool;

public class DownloadManager {

	private LinkedList<DownLoadInfo> mDownloadList = new LinkedList<DownLoadInfo>();
	private static Object mDownloadLock = new Object();
	private Vector<String> mInteruptVector = new Vector<String>();
	private ThreadPool pool;
	public static Context mcontext;
	public static DownloadManager mInst;

	public List<DownloadListener> mListeners = new ArrayList<DownloadListener>();

	private final String downPreName = "down_pre";

	private DownloadManager(Context context) {
		pool = ThreadPool.getInstance();
		mcontext = context;
		SharedPreferencesMgr.init(context, downPreName);
	}

	public static DownloadManager getInstance(Context context) {
		if (mInst == null) {
			synchronized (mDownloadLock) {
				if (mInst == null) {
					mInst = new DownloadManager(context);
				}
			}
		}
		return mInst;
	}

	public static boolean onCheckDown() {
		if (SharedPreferencesMgr.getBoolean(Contants.is_wifi_down, false)) {
			if (NetworkUtils.checkNetworkStatue(mcontext) == ConnectivityManager.TYPE_MOBILE) {
				return true;
			}
		}
		return false;
	}


	public void interuptApkDownload(String url) {
		if (!mInteruptVector.contains(url)) {
			mInteruptVector.add(url);
		}
	}

	public void stopFileDowload(String url) {
		synchronized (mDownloadLock) {
		}
	}

	public boolean willInterupt(String url) {
		boolean exist = mInteruptVector.contains(url);
		if (exist) {
			mInteruptVector.remove(url);
		}
		return exist;
	}

	

	public void updateDownloadList(DownLoadInfo info) {
		synchronized (mDownloadLock) {
			for (int j = 0; j < mListeners.size(); ++j) {
				mListeners.get(j).onUpdateDownload(info);
			}
		}
	}

	

	

	public void addDownloadList(DownLoadInfo info) {
		synchronized (mDownloadLock) {
			if (onCheckDown()) {
				Log.i("HU", "addDownloadList true");
				return;
			}
			String url_key = Md5GenUtils.generator(info.url);
			for (int j = 0; j < mListeners.size(); ++j) {
				mListeners.get(j).onAddDownload(info);
			}
			Task task1 = new Task(info);
			boolean isHas = false;
			for (int j = 0; j < pool.taskQueue.size(); j++) {
				if (info.url.equals(pool.taskQueue.get(j).info.url)) {
					isHas = true;
					break;
				}
			}
			if (pool.curTask != null && pool.curTask.info.equals(info.url))
				isHas = true;
			if (!isHas)
				pool.addTask(task1);
		}
	}

	public void deleteDownloadList(DownLoadInfo info) {
		synchronized (mDownloadLock) {
			int delPos = -1;
			for (int idx = 0; idx < pool.taskQueue.size(); ++idx) {
				Task task = pool.taskQueue.get(idx);
				if (info.url == task.info.url) {
					delPos = idx;
				}
			}
			if (delPos != -1)
				pool.taskQueue.remove(delPos);
			if (pool.curTask != null && pool.curTask.info != null) {
				if (pool.curTask.info.url.equals(info.url)) {
					pool.stopTask();
				}
			}
			String url_key = Md5GenUtils.generator(info.url);
			info.state = Download_State.DOWNLOAD_DELE;
			for (int j = 0; j < mListeners.size(); ++j) {
				mListeners.get(j).onDeleteDownload(info, 0);
			}
		}
	}

	public void deleteTaskQueue(DownLoadInfo info) {
		synchronized (mDownloadLock) {
			int delPos = -1;
			for (int idx = 0; idx < pool.taskQueue.size(); ++idx) {
				Task task = pool.taskQueue.get(idx);
				if (info.url == task.info.url) {
					delPos = idx;
				}
			}
			if (delPos != -1)
				pool.taskQueue.remove(delPos);
			if (pool.curTask != null && pool.curTask.info != null) {
				if (pool.curTask.info.url.equals(info.url)) {
					pool.stopTask();
				}
			}
		}
	}

	public void onReturnDownMsg(DownLoadInfo info, int downStatue) {
		synchronized (mDownloadLock) {
			for (int j = 0; j < mListeners.size(); ++j) {
				mListeners.get(j).onDownloadMessage(info, downStatue);
			}
		}
	}

	public void addDownloadListener(DownloadListener listener) {
		synchronized (mDownloadLock) {
			if (listener != null)
				mListeners.add(listener);
		}
	}

}
