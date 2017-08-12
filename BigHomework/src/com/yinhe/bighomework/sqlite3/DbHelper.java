package com.yinhe.bighomework.sqlite3;

import com.yinhe.bighomework.sqlite3.DBContract.EITTable;
import com.yinhe.bighomework.sqlite3.DBContract.FrequecyCATable;
import com.yinhe.bighomework.sqlite3.DBContract.ServerTable;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper{
	
	private static final String TEXT=" TEXT ";
	private static final String INTEGER=" INTEGER ";
	private static String DBName="DvbDatabase";
	private static int dbVersion=1;
	private static DbHelper dbHelper;
	
	
	
	private static final String SQL_CREATE_SERVER_TABLE="CREATE TABLE "+ServerTable.TABLENAME+" ("
	        +ServerTable.ID+INTEGER+"PRIMARY KEY  AUTOINCREMENT "+" , "  
			+ServerTable.SERVER_ID+INTEGER+" , "
	           +ServerTable.SERVER_NAME+TEXT+","
	           +ServerTable.SERVER_CATEGORY+TEXT+","
	           +ServerTable.FOLLOW_FLAG+INTEGER+","
	           +ServerTable.SCHEDULE_FLAG+INTEGER+","
	           +ServerTable.CA_MODE+INTEGER+","
	           
	           +ServerTable.PCR_PID+INTEGER+","
	           +ServerTable.ECM_PID+INTEGER+","
	           +ServerTable.EMM_PID+INTEGER+","
	           
			   +ServerTable.VIDEO_PID+INTEGER+","
			   +ServerTable.VIDEO_TYPE+INTEGER+","
			   +ServerTable.AUDIO_PID+INTEGER+","
			   +ServerTable.AUDIO_TYPE+INTEGER+","
			   +ServerTable.FREQUENCY+INTEGER+","
			   +ServerTable.SYMBOLRATE+INTEGER+","
			   +ServerTable.AUDIO_TYPE_STR+TEXT+","
			   +ServerTable.VIDEO_TYPE_STR+TEXT+")";
	
	
	private static final String SQL_CREATE_EIT_TABLE="CREATE TABLE "+EITTable.TABLENAME+" ("
			+EITTable.ID+INTEGER+" PRIMARY KEY AUTOINCREMENT "+" , "
			+EITTable.DATE+TEXT+","
			+EITTable.DURATION+TEXT+","
			+EITTable.NAME+TEXT+","
			+EITTable.EIT_CATEGORY+INTEGER+","
			+EITTable.SERVER_ID+INTEGER+","
			+EITTable.START_TIME+TEXT+")";
	
	
	private static final String SQL_CREATE_FREQUENCYCA_TABLE="CREATE TABLE "+FrequecyCATable.TABLENAME+" ("
			+FrequecyCATable.ID+INTEGER+" PRIMARY KEY AUTOINCREMENT "+" , "
			+FrequecyCATable.FREQUENCY+INTEGER+","
			+FrequecyCATable.SIMBOLRATE+INTEGER+","
			+FrequecyCATable.CAEMMPID+INTEGER+" DEFAULT -1 )";
	
	public static DbHelper getInstance(Context context){
		if(dbHelper==null){
			dbHelper=new DbHelper(context);
		}
		return dbHelper;
	}
	
	private DbHelper(Context context){
		super(context, DBName, null, dbVersion);
	}

	public DbHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(SQL_CREATE_SERVER_TABLE);
		db.execSQL(SQL_CREATE_FREQUENCYCA_TABLE);
		db.execSQL(SQL_CREATE_EIT_TABLE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
