package com.apple.down.service;

//import com.hupu.push.HupuPublishEntity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.apple.down.assit.DownLoadInfo;
import com.apple.down.listener.DownloadListener;
import com.apple.down.listener.Download_State;
import com.apple.down.utils.Md5GenUtils;
import com.apple.down.utils.ThreadPool;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;

public class DownloadService extends Service {

	public DownloadManager mInst;
	private DownloadBinder mBinder = new DownloadBinder();

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mInst = DownloadManager.getInstance(this.getApplicationContext());
		Log.i("ZYN", "--PushService->onCreate--->");
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		Log.i("ZYN", "--PushService->onStart--->");
	}

	public class DownloadBinder extends Binder {
		
		public void addDownloadListener(DownloadListener listener) {
			Log.i("ZYN", "--DownloadService->DownloadBinder--->");
			mInst.addDownloadListener(listener);
		}


		public void addDownloadList(final DownLoadInfo info) {
			Log.i("ZYN", "--addDownloadList->DownloadBinder--->");
			WebView web_view = new WebView(DownloadManager.mInst.mcontext);
			WebSettings webSettings = web_view.getSettings();
			webSettings.setJavaScriptEnabled(true);
			webSettings.setSupportZoom(true);
			webSettings.setPluginsEnabled(true);
			webSettings.setAllowFileAccess(true);
			webSettings.setBuiltInZoomControls(true);
			webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
			webSettings.setPluginState(PluginState.ON);
			String url_key = Md5GenUtils.generator(info.url);
			web_view.loadUrl(info.url);
			web_view.setWebViewClient(new WebViewClient() {

				@Override
				public void onPageStarted(WebView view, String url,
						Bitmap favicon) {
					// TODO Auto-generated method stub
					super.onPageStarted(view, url, favicon);
				}

				@Override
				public void onPageFinished(WebView view, String url) {
					// TODO Auto-generated method stub
					super.onPageFinished(view, url);
					DownLoadInfo downInfo=info;
					downInfo.downUrl=url;
					mInst.addDownloadList(downInfo);
				}

				@Override
				public void onReceivedError(WebView view, int errorCode,
						String description, String failingUrl) {
					// TODO Auto-generated method stub
					super.onReceivedError(view, errorCode, description, failingUrl);
					mInst.onReturnDownMsg(info, Download_State.DOWNLOAD_ERROR);
				}
				
			});
		}

		public void deleteDownloadList(DownLoadInfo info) {
			Log.i("ZYN", "--deleteDownloadList->DownloadBinder--->");
			mInst.deleteDownloadList(info);
		}
	}

}
