package com.yinhe.bighomework.utils;

public class Constant {
	
	public static final int CA_FREE=0;
	public static final int CA_CHARGE=1;
	
	public static final int MSG_NET_NAME = 1;
	public static final int MSG_PAT = 2;
	public static final int MSG_PMT = 3;
	public static final int MSG_SDT = 7;
	public static final int MSG_NIT = 8;
	public static final int MSG_PROGRESS = 9;
	public static final int MSG_EIT = 10;

	public static final int MSG_EIT_FOLLOW = 11;
	public static final int MSG_EIT_SCHEDULE = 12;
	public static final int MSG_EVENT_GONE = 13;

	public static final int MSG_CA = 14;
	public static final int MSG_NO_CA = 15;
	public static final int MSG_START_CA_THREAD=16;
	
	public static final int MSG_MANUEL_SCAN=17;
	public static final int MSG_AUTO_SCAN=18;
	public static final int MSG_TIMER=19;
	public static final int MSG_NO_EIT_SCHEDULE=20;
	
	
	public static final int SDT_PID = 0x0011;
	public static final byte[] SDT_MATCH = { 0x42 };

	public static final int PAT_PID = 0x0000;
	public static final byte[] PAT_MATCH = { 0x00 };
	public static final byte[] PMT_MATCH = { 0x02 };
	public static final int NIT_PID = 0x0010;
	public static final byte[] NIT_MATCH = { 0x40 };

	public static final int CA_PID = 0x0001;
	public static final byte[] CA_MATCH = { 0x01 };
	
	
	public static int PROGRESS_MAX_MANUEL = 10000;
	public static int AUTO_ADD = 2000;
	public static int PROGRESS_MAX_AUTO=PROGRESS_MAX_MANUEL+AUTO_ADD;
	public static int SINGLE_PROGRESS = PROGRESS_MAX_MANUEL / 3;


	
	public static final String ACTION_AUTO_SCAN="com.zlx.bighomewor.action.autoScan";
	public static final String ACTION_MANUEL_SCAN="com.zlx.bighomewor.action.manuelScan";
	public static final String ACTION_STOP_SCAN="com.zlx.bighomewor.action.stopScan";
	public static final String ACTION_REFRESH_LIST="com.zlx.bighomewor.action.refreshList";
	public static final String ACTION_PROGRESS="com.zlx.bighomewor.action.updateProgress";
	public static final String ACTION_FREQUENCY_CHANGE="com.zlx.bighomewor.action.frequencyChange";
	public static final String ACTION_SCAN_FREQUENCY_FAILED="com.zlx.bighomewor.action.scanFrequencyFailed";


	public static final String ACTION_SCAN_FINISH="com.zlx.bighomewor.action.scanFinish";
	public static final String ACTION_START_CA="com.zlx.bighomework.action.startCAThread";
	public static final int EMMPID_DEFAULT=-1;
	
	
	public static final String EXTRA_FREQUENCY="frequency";
	public static final String EXTRA_SYMBOLRATE="symbolRate";
	public static final String EXTRA_PROGRESS="progress";

	
	public static final String THREAD_START_TAG="-------------";
	public static final int NULL_COUNT_10=10;
	
	
	public static final int NULL_COUNT_2=2;

	
	public static final int WAIT_TIME_3000=3000;
	public static final int WAIT_TIME_1500=1500;
	public static final int WAIT_TIME_1000=1000;
	public static final int WAIT_TIME_500=500;
	
	public static final int WAIT_TIME_100=100;
	public static final int NULL_COUNT_120=120;

	
	public static final String APP_SHARE="bighomeworkShare";
	public static final String SHARE_EMMPID="shareEmmpid";
	public static final String NULL=" ";
	
	
	
}
