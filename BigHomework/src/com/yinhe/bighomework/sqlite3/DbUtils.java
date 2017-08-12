package com.yinhe.bighomework.sqlite3;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.yinhe.bighomework.obj.EITInfo;
import com.yinhe.bighomework.obj.NITInfo;
import com.yinhe.bighomework.obj.PMTInfo;
import com.yinhe.bighomework.obj.SDTInfo;
import com.yinhe.bighomework.sqlite3.DBContract.EITTable;
import com.yinhe.bighomework.sqlite3.DBContract.FrequecyCATable;
import com.yinhe.bighomework.sqlite3.DBContract.ServerTable;
import com.yinhe.bighomework.utils.Constant;

public class DbUtils {

	DbHelper helper;
	Context context;

	public DbUtils(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	// ////////////操作EITTable

	// /插入EIT信息
	public void addEITInfo(EITInfo eitInfo) {
		helper = DbHelper.getInstance(context);
		SQLiteDatabase database = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(EITTable.DATE, eitInfo.getDate());
		values.put(EITTable.DURATION, eitInfo.getEndTime());
		values.put(EITTable.EIT_CATEGORY, eitInfo.getEitCategory());
		values.put(EITTable.NAME, eitInfo.getName());
		values.put(EITTable.SERVER_ID, eitInfo.getServerId());
		values.put(EITTable.START_TIME, eitInfo.getStartTime());
		database.insert(EITTable.TABLENAME, null, values);
	}

	// /获取所有EIT日期
	public ArrayList<HashMap<String, String>> getEITDate(int ServerId) {
		ArrayList<HashMap<String, String>> dateList = new ArrayList<HashMap<String, String>>();
		helper = DbHelper.getInstance(context);
		SQLiteDatabase database = helper.getReadableDatabase();
		String selection = EITTable.SERVER_ID + "=?";
		String[] selectionArgs = { ServerId + "" };
		String[] columns = { EITTable.DATE };
		String groupBy = EITTable.DATE;
		String orderBy = EITTable.DATE;
		Cursor cursor = database.query(EITTable.TABLENAME, columns, selection,
				selectionArgs, groupBy, null, orderBy);

		while (cursor.moveToNext()) {
			String date = cursor
					.getString(cursor.getColumnIndex(EITTable.DATE));
			HashMap<String, String> map = new HashMap<>();
			map.put(EITTable.DATE, date);
			dateList.add(map);
		}
		cursor.close();
		return dateList;
	}

	// /删除所有的EIT表中的内容
	public void delAllEITInfo() {
		helper = DbHelper.getInstance(context);
		SQLiteDatabase database = helper.getWritableDatabase();
		database.delete(EITTable.TABLENAME, null, null);
	}

	// /获取某天所有的EIT Schedule表的信息
	public ArrayList<EITInfo> getEITScheduleInfo(String date, int serverId) {
		ArrayList<EITInfo> eitList = new ArrayList<>();
		helper = DbHelper.getInstance(context);
		SQLiteDatabase database = helper.getReadableDatabase();
		String selection = EITTable.DATE + "=? AND " + EITTable.EIT_CATEGORY
				+ "=? AND " + EITTable.SERVER_ID + " =?";
		String[] selectionArgs = { date, EITInfo.EIT_SCHEDULE + "",
				serverId + "" };
		String orderBy = EITTable.START_TIME;
		Cursor cursor = database.query(EITTable.TABLENAME, null, selection,
				selectionArgs, null, null, orderBy);
		while (cursor.moveToNext()) {
			EITInfo eitInfo = new EITInfo();
			eitInfo.setDate(cursor.getString(cursor
					.getColumnIndex(EITTable.DATE)));
			eitInfo.setEndTime(cursor.getString(cursor
					.getColumnIndex(EITTable.DURATION)));
			eitInfo.setName(cursor.getString(cursor
					.getColumnIndex(EITTable.NAME)));
			eitInfo.setStartTime(cursor.getString(cursor
					.getColumnIndex(EITTable.START_TIME)));
			eitList.add(eitInfo);
		}
		cursor.close();
		return eitList;
	}

	// /获取某天的EIT PF信息

	// ////////////操作FrequecyCATable
	public void addFrequencyInfo(NITInfo nitInfo) {
		helper = DbHelper.getInstance(context);
		SQLiteDatabase database = helper.getWritableDatabase();
		ContentValues value = new ContentValues();
		value.put(FrequecyCATable.FREQUENCY, nitInfo.getFrequency());
		value.put(FrequecyCATable.SIMBOLRATE, nitInfo.getSymbolRate());
		database.insert(FrequecyCATable.TABLENAME, null, value);
	}

	public ArrayList<NITInfo> getAllNITInfo() {
		ArrayList<NITInfo> nitList = new ArrayList<>();
		helper = DbHelper.getInstance(context);
		SQLiteDatabase database = helper.getReadableDatabase();
		Cursor cursor = database.query(FrequecyCATable.TABLENAME, null, null,
				null, null, null, ServerTable.ID);
		if (cursor == null) {
			return nitList;
		}

		while (cursor.moveToNext()) {
			NITInfo nitInfo = new NITInfo();
			int frequency = cursor.getInt(cursor
					.getColumnIndex(FrequecyCATable.FREQUENCY));
			int symbolRate = cursor.getInt(cursor
					.getColumnIndex(FrequecyCATable.SIMBOLRATE));
			nitInfo.setFrequency(frequency);
			nitInfo.setSymbolRate(symbolRate);
			nitList.add(nitInfo);
		}
		cursor.close();
		return nitList;

	}

	public void delAllFrequencyInfo() {
		helper = DbHelper.getInstance(context);
		SQLiteDatabase database = helper.getWritableDatabase();
		database.delete(FrequecyCATable.TABLENAME, null, null);
	}

	public void delAllCAInfo() {
		helper = DbHelper.getInstance(context);
		SQLiteDatabase database = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(FrequecyCATable.CAEMMPID, Constant.EMMPID_DEFAULT);
		int rows = database.update(FrequecyCATable.TABLENAME, values, null,
				null);
		Log.e("influence rows:", rows + "");
	}

	public void addCAInfo(int emmPid, int frequency) {
		helper = DbHelper.getInstance(context);
		SQLiteDatabase database = helper.getWritableDatabase();
		ContentValues value = new ContentValues();
		value.put(FrequecyCATable.CAEMMPID, emmPid);
		String whereClause = FrequecyCATable.FREQUENCY + "=?";
		String[] whereArgs = { frequency + "" };
		database.update(FrequecyCATable.TABLENAME, value, whereClause,
				whereArgs);
	}

	public int getEmmPid(int frequency) {
		helper = DbHelper.getInstance(context);
		int emmPid = -1;
		SQLiteDatabase database = helper.getReadableDatabase();
		String selection = FrequecyCATable.FREQUENCY + "=?";
		String[] selectionArgs = { frequency + "" };
		Cursor cursor = database.query(FrequecyCATable.TABLENAME,
				new String[] { FrequecyCATable.CAEMMPID }, selection,
				selectionArgs, null, null, null);
		if (cursor == null) {
			return Constant.EMMPID_DEFAULT;
		}
		while (cursor.moveToNext()) {
			emmPid = cursor.getInt(cursor
					.getColumnIndex(FrequecyCATable.CAEMMPID));
			return emmPid;
		}
		cursor.close();
		return Constant.EMMPID_DEFAULT;

	}

	public void insertEmmPid(int emmPid) {
		helper = DbHelper.getInstance(context);
		SQLiteDatabase database = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ServerTable.EMM_PID, emmPid);

	}

	// ////////////////////操作ServerTable
	public void addPMTInfo(PMTInfo pmtInfo) {
		helper = DbHelper.getInstance(context);
		SQLiteDatabase database = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ServerTable.SERVER_ID, pmtInfo.getServerId());
		values.put(ServerTable.VIDEO_PID, pmtInfo.getVideoPid());
		values.put(ServerTable.VIDEO_TYPE, pmtInfo.getVideoType());
		values.put(ServerTable.VIDEO_TYPE_STR, pmtInfo.getStrVideoType());
		values.put(ServerTable.AUDIO_PID, pmtInfo.getAudioPid());
		values.put(ServerTable.AUDIO_TYPE, pmtInfo.getAudioType());
		values.put(ServerTable.AUDIO_TYPE_STR, pmtInfo.getStrAudeoType());
		values.put(ServerTable.FREQUENCY, pmtInfo.getFrequency());
		values.put(ServerTable.SYMBOLRATE, pmtInfo.getSymbolRate());
		values.put(ServerTable.PCR_PID, pmtInfo.getPcrPid());
		values.put(ServerTable.ECM_PID, pmtInfo.getEcmPid());
		String whereClause = ServerTable.SERVER_ID + "=?";
		String[] whereArgs = { pmtInfo.getServerId() + "" };

		int rowCount = database.update(ServerTable.TABLENAME, values,
				whereClause, whereArgs);
		if (rowCount == 0) {
			long insertRow = database.insert(ServerTable.TABLENAME, null,
					values);
			Log.e("sqlite", "insertPMTInfo:" + insertRow);
		} else {
			Log.e("sqlite", "updatePMTInfo:");

		}
	}

	public void addSDTInfo(SDTInfo sdtInfo) {
		helper = DbHelper.getInstance(context);
		SQLiteDatabase database = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ServerTable.SERVER_NAME, sdtInfo.getServerName());
		values.put(ServerTable.SERVER_CATEGORY, sdtInfo.getCategory());
		values.put(ServerTable.SERVER_ID, sdtInfo.getServerId());
		values.put(ServerTable.SCHEDULE_FLAG, sdtInfo.getFollowingFlag());
		values.put(ServerTable.FOLLOW_FLAG, sdtInfo.getSchedulFlag());
		values.put(ServerTable.CA_MODE, sdtInfo.getCA_MODE());

		String whereClause = ServerTable.SERVER_ID + "=? ";
		int rowCount = database.update(ServerTable.TABLENAME, values,
				whereClause, new String[] { sdtInfo.getServerId() + "" });
		if (rowCount == 0) {
			Long insertRow = database.insert(ServerTable.TABLENAME, null,
					values);
			Log.e("sqlite", "insertSDTInfo:" + insertRow);
		} else {
			Log.e("sqlite", "updateSDTInfo" + rowCount + "");
		}

	}

	public ArrayList<SDTInfo> getAllSDTInfo() {
		helper = DbHelper.getInstance(context);
		SQLiteDatabase database = helper.getReadableDatabase();
		Cursor cursor = database.query(ServerTable.TABLENAME, null, null, null,
				null, null, ServerTable.ID);

		ArrayList<SDTInfo> sdtList = new ArrayList<SDTInfo>();
		if (cursor == null) {
			return sdtList;
		}

		while (cursor.moveToNext()) {
			SDTInfo sdtInfo = new SDTInfo();
			String serverName = cursor.getString(cursor
					.getColumnIndex(ServerTable.SERVER_NAME));
			String serverCategory = cursor.getString(cursor
					.getColumnIndex(ServerTable.SERVER_CATEGORY));
			int serverId = cursor.getInt(cursor
					.getColumnIndex(ServerTable.SERVER_ID));
			int scheduleFlag = cursor.getInt(cursor
					.getColumnIndex(ServerTable.SCHEDULE_FLAG));
			int followFlag = cursor.getInt(cursor
					.getColumnIndex(ServerTable.FOLLOW_FLAG));
			int caMode = cursor.getInt(cursor
					.getColumnIndex(ServerTable.CA_MODE));
			int freqency = cursor.getInt(cursor
					.getColumnIndex(ServerTable.FREQUENCY));
			int symbolRate = cursor.getInt(cursor
					.getColumnIndex(ServerTable.SYMBOLRATE));
			int id = cursor.getInt(cursor.getColumnIndex(ServerTable.ID));

			sdtInfo.setIndex(id + "");
			sdtInfo.setCA_MODE(caMode);
			sdtInfo.setSchedulFlag(scheduleFlag);
			sdtInfo.setFollowingFlag(followFlag);
			sdtInfo.setFrequency(freqency);
			sdtInfo.setSymbolRate(symbolRate);

			sdtInfo.setCategory(serverCategory);
			sdtInfo.setServerId(serverId);
			sdtInfo.setServerName(serverName);
			sdtList.add(sdtInfo);
		}
		cursor.close();

		return sdtList;

	}

	public PMTInfo getPMTInfo(int server_id) {

		helper = DbHelper.getInstance(context);
		SQLiteDatabase database = helper.getReadableDatabase();
		String selection = ServerTable.SERVER_ID + "=?";
		String[] selectionArgs = new String[] { server_id + "" };
		Cursor cursor = database.query(ServerTable.TABLENAME, null, selection,
				selectionArgs, null, null, null);
		PMTInfo pmtInfo = new PMTInfo();

		if (cursor == null) {
			return null;
		}
		while (cursor.moveToNext()) {
			int serverId = cursor.getInt(cursor
					.getColumnIndex(ServerTable.SERVER_ID));

			int videoPid = cursor.getInt(cursor
					.getColumnIndex(ServerTable.VIDEO_PID));
			int videoType = cursor.getInt(cursor
					.getColumnIndex(ServerTable.VIDEO_TYPE));
			String videoTypeStr = cursor.getString(cursor
					.getColumnIndex(ServerTable.VIDEO_TYPE_STR));

			int audioPid = cursor.getInt(cursor
					.getColumnIndex(ServerTable.AUDIO_PID));
			int audioType = cursor.getInt(cursor
					.getColumnIndex(ServerTable.AUDIO_TYPE));
			String audioTypeStr = cursor.getString(cursor
					.getColumnIndex(ServerTable.AUDIO_TYPE_STR));

			int frequency = cursor.getInt(cursor
					.getColumnIndex(ServerTable.FREQUENCY));
			int symbolRate = cursor.getInt(cursor
					.getColumnIndex(ServerTable.SYMBOLRATE));

			int ecmPid = cursor.getInt(cursor
					.getColumnIndex(ServerTable.ECM_PID));
			int pcrPid = cursor.getInt(cursor
					.getColumnIndex(ServerTable.PCR_PID));

			pmtInfo.setEcmPid(ecmPid);
			pmtInfo.setPcrPid(pcrPid);

			pmtInfo.setServerId(serverId);
			pmtInfo.setAudioPid(audioPid);
			pmtInfo.setAudioType(audioType);
			pmtInfo.setStrAudeoType(audioTypeStr);

			pmtInfo.setVideoPid(videoPid);
			pmtInfo.setVideoType(videoType);
			pmtInfo.setStrVideoType(videoTypeStr);

			pmtInfo.setFrequency(frequency);
			pmtInfo.setSymbolRate(symbolRate);

		}
		cursor.close();
		return pmtInfo;
	}

	public void delAllServerInfo() {
		helper = DbHelper.getInstance(context);
		SQLiteDatabase database = helper.getWritableDatabase();
		database.delete(ServerTable.TABLENAME, null, null);
		database.close();
	}
	
	public void closeDatabase(){
		helper.close();
	}
}
