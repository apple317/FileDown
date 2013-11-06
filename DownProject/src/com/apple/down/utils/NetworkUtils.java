package com.apple.down.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils{
	
	/**
	 * check network statue
	 * @param context
	 */
	public static int checkNetworkStatue(Context context){
		ConnectivityManager connectivityManager = (ConnectivityManager) context
		.getSystemService(Context.CONNECTIVITY_SERVICE);//获取系统的连接服务
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();//获取网络的连接情况
		return (activeNetInfo==null)?ConnectivityManager.TYPE_MOBILE:activeNetInfo.getType();
	}
}
