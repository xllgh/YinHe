package com.yinhe.iptvsetting;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;

public class NTPservice extends Service {
	private static final String TAG = "NTPservice";
	private static final String NTP_SERVER = "ntp_server";
	private String ipAddress;

	public IBinder onBind(Intent intent) {
		return null;
	}

	public void onCreate() {
		super.onCreate();
	}

	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		ipAddress = intent.getStringExtra(NTP_SERVER);
		setNTPServer();
	}

	private void setNTPServer() {
		Log.d(TAG, "ntp server ipaddress" + ipAddress);
		Settings.Secure.putString(getContentResolver(), NTP_SERVER, ipAddress);

		SystemProperties.set("yinhe.ntp_server", ipAddress);
	}

}
