package com.yinhe.bighomework.scanthread;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yinhe.bighomework.utils.Constant;
import com.yinhe.bighomework.utils.ParseUtils;
import com.yinhe.dtv.DtvBuffer;
import com.yinhe.dtv.DtvChannel;
import com.yinhe.dtv.DtvDemux;
import com.yinhe.dtv.DtvFilter;
import com.yinhe.dtv.DtvManager;
import com.yinhe.dtv.DtvSignal;
import com.yinhe.dtv.DtvTuner;

public class PATScannerThread extends Thread {

	private int frequency;
	private int symbolRate;
	private int pid;
	private byte[] u_match;
	private ArrayList<Integer> list = new ArrayList<Integer>();
	int msg;
	DtvTuner tuner;
	DtvManager dtv;
	private boolean hasMore = true;
	String TAG = this.getClass().getName();
	int nullCount = 0;
	Context context;

	public PATScannerThread(Context context,int frequency, int symbolRate,
			DtvTuner tuner, DtvManager dtv) {
		// TODO Auto-generated constructor stub
		this.frequency = frequency;
		this.symbolRate = symbolRate;
		this.tuner = tuner;
		this.dtv = dtv;
		this.context=context;
		pid=Constant.PAT_PID;
		u_match=Constant.PAT_MATCH;
	}
	
	boolean stopThread=false;
	public void stopThread(){
		stopThread=true;
	}

	@Override
	public void run() {
		
		Intent intent=new Intent(Constant.ACTION_FREQUENCY_CHANGE);
		intent.putExtra(Constant.EXTRA_FREQUENCY, frequency);
		context.sendBroadcast(intent);
		Log.e(Constant.THREAD_START_TAG, "start to parse PAT");
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

		channel.start();

		hasMore = true;
		nullCount = 0;
		signal.wait(Constant.WAIT_TIME_1500);
		while (hasMore&&nullCount<Constant.NULL_COUNT_2&&!stopThread) {
			signal.wait(Constant.WAIT_TIME_100);
			byte[][] data = buffer.readData(1);
			if (data != null) {
				list.addAll(parsePAT(data));
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
		
		if(onPAThreadFinishedListener!=null&&!stopThread){
			onPAThreadFinishedListener.OnPAThreadFinished(list,frequency,symbolRate);
		}
		
	}

	OnPAThreadFinishedListener onPAThreadFinishedListener;
	public void setOnPAThreadFinishedListener(OnPAThreadFinishedListener onPAThreadFinishedListener){
		this.onPAThreadFinishedListener=onPAThreadFinishedListener;
	}
	
	ArrayList<Integer> sectionList = new ArrayList<Integer>();

	public ArrayList<Integer> parsePAT(byte[][] sdata) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		byte[][] data = sdata;
		String pid;
		int pronum = 0;
		boolean add = false;

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			sb.append(ParseUtils.byteToBinarySequence(data[i]));
		}
		String strsb = sb.toString();
		int sectionNum = ParseUtils.binarySequenceToNumber(ParseUtils.getBits(
				strsb, 48, 8));
		int lastSectionNum = ParseUtils.binarySequenceToNumber(ParseUtils
				.getBits(strsb, 56, 8));
		Log.e("PATScannerThread sectionNum", sectionNum + "");
		Log.e("PATScannerThread lastSectionNum", lastSectionNum + "");
		add = true;
		if (sectionList.size() == 0) {
			add = true;
		} else {
			for (Integer section : sectionList) {
				if (section == sectionNum) {
					add = false;
					break;
				}
			}
		}

		if (add) {
			sectionList.add(sectionNum);
			int pronumCur = 64;
			int pidCur = 83;
			for (int i = pronumCur; i < (sb.length() - 32); i = i + 32) {
				String pro = ParseUtils.getBits(strsb, i, 16);
				pronum = ParseUtils.binarySequenceToNumber(pro);
			}
			if (pronum == 0) {
				for (int i = pidCur; i < (sb.length() - 32); i = i + 32) {
					String net = ParseUtils.getBits(strsb, i, 13);
				}
			} else {
				for (int i = pidCur; i < (sb.length() - 32); i = i + 32) {
					pid = ParseUtils.getBits(strsb, i, 13);
					int PID = ParseUtils.binarySequenceToNumber(pid);
					list.add(PID);
				}
			}
		}

		if (sectionList.size() < lastSectionNum + 1) {
			hasMore = true;
		} else {
			hasMore = false;
		}

		return list;

	}
	
	public interface OnPAThreadFinishedListener {

		public  void OnPAThreadFinished(ArrayList<Integer> pidList,int frequency,int symbolRate);

	}

}
