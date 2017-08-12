package com.yinhe.bighomework.scanthread;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.yinhe.bighomework.obj.NITInfo;
import com.yinhe.bighomework.sqlite3.DbUtils;
import com.yinhe.bighomework.utils.Constant;
import com.yinhe.bighomework.utils.ParseUtils;
import com.yinhe.dtv.DtvBuffer;
import com.yinhe.dtv.DtvChannel;
import com.yinhe.dtv.DtvDemux;
import com.yinhe.dtv.DtvFilter;
import com.yinhe.dtv.DtvManager;
import com.yinhe.dtv.DtvSignal;
import com.yinhe.dtv.DtvTuner;

public class NITScannerThread extends Thread {
	private int frequency;
	private int symbolRate;
	private int pid;
	private byte[] u_match;
	private ArrayList<NITInfo> list = new ArrayList<NITInfo>();
	int msg;
	DtvManager dtv;
	DtvTuner tuner;
	boolean moreSection = true;
	String TAG = this.getClass().getName();
    Context context;
    DbUtils dbUtils;
	
	public NITScannerThread(Context context,int frequency, int symbolRate, DtvTuner tuner,
			DtvManager dtv) {
		// TODO Auto-generated constructor stub
		this.frequency = frequency;
		this.symbolRate = symbolRate;
		this.tuner = tuner;
		this.context=context;
		this.dtv = dtv;
		pid = Constant.NIT_PID;
		u_match = Constant.NIT_MATCH;
		dbUtils=new DbUtils(context);
	}
	int nullCount=0;
	
	
	boolean stopThread=false;
	public void stopThread(){
		stopThread=true;
	}

	@Override
	public void run() {
		Log.e(Constant.THREAD_START_TAG, "start to parse NIT");

		tuner.setParameter(new DtvTuner.DvbcPamameter(frequency, symbolRate,
				DtvTuner.QAM64, DtvTuner.SPECTRUM_AUTO));

		DtvDemux demux = dtv.getDemux(0);
		demux.linkTuner(tuner);
		DtvChannel channel = demux.createChannel(DtvChannel.SECTION,
				DtvDemux.DEMUX_CHANNEL_CRC_FORCE_AND_DISCARD, 8192);

		channel.setPid(pid);// >>>>>>>>>>>>>>>>>>>>

		DtvBuffer buffer = demux.createBuffer(1024 * 1024);
		channel.linkBuffer(buffer);

		DtvSignal signal = demux.createSignal();
		buffer.associateSignal(signal);

		byte[] mask = { (byte) 0xFF };
		byte[] match = u_match;// >>>>>>>>>>>>>>>>>>>>>>
		byte[] negate = { (byte) 0x00 };
		DtvFilter filter = demux.createFilter(mask, match, negate);
		filter.associateChannel(channel);

		list.clear();
		channel.start();
		sectionList.clear();

		moreSection = true;
		nullCount=0;
		while (moreSection&&nullCount<Constant.NULL_COUNT_10&&!stopThread) {
			signal.wait(Constant.WAIT_TIME_3000);
			byte[][] data = buffer.readData(1);
			if (data != null) {
				list.addAll(parseNIT(data));
			} else {
				Log.e(TAG, "nitTable data is null");
				nullCount++;
			}
		}

		channel.stop();
		filter.disassociateChannel(channel);
		buffer.disassociateSignal();
		channel.unlinkBuffer();

		filter.release();
		channel.release();
		buffer.release();
		signal.release();
		demux.release();
		if (onNITThreadFinishedListener != null&&!stopThread) {
			onNITThreadFinishedListener.OnNITThreadFinished(list);
			Log.e(TAG, "nitList size:"+list.size());
		}

	}

	OnNITThreadFinishedListener onNITThreadFinishedListener;

	public void setOnNITThreadFinshedListener(
			OnNITThreadFinishedListener onNITThreadFinishedListener) {
		this.onNITThreadFinishedListener = onNITThreadFinishedListener;
	}

	ArrayList<Integer> sectionList = new ArrayList<Integer>();

	private ArrayList<NITInfo> parseNIT(byte[][] data) {
		ArrayList<NITInfo> nitList = new ArrayList<NITInfo>();

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			sb.append(ParseUtils.byteToBinarySequence(data[i]));
		}

		String strsb = sb.toString();
		int sectionNum = ParseUtils.binarySequenceToNumber(ParseUtils.getBits(
				strsb, 48, 8));
		int lastSectionNum = ParseUtils.binarySequenceToNumber(ParseUtils
				.getBits(strsb, 56, 8));
		Log.e("NITScannerThread sectionNum", sectionNum + "");
		Log.e("NITScannerThread lastSectionNum", lastSectionNum + "");
		boolean add = true;
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
			sectionList.add(sectionNum);
			String netdeL = ParseUtils.getBits(strsb, 68, 12);
			int ndL = ParseUtils.binarySequenceToNumber(netdeL);

			String tsloL = ParseUtils.getBits(strsb, 84 + ndL * 8, 12);
			int tsL = ParseUtils.binarySequenceToNumber(tsloL);

			int tag = 96 + ndL * 8;

			String forStr = ParseUtils.getBits(strsb, tag, sb.length() - 32
					- tag);

			int j = 48;
			int tdL = 0;
			for (int i = 36; i < forStr.length(); i = i + 48 + tdL * 8) {
				NITInfo nitObject = new NITInfo();

				String tsdL = ParseUtils.getBits(forStr, i, 12);
				tdL = ParseUtils.binarySequenceToNumber(tsdL);

				String desStr = ParseUtils.getBits(forStr, j, tdL * 8);

				int frequency = ParseUtils.binarySequenceToNumber(ParseUtils
						.getBits(desStr, 16, 32));
				String strFrequency = String.format("%02x", frequency);
				int ff = Integer.parseInt(strFrequency);

				int symbol_rate = ParseUtils.binarySequenceToNumber(ParseUtils
						.getBits(desStr, 72, 28));
				int sr = Integer.parseInt(String.format("%02x", symbol_rate));

				nitObject.setFrequency(ff / 10);
				nitObject.setSymbolRate(sr * 100);

				j = j + 48 + tdL * 8;
				if(dbUtils!=null){
					dbUtils.addFrequencyInfo(nitObject);
				}
				nitList.add(nitObject);
			}
		}

		if (sectionList.size() < lastSectionNum + 1) {
			moreSection = true;
		} else {
			moreSection = false;
		}

		return nitList;

	}
	
	
	public interface OnNITThreadFinishedListener {

		public  void OnNITThreadFinished(ArrayList<NITInfo> nitList);

	}

}
