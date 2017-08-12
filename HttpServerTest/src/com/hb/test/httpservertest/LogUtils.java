package com.hb.test.httpservertest;

import java.text.SimpleDateFormat;

import android.util.Log;

public class LogUtils {

	public static boolean switcher = true;
	public static String tag = "tag";

	public static void d(String msg) {
		if (switcher) {
			Log.e(tag, msg);
		}
	}

	public static void time(String content) {
		if (switcher) {
			Log.e(String.format(new SimpleDateFormat("mm:ss.SSSZ")
					.format(System.currentTimeMillis())), content);
		}

	}

}
