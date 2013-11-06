package com.apple.down.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Environment;
import android.util.Log;

import com.apple.down.assit.DownLoadInfo;
import com.apple.down.listener.Download_State;
import com.apple.down.service.DownloadManager;

/**
 * 文件下载类
 * 
 * @author Administrator
 * 
 */

public class Task implements Runnable {

	public DownLoadInfo info;

	public Task(DownLoadInfo info) {
		this.info = info;
	}

	private volatile boolean isRunning = true;

	public void stop() {
		isRunning = false;
	}

	@Override
	public void run() {
		if (DownloadManager.onCheckDown())
			return;
		try {
			URL httpUrl;
			boolean sdcardError = false;
			int totalBytes = 0;
			int currentBytes = 0;
			int curdown = 0;
			httpUrl = new URL(info.downUrl);
			HttpURLConnection httpConn = (HttpURLConnection) httpUrl
					.openConnection();
			httpConn.setConnectTimeout(60 * 1000);
			httpConn.setReadTimeout(60 * 1000);
			httpConn.setDoInput(true);
			httpConn.setDoOutput(true);
			httpConn.setRequestProperty(
					"User-Agent",
					"Mozilla/5.0 (Windows; U; Windows NT 6.0; ru; rv:1.9.0.11) Gecko/2009060215 Firefox/3.0.11 (.NET CLR 3.5.30729)");
			httpConn.getHeaderField(2);
			httpConn.connect();
			if (httpConn.getResponseCode() == 200) {
				boolean willInterupted = false;
				totalBytes = httpConn.getContentLength();
				info.downMsg="系统空间不足";
				long sdcardAvailable = MachineInfo
						.getAvailableExternalMemorySize();
				if (sdcardAvailable < totalBytes) {
					sdcardError = true;
					DownloadManager.mInst.onReturnDownMsg(info, Download_State.DOWNLOAD_NOSPACE);
					return;
				}

				long pre = System.currentTimeMillis();
				long now;
				if (info.state == Download_State.DOWNLOAD_DONE)
					return;
				curdown = info.currentBytes;
				currentBytes += curdown;
				InputStream is = httpConn.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				String fileDir = Environment.getExternalStorageDirectory()
						+ "/" + DownloadManager.mcontext.getPackageName();
				String filePath = fileDir + "/"
						+ Md5GenUtils.generator(info.url) + "."
						+ Md5GenUtils.resolve(info.url);
				new File(fileDir).mkdirs();
				File fpath = new File(filePath);
				if (!fpath.exists()) {
					fpath.createNewFile();
				}
				File file = new File(filePath);
				sdcardError = true;
				RandomAccessFile out = new RandomAccessFile(file, "rw");
				out.skipBytes(curdown);
				info.currentBytes = currentBytes;
				info.totalBytes = totalBytes;
				info.savePath = filePath;
				info.state = Download_State.DOWNLOAD_GOING;
				DownloadManager.mInst.updateDownloadList(info);
				byte[] b = new byte[4096];
				while (true) {
					if (isRunning) {
						int len = bis.read(b);
						if (len == -1)
							break;
						out.write(b, 0, len);
						currentBytes += len;
						now = System.currentTimeMillis();
						info.currentBytes = currentBytes;
						if (now - pre > 800) {
							info.downSpeed = ((currentBytes - info.currentBytes) / 800.0);
							Log.i("ZYN", "code--info.currentBytes"
									+ info.currentBytes);
							DownloadManager.mInst.updateDownloadList(info);
							pre = now;
						}
					} else {
						break;
					}
				}
				if (currentBytes > info.totalBytes
						|| currentBytes == info.totalBytes) {
					info.currentBytes = currentBytes;
					info.state = Download_State.DOWNLOAD_DONE;
					DownloadManager.mInst.updateDownloadList(info);
				}
				bis.close();
				is.close();
				out.close();
				// if (!isRunning) {
				// File outFile = new File(filePath);
				// if (outFile.exists())
				// outFile.delete();
				// }
			} else {
				info.downMsg="当前网络部稳定,请重试！";
				info.state=Download_State.DOWNLOAD_ERROR;
				DownloadManager.mInst.deleteTaskQueue(info);
				return;
			}
		} catch (IOException e) {
			info.downMsg="当前网络部稳定,请重试！";
			info.state=Download_State.DOWNLOAD_ERROR;
			DownloadManager.mInst.deleteTaskQueue(info);
			return;
		}
	}
}
