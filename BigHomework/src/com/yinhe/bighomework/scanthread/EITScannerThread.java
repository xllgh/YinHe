package com.yinhe.bighomework.scanthread;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.yinhe.bighomework.obj.EITInfo;
import com.yinhe.bighomework.obj.SDTInfo;
import com.yinhe.bighomework.sqlite3.DbUtils;
import com.yinhe.bighomework.utils.Constant;
import com.yinhe.bighomework.utils.ParseUtils;
import com.yinhe.bighomework.utils.TDTTime;
import com.yinhe.bighomwork.R;
import com.yinhe.dtv.DtvBuffer;
import com.yinhe.dtv.DtvChannel;
import com.yinhe.dtv.DtvDemux;
import com.yinhe.dtv.DtvFilter;
import com.yinhe.dtv.DtvManager;
import com.yinhe.dtv.DtvSignal;
import com.yinhe.dtv.DtvTuner;

public class EITScannerThread extends Thread {

	private int frequency;
	private int symbolRate;
	private Handler handler;
	private ArrayList<EITInfo> listFollow = new ArrayList<EITInfo>();
	private ArrayList<EITInfo> listSchedule = new ArrayList<EITInfo>();

	DtvManager dtv;
	DtvTuner tuner;
	int followFlag;
	int scheduleFlag;
	SDTInfo sdtInfo;
	int pid = 0x0012;
	byte[] matchScheduleCurrent = new byte[] { 0x50, 0x51, 0x52, 0x53, 0x53,
			0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5A, 0x5B, 0x5C, 0x5D, 0x5F };

	byte[] matchScheduleOther = new byte[] { 0x60, 0x61, 0x62, 0x63, 0x63,
			0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6A, 0x6B, 0x6C, 0x6D, 0x6F };

	byte[] matchFollowCurrent = { 0x4E };
	byte[] matchFollowOther = { 0x4F };

	boolean hasMore = true;
	boolean moreTable = true;
	byte[] mask = { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
	byte[] negate = { (byte) 0x00, (byte) 0x00, (byte) 0x00 };
	String TAG = this.getClass().getName();
	int repeatCount = 0;
	public static int MAX_REPEAT = 5;
	public int serverId;
	private Context context;
	private DbUtils dbUtils;

	boolean getSchedule = true;

	public EITScannerThread(Context context, SDTInfo sdtInfo, Handler handler,
			DtvTuner tuner, DtvManager dtv, boolean getSchedule) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.sdtInfo = sdtInfo;
		frequency = sdtInfo.getFrequency();
		symbolRate = sdtInfo.getSymbolRate();
		followFlag = sdtInfo.getFollowingFlag();
		scheduleFlag = sdtInfo.getSchedulFlag();
		serverId = sdtInfo.getServerId();
		dbUtils = new DbUtils(context);
		this.getSchedule = getSchedule;

		this.handler = handler;
		this.tuner = tuner;
		this.dtv = dtv;
	}

	int nullCount = 0;
	volatile int c = 0;

	OnEITThreadFinishedListener onEITThreadFinishedListener;

	public void setOnEITThreadFinishedListener(
			OnEITThreadFinishedListener onEITThreadFinishedListener) {
		this.onEITThreadFinishedListener = onEITThreadFinishedListener;
	}

	@Override
	public void run() {
		Log.e("EITScannerThread", sdtInfo.toString());
		tuner.setParameter(new DtvTuner.DvbcPamameter(frequency, symbolRate,
				DtvTuner.QAM64, DtvTuner.SPECTRUM_AUTO));

		getFollowEIT();
		if (getSchedule) {
			getScheduleEIT();
		}

	}

	ArrayList<Integer> sectionList = new ArrayList<Integer>();

	boolean add = true;

	private void getScheduleEIT() {
		Log.e("getScheduleEIT", "getScheduleEIT");
		sectionList.clear();
		DtvDemux demuxSchedule = dtv.getDemux(2);
		demuxSchedule.linkTuner(tuner);
		DtvChannel channelSchedule = demuxSchedule.createChannel(
				DtvChannel.SECTION,
				DtvDemux.DEMUX_CHANNEL_CRC_FORCE_AND_DISCARD, 8192);

		channelSchedule.setPid(pid);
		DtvBuffer buffer = demuxSchedule.createBuffer(1024 * 1024);
		channelSchedule.linkBuffer(buffer);

		DtvSignal signalSchedule = demuxSchedule.createSignal();
		buffer.associateSignal(signalSchedule);

		int count = 0;
		moreTable = true;
		while (moreTable && count < matchScheduleCurrent.length) {
			sectionList.clear();
			Log.e(">>>>>>>>>>>>>", "parse next table");
			Log.e(TAG, String.format("%02x", serverId));
			byte t1 = (byte) (serverId & 0xFF);
			byte t2 = (byte) ((serverId >> 8) & 0xFF);
			byte[] match = { matchScheduleCurrent[count++], t2, t1 };

			DtvFilter filterSchedule = demuxSchedule.createFilter(mask, match,
					negate);
			filterSchedule.associateChannel(channelSchedule);

			channelSchedule.start();
			signalSchedule.wait(3000);
			hasMore = true;
			nullCount = 0;
			Log.e("hasMore", hasMore + "");
			while (hasMore && nullCount < Constant.NULL_COUNT_2) {
				Log.e("hasMore", "parse next section");
				signalSchedule.wait(Constant.WAIT_TIME_1000);
				byte[][] dataS = buffer.readData(1);
				if (dataS != null) {
					listSchedule.addAll(parseEIT(dataS, true));
				} else {
					Log.e("data", "data is null");
					nullCount++;
					continue;
				}
				onEITThreadFinishedListener.OnEITGetSchedule(listSchedule,
						false);
				Log.e("tag getScheduleEIT", listSchedule.toString());
			}

			if (listSchedule.size() == 0) {
				moreTable = false;
			}

			channelSchedule.stop();
			filterSchedule.disassociateChannel(channelSchedule);
			filterSchedule.release();
		}
		buffer.disassociateSignal();
		channelSchedule.unlinkBuffer();
		channelSchedule.release();
		buffer.release();
		signalSchedule.release();
		demuxSchedule.release();

		onEITThreadFinishedListener.OnEITGetSchedule(listSchedule, true);
	}

	private void getFollowEIT() {
		sectionList.clear();
		int count = 0;
		Log.e(">>>>>>>>>>>>>>>", "getFollowEIT");
		DtvDemux demuxFollow = dtv.getDemux(2);
		demuxFollow.linkTuner(tuner);
		DtvChannel channelFollow = demuxFollow.createChannel(
				DtvChannel.SECTION,
				DtvDemux.DEMUX_CHANNEL_CRC_FORCE_AND_DISCARD, 8192);
		channelFollow.setPid(pid);
		DtvBuffer bufferFollow = demuxFollow.createBuffer(1024 * 1024);
		channelFollow.linkBuffer(bufferFollow);
		DtvSignal signalFollow = demuxFollow.createSignal();
		bufferFollow.associateSignal(signalFollow);

		byte t1 = (byte) (serverId & 0xFF);
		Log.e(TAG, String.format("%02x", t1));

		byte t2 = (byte) ((serverId) >> 8 & 0xFF);
		Log.e(TAG, String.format("%02x", t2));

		byte[] match = { matchFollowCurrent[0], t2, t1 };

		DtvFilter filterFollow = demuxFollow.createFilter(mask, match, negate);
		filterFollow.associateChannel(channelFollow);

		channelFollow.start();

		nullCount = 0;
		hasMore = true;
		signalFollow.wait(Constant.WAIT_TIME_1000);
		while (hasMore && nullCount < Constant.NULL_COUNT_2) {
			Log.e("hasMore", hasMore + "");
			signalFollow.wait(Constant.WAIT_TIME_1000);
			byte[][] data = bufferFollow.readData(1);
			if (data != null) {
				listFollow.addAll(parseEIT(data, false));
			} else {
				Log.e(TAG, "data is null");
				nullCount++;
			}
			Log.e("tag getFollowEIT", listFollow.toString());
		}

		channelFollow.stop();
		filterFollow.disassociateChannel(channelFollow);
		bufferFollow.disassociateSignal();
		channelFollow.unlinkBuffer();

		filterFollow.release();
		channelFollow.release();
		bufferFollow.release();
		signalFollow.release();
		demuxFollow.release();
		onEITThreadFinishedListener.OnEITGetFollow(listFollow);
	}

	private ArrayList<EITInfo> parseEIT(byte[][] data, boolean isEITSchedule) {
		ArrayList<EITInfo> eitList = new ArrayList<EITInfo>();

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			sb.append(ParseUtils.byteToBinarySequence(data[i]));
		}
		String strsb = sb.toString();
		int tableID = ParseUtils.binarySequenceToNumber(ParseUtils.getBits(
				strsb, 0, 8));
		int lastTableID = ParseUtils.binarySequenceToNumber(ParseUtils.getBits(
				strsb, 104, 8));

		Log.e("tableID", tableID + "");
		Log.e("lastTableID", lastTableID + "");

		if (tableID == lastTableID) {
			moreTable = false;
		}

		int sectionNum = 0;
		int lastSectionNum = 0;
		if (isEITSchedule) {
			sectionNum = ParseUtils.binarySequenceToNumber(ParseUtils.getBits(
					strsb, 48, 8)) / 8;
			lastSectionNum = ParseUtils.binarySequenceToNumber(ParseUtils
					.getBits(strsb, 56, 8)) / 8;
		} else {
			sectionNum = ParseUtils.binarySequenceToNumber(ParseUtils.getBits(
					strsb, 48, 8));
			lastSectionNum = ParseUtils.binarySequenceToNumber(ParseUtils
					.getBits(strsb, 56, 8));
		}

		int segment_last_section_number = ParseUtils
				.binarySequenceToNumber(ParseUtils.getBits(strsb, 96, 8)) / 8;
		Log.e("sectionNum", sectionNum + "");
		Log.e("lastSectionNum", lastSectionNum + "");
		Log.e("segment_last_section_number", segment_last_section_number + "");

		add = true;
		if (sectionList.size() == 0) {
			add = true;
		} else {
			for (int i = 0; i < sectionList.size(); i++) {
				if (sectionList.get(i) == sectionNum) {
					add = false;
					break;
				}
			}
		}
		if (add) {
			String forStr = ParseUtils.getBits(strsb, 112,
					sb.length() - 32 - 112);
			Log.e("forStr.length", forStr.length() + "");
			int tag = 84;
			int j = 16;
			int k = 96;
			int deL = 0;
			int enL = 0;
			sectionList.add(sectionNum);
			for (int i = tag; i < forStr.length(); i = i + 96 + deL * 8) {
				EITInfo eitInfo = new EITInfo();
				deL = ParseUtils.binarySequenceToNumber(ParseUtils.getBits(
						forStr, i, 12));
				byte[] tt = new byte[5];
				for (int n = 0; n < 5; n++) {
					tt[n] = data[0][j + n];
				}
				long time = TDTTime.getTDTCalendar(tt);
				SimpleDateFormat sdfTime = new SimpleDateFormat(
						context.getString(R.string.timeFomart));
				String strTime = sdfTime.format(new Date(time));
				eitInfo.setStartTime(strTime);

				SimpleDateFormat sdtDate = new SimpleDateFormat(
						context.getString(R.string.dateFormat));
				String date = sdtDate.format(new Date(time));
				eitInfo.setDate(date);
				Log.e(TAG, "date:" + date);

				byte[] dut = new byte[3];
				for (int n = 0; n < 3; n++) {
					dut[n] = data[0][j + 5 + n];
				}
				long duration = TDTTime.getTDTDuration(dut);
				long endtime = time + duration;

				eitInfo.setEndTime(sdfTime.format(new Date(endtime)));
				String desStr = ParseUtils.getBits(forStr, k, deL * 8);

				if (deL > 6) {

					enL = ParseUtils.binarySequenceToNumber(ParseUtils.getBits(
							desStr, 40, 8));
					String name = ParseUtils.binarySequenceToWords(ParseUtils
							.getBits(desStr, 48, enL * 8));
					Log.e("EventName", name);
					eitInfo.setName(name);
				}else{
					eitInfo.setName(context.getString(R.string.eventDefault));
				}
				eitInfo.setSectionNum(sectionNum);
				eitInfo.setEitCategory(EITInfo.EIT_SCHEDULE);
				eitInfo.setServerId(serverId);

				k = k + 96 + deL * 8;
				j = j + 12 + deL;
				eitList.add(eitInfo);

				if (isEITSchedule) {
					dbUtils.addEITInfo(eitInfo);
				}
			}
		}

		if (sectionList.size() < lastSectionNum + 1) {
			hasMore = true;
			Log.e("selectionList.size", sectionList.size() + "");
		} else {
			hasMore = false;
		}
		return eitList;

	}
	
	
	public interface OnEITThreadFinishedListener {

		public  void OnEITGetFollow(ArrayList<EITInfo> eitList);
		public  void OnEITGetSchedule(ArrayList<EITInfo> eitList,boolean finished);

	}
}
