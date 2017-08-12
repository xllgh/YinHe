package com.yinhe.bighomework.utils;

import java.util.Calendar;

import android.R.integer;
import android.util.Log;

public class TDTTime {

	private static final String TAG = "MyLogCat";

	// 得到具体的年月日和时间
	static public long getTDTCalendar(byte[] data) {

		int mMJDInt = ((int) ((data[0] & 0xff) << 8)) | (data[1] & 0xff);

		int hour = data[2]; // 时
		int minute = data[3]; // 分
		int second = data[4]; // 秒

		Log.i(TAG, "MJD " + String.format("%4x", mMJDInt));

		int year = (int) ((mMJDInt - 15078.2) / 365.25);
		int month = (int) ((mMJDInt - 14956.1 - (int) (year * 365.25)) / 30.6001);
		int day = (int) (mMJDInt - 14956 - (int) (year * 365.25) - (int) (month * 30.6001));

		int k = 0;
		if (14 == month || 15 == month) {
			k = 1;
		}

		year = year + k + 1900; // 年
		month = month - 1 - k * 12; // 月

		Log.i(TAG, "year = " + year + " month = " + month + " day = " + day);

//		if (hour >= 24) {
//			hour = hour - 24;
//			day = day + 1;
//			if (day == (getMonthDays(month, year) + 1)) {
//				day = 1;
//				month = month + 1;
//				if (month == 13) {
//					year = year + 1;
//				}
//			}
//		}
		
		int hour_tmp = Integer.parseInt(String.format("%02x", hour)) + 8;
        if(hour_tmp >= 24)
        {
        	hour_tmp -= 24;
        	day++;
        }
        if(day > 31)
        {
        	day = day - 31;
        	month++;
        }

		String date = new String(year + "-" + month + "-" + day);
		String time = new String(hour_tmp + ":" + String.format("%02x", minute)
				+ ":" + String.format("%02x", second));
		String calendar = new String(date + " " + time);

		int iHour = hour_tmp;
		int iMinute = Integer.valueOf(String.format("%02x", minute));
		int iSecond = Integer.valueOf(String.format("%02x", second));

		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month - 1);
		c.set(Calendar.DAY_OF_MONTH, day);
		Log.e("(((((((((((((((((", iHour + "");
		c.set(Calendar.HOUR_OF_DAY, iHour);
		c.set(Calendar.MINUTE, iMinute);
		c.set(Calendar.SECOND, iSecond);
		long milis = c.getTimeInMillis();

		Log.e(TAG, "time : " + calendar);
		return milis;

		// return calendar;
	}

	private static int getMonthDays(int month, int year) {

		int days = 0;

		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			days = 31;
			break;
		case 2:
			if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
				days = 28;
			} else {
				days = 29;
			}
			break;
		case 4:
		case 6:
		case 9:
		case 11:
			days = 30;
			break;
		default:
			return 0;
		}

		return days;

	}

	// 得到时间段信息
	public static long getTDTDuration(byte[] data) {
		int hour = data[0]; // 时
		int minute = data[1]; // 分
		int second = data[2]; // 秒

		String strDuration = new String(String.format("%02x", hour) + ":"
				+ String.format("%02x", minute) + ":"
				+ String.format("%02x", second));

		int iHour = Integer.valueOf(String.format(String.format("%02x", hour)));
		int iMinute = Integer.valueOf(String.format("%02x", minute));
		int iSecond = Integer.valueOf(String.format("%02x", second));
		long duration = (iHour * 60 * 60 + iMinute * 60 + iSecond) * 1000;

		Log.e("&&&&&&&&&&&&&", strDuration);

		return duration;
	}

}
