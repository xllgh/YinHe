package com.yinhe.bighomework.scanthread;

import java.util.ArrayList;

import android.util.Log;

import com.yinhe.bighomework.obj.PMTInfo;
import com.yinhe.bighomework.sqlite3.DbUtils;
import com.yinhe.bighomework.utils.Constant;
import com.yinhe.bighomework.utils.DecoderTypeUtils;
import com.yinhe.bighomework.utils.ParseUtils;
import com.yinhe.dtv.DtvBuffer;
import com.yinhe.dtv.DtvChannel;
import com.yinhe.dtv.DtvDemux;
import com.yinhe.dtv.DtvFilter;
import com.yinhe.dtv.DtvManager;
import com.yinhe.dtv.DtvSignal;
import com.yinhe.dtv.DtvTuner;

public class PMTScannerThread extends Thread {

	// (int frequency,int symbolRate,int pid,byte[] u_match)
	ArrayList<Integer> pidList;
	int frequency;
	int symbolRate;
	byte[] u_match;
	private ArrayList<PMTInfo> pmtList;
	int msg;
	DbUtils dbUtils;
	DtvTuner tuner;
	DtvManager dtv;
	boolean moreSection = true;
	int nullCount = 0;
	String TAG = this.getClass().getName();

	public PMTScannerThread(ArrayList<Integer> pidList, int frequency,
			int symbolRate, DbUtils dbUtils, DtvTuner tuner, DtvManager dtv) {
		// TODO Auto-generated constructor stub
		this.pidList = pidList;
		this.frequency = frequency;
		this.symbolRate = symbolRate;
		this.dbUtils = dbUtils;
		this.tuner = tuner;
		this.dtv = dtv;
		u_match = Constant.PMT_MATCH;

	}
	
	boolean stopThread=false;
	public void stopThread(){
		stopThread=true;
	}

	@Override
	public void run() {
		Log.e(Constant.THREAD_START_TAG, "start to parse PMT");

		byte[] mask = { (byte) 0xFF };
		byte[] negate = { (byte) 0x00 };
		byte[] match = u_match;

		tuner.setParameter(new DtvTuner.DvbcPamameter(frequency, symbolRate,
				DtvTuner.QAM64, DtvTuner.SPECTRUM_AUTO));
		DtvDemux demux = dtv.getDemux(0);
		demux.linkTuner(tuner);
		pmtList = new ArrayList<PMTInfo>();

		for (int i = 0; i < pidList.size(); i++) {
			if(stopThread){
				return;
			}
			DtvChannel channel = demux.createChannel(DtvChannel.SECTION,
					DtvDemux.DEMUX_CHANNEL_CRC_FORCE_AND_DISCARD, 8192);
			channel.setPid(pidList.get(i));
			DtvBuffer buffer = demux.createBuffer(1024 * 1024);
			channel.linkBuffer(buffer);

			DtvSignal signal = demux.createSignal();
			buffer.associateSignal(signal);

			DtvFilter filter = demux.createFilter(mask, match, negate);
			filter.associateChannel(channel);
			channel.start();
			sectionList.clear();
			moreSection = true;
			nullCount = 0;
			signal.wait(Constant.WAIT_TIME_1000);
			while (moreSection && nullCount < Constant.NULL_COUNT_2&&!stopThread) {
				signal.wait(Constant.WAIT_TIME_100);
				byte[][] data = buffer.readData(1);
				if (data != null) {
					parsePMT(data, pidList.get(i));
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
		}
		demux.release();

		if (onPMThreadFinishedListener != null&&!stopThread) {
			onPMThreadFinishedListener.OnPMThreadFinished(pmtList, frequency, symbolRate);
		}
	}

	OnPMThreadFinishedListener onPMThreadFinishedListener;

	public void setOnPMThreadFinishedListener(
			OnPMThreadFinishedListener onPMThreadFinishedListener) {
		this.onPMThreadFinishedListener = onPMThreadFinishedListener;

	}

	ArrayList<Integer> sectionList = new ArrayList<Integer>();
	boolean add = true;

	public void parsePMT(byte[][] sdata, int serverId) {

		byte[][] data = sdata;
		int eiL = 0;

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			sb.append(ParseUtils.byteToBinarySequence(data[i]));
		}

		String strsb = sb.toString();
		int sectionNum = ParseUtils.binarySequenceToNumber(ParseUtils.getBits(
				strsb, 48, 8));
		int lastSectionNum = ParseUtils.binarySequenceToNumber(ParseUtils
				.getBits(strsb, 56, 8));
		int PCR_PID = ParseUtils.binarySequenceToNumber(ParseUtils.getBits(
				strsb, 67, 13));
		Log.e("PMTScannerThread PCR_PID", PCR_PID + "");
		Log.e("PMTScannerThread sectionNum", sectionNum + "");
		Log.e("PMTScannerThread lastSectionNum", lastSectionNum + "");

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
			PMTInfo pmtInfo = new PMTInfo();
			pmtInfo.setFrequency(frequency);
			pmtInfo.setSymbolRate(symbolRate);
			sectionList.add(sectionNum);
			int program_number = ParseUtils.binarySequenceToNumber(ParseUtils
					.getBits(strsb, 24, 16));
			pmtInfo.setServerId(program_number);
			Log.e("program_number", program_number + "");

			String pcrpid = ParseUtils.getBits(strsb, 67, 13);
			String proinfoL = ParseUtils.getBits(strsb, 84, 12);
			int piL = ParseUtils.binarySequenceToNumber(proinfoL);

			String desforStr = ParseUtils.getBits(strsb, 96, piL * 8);
			int cadeL = 0;
			for (int n = 0; n < desforStr.length(); n = n + 16 + cadeL * 8) {

				String destag = ParseUtils.getBits(desforStr, n, 8);
				int destag1 = ParseUtils.binarySequenceToNumber(destag);

				String cadesL = ParseUtils.getBits(desforStr, n + 8, 8);
				cadeL = ParseUtils.binarySequenceToNumber(cadesL);

				if (destag1 == 9) {
					String CA_Pid = ParseUtils.getBits(desforStr, 35 + n, 13);
					int ca = ParseUtils.binarySequenceToNumber(CA_Pid);
					pmtInfo.setEcmPid(ca);
				}
			}

			int pmtcur = 124 + piL * 8;
			int j = 96 + piL * 8;

			for (int i = pmtcur; i < (sb.length() - 32); i = i + 40 + eiL * 8) {

				String esinfoL = ParseUtils.getBits(strsb, i, 12);
				eiL = ParseUtils.binarySequenceToNumber(esinfoL);

				String stream_type = ParseUtils.getBits(strsb, j, 8);
				int iStreamType = ParseUtils
						.binarySequenceToNumber(stream_type);

				String elementary_PID = ParseUtils.getBits(strsb, j + 11, 13);
				int iElementaryPID = ParseUtils
						.binarySequenceToNumber(elementary_PID);

				// 设置音视频流类型
				DecoderTypeUtils.setDecoderType(iStreamType, pmtInfo,
						iElementaryPID);
				j = j + 40 + eiL * 8;
				if (((sb.length() - 32)) < i + 40 + eiL * 8) {
					break;
				}
			}
			if (dbUtils != null) {
				dbUtils.addPMTInfo(pmtInfo);
			}
			pmtList.add(pmtInfo);

		}

		if (sectionList.size() < lastSectionNum + 1) {
			moreSection = true;
		} else {
			moreSection = false;
		}

	}
	
	
	public interface OnPMThreadFinishedListener {

		public  void OnPMThreadFinished(ArrayList<PMTInfo> pmtList,int frequency,int symbolRate);

	}

}
