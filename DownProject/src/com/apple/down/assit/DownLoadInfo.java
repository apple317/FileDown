package com.apple.down.assit;



public class DownLoadInfo {
	
	public String url;
	public int currentBytes=0;
	public int totalBytes;
	public int state;
	public String savePath;
	//文件下载最后地址
	public String downUrl;
	public double downSpeed = 0.0;
	public String downMsg;

	public DownLoadInfo(String url, int currentBytes, int totalBytes,
			int state, String savePath, String downUrl, double downSpeed) {
		this.url = url;
		this.currentBytes = currentBytes;
		this.totalBytes = totalBytes;
		this.state = state;
		this.savePath = savePath;
		this.downUrl = downUrl;
		this.downSpeed = downSpeed;
	}

	public DownLoadInfo() {
		
	}

}
