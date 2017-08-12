package com.yinhe.bighomework.scanthread;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.yinhe.bighomework.obj.NITInfo;
import com.yinhe.bighomework.obj.PMTInfo;
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

public class CAScannerThread extends Thread {

	// (int frequency,int symbolRate,int pid,byte[] u_match)
	ArrayList<Integer> pidList;
	int frequency;
	int symbolRate;
	byte[] u_match;
	private ArrayList<PMTInfo> pmtList;
	int msgCA;
	int msgNoCA;
	DbUtils dbUtils;
	DtvTuner tuner;
	DtvManager dtv;
	volatile boolean moreSection = true;
	int nullCount = 0;
	String TAG = this.getClass().getName();
	int CA = -1;
	int caPid;
	Context context;
	NITInfo nitInfo;
	byte[] mask = { (byte) 0xFF };
	byte[] negate = { (byte) 0x00 };
	volatile boolean stopThread=false;

	public CAScannerThread(Context context, DtvTuner tuner, DtvManager dtv,
			NITInfo nitInfo) {
		// TODO Auto-generated constructor stub
		this.tuner = tuner;
		this.dtv = dtv;
		this.context = context;
		caPid = Constant.CA_PID;
		u_match = Constant.CA_MATCH;
		dbUtils = new DbUtils(context);
		this.nitInfo = nitInfo;
	}

	@Override
	public void run() {
		Log.e(Constant.THREAD_START_TAG, "start to parse CAT");
		int frequency = nitInfo.getFrequency();
		int symbolRate = nitInfo.getSymbolRate();
		tuner.setParameter(new DtvTuner.DvbcPamameter(frequency, symbolRate,
				DtvTuner.QAM64, DtvTuner.SPECTRUM_AUTO));
		DtvDemux demux = dtv.getDemux(0);
		demux.linkTuner(tuner);
		pmtList = new ArrayList<PMTInfo>();

		DtvChannel channel = demux.createChannel(DtvChannel.SECTION,
				DtvDemux.DEMUX_CHANNEL_CRC_FORCE_AND_DISCARD, 8192);
		channel.setPid(caPid);

		DtvBuffer buffer = demux.createBuffer(1024 * 1024);
		channel.linkBuffer(buffer);

		DtvSignal signal = demux.createSignal();
		buffer.associateSignal(signal);

		DtvFilter filter = demux.createFilter(mask, u_match, negate);
		filter.associateChannel(channel);

		channel.start();
		sectionList.clear();
		moreSection = true;
		signal.wait(Constant.WAIT_TIME_1000);
		while (moreSection&&nullCount<Constant.NULL_COUNT_2&&!stopThread) {

			signal.wait(Constant.WAIT_TIME_100);
			byte[][] data = buffer.readData(1);
			if (data != null) {
				CA = parseCAT(data);
				dbUtils.addCAInfo(CA, frequency);

			} else {
				Log.e(TAG, "data is null");
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
		
		if(onCAThreadFinished!=null&&!stopThread){
			onCAThreadFinished.OnCAThreadFinished();
		}

	}
	
	public void stopThread(){
		stopThread=true;
	}
	
	public boolean getStopThread(){
		return stopThread;
	}

	ArrayList<Integer> sectionList = new ArrayList<Integer>();
	boolean add = true;

	OnCAThreadFinishedListener onCAThreadFinished;

	public void setOnCAThreadFinishedListener(
			OnCAThreadFinishedListener onCAThreadFinished) {
		this.onCAThreadFinished = onCAThreadFinished;
	}

	public int parseCAT(byte[][] data) {
		int CA_Pid = -1;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			sb.append(ParseUtils.byteToBinarySequence(data[i]));
		}

		String strsb = sb.toString();
		String forStr = ParseUtils.getBits(strsb, 64, sb.length() - 32 - 64);
		int sectionNum = ParseUtils.binarySequenceToNumber(ParseUtils.getBits(
				strsb, 48, 8));
		int lastSectionNum = ParseUtils.binarySequenceToNumber(ParseUtils
				.getBits(strsb, 56, 8));
		Log.e("sectionNum", sectionNum + "");
		Log.e("lastSectionNum", lastSectionNum + "");

		add = true;
		if (sectionList.size() == 0) {
			add = true;
		} else {
			for (int i : sectionList) {
				if (i == sectionNum) {
					add = false;
					break;
				}
			}
		}
		if (add) {
			sectionList.add(sectionNum);
			int cadeL = 0;
			for (int n = 0; n < forStr.length(); n = n + 16 + cadeL * 8) {
				String tag = ParseUtils.getBits(forStr, n, 8);
				int itag = ParseUtils.binarySequenceToNumber(tag);

				String cadescL = ParseUtils.getBits(forStr, n + 8, 8);
				cadeL = ParseUtils.binarySequenceToNumber(cadescL);
				if (itag == 9) {
					CA_Pid = ParseUtils.binarySequenceToNumber(ParseUtils
							.getBits(forStr, 35 + n, 13));
					Log.e("CAT_CA_Pid", CA_Pid + "");
					moreSection = false;
					return CA_Pid;
				}
			}
		}

		if (sectionList.size() < lastSectionNum + 1) {
			moreSection = true;
		} else {
			moreSection = false;
		}
		return CA_Pid;
	}
	
	public interface OnCAThreadFinishedListener {

		public  void OnCAThreadFinished();

	}

}
