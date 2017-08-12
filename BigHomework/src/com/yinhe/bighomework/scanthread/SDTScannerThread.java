package com.yinhe.bighomework.scanthread;

import java.util.ArrayList;

import android.util.Log;

import com.yinhe.bighomework.obj.SDTInfo;
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

public class SDTScannerThread extends Thread {

	// frequency 387000
	// 6875000
	// pid
	// match

	public static String DEBUG = ">>>>>>>>>>>>>>>";
	public static String TAG = "SDTScannerThread";
	private int frequency;
	private int symbolRate;
	private int pid;
	private byte[] u_match;
	private ArrayList<SDTInfo> list = new ArrayList<SDTInfo>();
	int msg;
	DbUtils dbUtils;
	DtvManager dtv;
	DtvTuner tuner;
	boolean moreSection = true;
	int nullCount = 0;

	public SDTScannerThread(int frequency, int symbolRate, DbUtils dbUtils,
			DtvTuner tuner, DtvManager dtv) {
		// TODO Auto-generated constructor stub
		this.dbUtils = dbUtils;
		this.tuner = tuner;
		this.dtv = dtv;
		this.frequency = frequency;
		this.symbolRate = symbolRate;
		pid = Constant.SDT_PID;
		u_match = Constant.SDT_MATCH;
	}

	boolean stopThread = false;

	public void stopThread() {
		stopThread = true;
	}

	private void getOneSDT(int frequency, int symbolRate) {
		Log.e("pppppp  frequency", frequency + "");
		Log.e("pppppp symbolRate", symbolRate + "");

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
		signal.wait(Constant.WAIT_TIME_1000);
		while (moreSection && nullCount < Constant.NULL_COUNT_2 && !stopThread) {
			signal.wait(Constant.WAIT_TIME_500);
			byte[][] data = buffer.readData(1);
			if (data != null) {
				list.addAll(parseSDT(data));
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
		Log.e("SDTScannerThread  send", msg + "");
		if (onSDThreadFinishedListener != null && !stopThread) {
			onSDThreadFinishedListener.onSDTThreadFinished(list);
		}

	}

	@Override
	public void run() {
		Log.e(Constant.THREAD_START_TAG, "start to parse SDT");
		getOneSDT(frequency, symbolRate);

	}

	OnSDTThreadFinishedListener onSDThreadFinishedListener;

	public void setOnSDThreadFinishedListener(
			OnSDTThreadFinishedListener onSDThreadFinishedListener) {
		this.onSDThreadFinishedListener = onSDThreadFinishedListener;

	}

	ArrayList<Integer> sectionList = new ArrayList<Integer>();
	boolean add = true;

	public ArrayList<SDTInfo> parseSDT(byte[][] sdata) {

		ArrayList<SDTInfo> sdtList = new ArrayList<SDTInfo>();
		byte[][] data = sdata;
		StringBuilder sb = new StringBuilder();
		sb.append(ParseUtils.byteToBinarySequence(data[0]));

		String str = sb.toString();

		int sectionNum = ParseUtils.binarySequenceToNumber(ParseUtils.getBits(
				str, 48, 8));
		int lastSectionNum = ParseUtils.binarySequenceToNumber(ParseUtils
				.getBits(str, 56, 8));
		Log.e(" SDTScannerThread sectionNum", sectionNum + "");
		Log.e("SDTScannerThread lastSectionNum", lastSectionNum + "");

		add = true;
		if (sectionList.size() == 0) {
			add = true;
		} else {
			for (int num : sectionList) {
				if (num == sectionNum) {
					add = false;
					break;
				}
			}
		}

		if (add) {
			sectionList.add(sectionNum);
			int cursor = 0;
			int totalLen = str.length();
			cursor = 12;
			int sectionLen = ParseUtils.binarySequenceToNumber(ParseUtils
					.getBits(str, cursor, 12)) * 8;
			cursor = 88;// jump to big Loop

			while (cursor < totalLen - 32) {

				SDTInfo sdtInfo = new SDTInfo();
				int service_id = ParseUtils.binarySequenceToNumber(ParseUtils
						.getBits(str, cursor, 16));
				sdtInfo.setServerId(service_id);
				sdtInfo.setFrequency(frequency);
				sdtInfo.setSymbolRate(symbolRate);
				cursor += 2 * 8;
				cursor += 6;
				int EIT_schedule_flag = ParseUtils
						.binarySequenceToNumber(ParseUtils.getBits(str, cursor,
								1));
				sdtInfo.setSchedulFlag(EIT_schedule_flag);
				cursor += 1;
				int EIT_present_following_flag = ParseUtils
						.binarySequenceToNumber(ParseUtils.getBits(str, cursor,
								1));

				sdtInfo.setFollowingFlag(EIT_present_following_flag);

				cursor += 1;
				cursor += 3;

				int free_CA_mode = ParseUtils.binarySequenceToNumber(ParseUtils
						.getBits(str, cursor, 1));
				sdtInfo.setCA_MODE(free_CA_mode);
				Log.e("free_CA_mode", free_CA_mode + "");
				cursor += 1;

				// cursor += 4 * 8;// 这四个字节有service_id
				int serviceLen = ParseUtils.binarySequenceToNumber(ParseUtils
						.getBits(str, cursor, 12)) * 8;// 每个节目所占有的长度
				cursor += 12;

				Log.e("serviceLen>>>>>>>>>>", serviceLen + "");
				Log.e("SDT strLen>>>>>>>>>>>>>>>>>>", totalLen + "");

				// 进入子循环，获取节目名和节目分类
				if (serviceLen > totalLen) {
					break;
				}
				while (serviceLen > 0) {
					int tag = ParseUtils.binarySequenceToNumber(ParseUtils
							.getBits(str, cursor, 8));

					serviceLen -= 8;
					cursor += 8;

					int decriptorLen = ParseUtils
							.binarySequenceToNumber(ParseUtils.getBits(str,
									cursor, 8));
					serviceLen -= 8;
					cursor += 8;
					StringBuilder sbCategory = new StringBuilder();
					Log.e("SDT decriptor tag", String.format("%02x", tag));

					if (tag == 0x48) {//
						serviceLen -= 8;
						cursor += 8;

						int service_provider_name_lenght = ParseUtils
								.binarySequenceToNumber(ParseUtils.getBits(str,
										cursor, 8));
						serviceLen -= 8;
						cursor += 8;

						serviceLen -= 8 * service_provider_name_lenght;
						cursor += 8 * service_provider_name_lenght;

						serviceLen -= 8;
						cursor += 8;

						String temp = ParseUtils
								.binarySequenceToWords(ParseUtils.getBits(str,
										cursor, (decriptorLen - 3) * 8));

						serviceLen -= (decriptorLen - 3) * 8;
						cursor += (decriptorLen - 3) * 8;
						sdtInfo.setServerName(temp);
					} else if (tag == 0x47) {
						String category = ParseUtils
								.binarySequenceToWords(ParseUtils.getBits(str,
										cursor, decriptorLen * 8));
						sbCategory.append(category + ",");
						serviceLen -= decriptorLen * 8;
						cursor += decriptorLen * 8;
						sdtInfo.setCategory(sbCategory.toString());
					} else {
						serviceLen -= decriptorLen * 8;
						cursor += decriptorLen * 8;
					}
				}
				if (dbUtils != null) {
					dbUtils.addSDTInfo(sdtInfo);
				}
				sdtList.add(sdtInfo);
			}
		}

		if (sectionList.size() < lastSectionNum + 1) {
			moreSection = true;
		} else {
			moreSection = false;
		}

		return sdtList;

	}
	
	
	public interface OnSDTThreadFinishedListener {

		public void onSDTThreadFinished(ArrayList<SDTInfo> sdtList);

	}
}
