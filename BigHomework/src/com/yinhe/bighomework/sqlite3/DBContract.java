package com.yinhe.bighomework.sqlite3;

public class DBContract {
	public static class ServerTable {
		public static String TABLENAME = "ServerTable";
		public static String ID = "_id";
		public static String SERVER_NAME = "ServerName";
		public static String SERVER_CATEGORY = "ServerCategory";
		public static String SERVER_ID = "ServerId";
		public static String VIDEO_PID = "VideoPid";
		public static String VIDEO_TYPE = "VideoType";
		public static String AUDIO_PID = "AudioPid";
		public static String AUDIO_TYPE = "AudioType";
		public static String FREQUENCY = "Frequency";
		public static String SYMBOLRATE = "SymbolRate";
		public static String VIDEO_TYPE_STR = "VideoTypeStr";
		public static String AUDIO_TYPE_STR = "AudioTypeStr";

		public static String SCHEDULE_FLAG = "scheduleFlag";
		public static String FOLLOW_FLAG = "FollowFlag";
		public static String CA_MODE = "CAMode";
		public static String PCR_PID = "PcrPid";
		public static String ECM_PID = "EcmPid";
		public static String EMM_PID="EmmPid";
	}

	public static class FrequecyCATable {
		public static String TABLENAME = "FrequencyTable";
		public static String ID = "_id";
		public static String FREQUENCY = "Frequency";
		public static String SIMBOLRATE = "SymbolRate";
		public static String CAEMMPID="CAEmmpid";
	}
	
	
	public static class EITTable{
		public static String TABLENAME="EitTable";
		public static String ID="_id";
		public static String START_TIME="StartTime";
		public static String DURATION="Duration";
		public static String NAME="Name";
		public static String DATE="Date";
		public static String EIT_CATEGORY="EitCategory";
		public static String SERVER_ID="ServerId";
		
	}

}
