package com.apple.down;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.apple.down.assit.DownLoadInfo;
import com.apple.down.listener.DownloadListener;
import com.apple.down.listener.Download_State;
import com.apple.down.service.DownloadService;
import com.apple.down.service.DownloadService.DownloadBinder;
import com.apple.down.utils.Contants;
import com.apple.down.utils.Md5GenUtils;
import com.apple.down.utils.SharedPreferencesMgr;

/**
 * version 1.1.1
 * 
 * @author Administrator
 * 
 */
public class DownloadInterface {

	private static ServiceConnection mConn;
	public static DownloadBinder mBinder = null;
	//public static List<CheckDownFileListener> cListeners = new ArrayList<CheckDownFileListener>();
	private static Context context;
	private static String file_name="apple_down";
	public static void initDownService(Context mContext) {
		startDownService(mContext);
		SharedPreferencesMgr.init(mContext, file_name);
		context=mContext;
	}

	
	
	/**
	 * start Service send Recevice
	 * 
	 * @param context
	 * @throws Exception
	 */
	public static void startDownService(Context context) {
		Intent intent = new Intent(context, DownloadService.class);
		context.startService(intent);
	}

	public static void setWifiDownloadFile(boolean isDown) {
		SharedPreferencesMgr.setBoolean(Contants.is_wifi_down, isDown);
	}

	public static boolean getWifiDownload() {
		return SharedPreferencesMgr.getBoolean(Contants.is_wifi_down, false);
	}

	
	public static void addDownloadListener(DownloadListener listener) {
		Log.i("ZYN", "bindService succeed!-->"+mBinder);
		if(mBinder==null){
			bindService(context);
		}
		if(mBinder!=null)
			mBinder.addDownloadListener(listener);
		
		
	}

	public static void addDownloadList(DownLoadInfo info) {
		Log.i("ZYN", "bindService succeed!-->"+mBinder);
		if(mBinder==null){
			bindService(context);
		}
		if(mBinder!=null)
			mBinder.addDownloadList(info);
	}

	public static void deleteDownloadList(DownLoadInfo info) {
		Log.i("ZYN", "bindService succeed!-->"+mBinder);
		if(mBinder==null){
			bindService(context);
		}
		if(mBinder!=null)
			mBinder.deleteDownloadList(info);
	}

	
	public static void stopDownService(Context context) {
		Intent intent = new Intent(context, DownloadService.class);
		context.stopService(intent);
	}



	public static DownloadBinder getDownloadBinder() {
		return mBinder;
	}

	private static void bindService(Context context) {
		mConn = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				mBinder = (DownloadService.DownloadBinder) service;
				if (mBinder == null) {
					Log.i("ZYN", "null is binder");
				} else
					Log.i("ZYN", "bind succeed!");
			}
			@Override
			public void onServiceDisconnected(ComponentName name) {
				Log.v("ZYN", "service disconnect...");
			}
		};
		context.bindService(new Intent(context, DownloadService.class), mConn,
				Context.BIND_AUTO_CREATE);
	}

}
