package com.yinhe.bighomework.service;

import java.util.ArrayList;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.yinhe.bighomework.custominterface.OnGetTunerListener;
import com.yinhe.bighomework.obj.EITInfo;
import com.yinhe.bighomework.obj.NITInfo;
import com.yinhe.bighomework.obj.PMTInfo;
import com.yinhe.bighomework.obj.SDTInfo;
import com.yinhe.bighomework.scanthread.CAScannerThread;
import com.yinhe.bighomework.scanthread.CAScannerThread.OnCAThreadFinishedListener;
import com.yinhe.bighomework.scanthread.EITScannerThread.OnEITThreadFinishedListener;
import com.yinhe.bighomework.scanthread.NITScannerThread;
import com.yinhe.bighomework.scanthread.NITScannerThread.OnNITThreadFinishedListener;
import com.yinhe.bighomework.scanthread.PATScannerThread;
import com.yinhe.bighomework.scanthread.PATScannerThread.OnPAThreadFinishedListener;
import com.yinhe.bighomework.scanthread.PMTScannerThread;
import com.yinhe.bighomework.scanthread.PMTScannerThread.OnPMThreadFinishedListener;
import com.yinhe.bighomework.scanthread.SDTScannerThread;
import com.yinhe.bighomework.scanthread.SDTScannerThread.OnSDTThreadFinishedListener;
import com.yinhe.bighomework.sqlite3.DbUtils;
import com.yinhe.bighomework.utils.Constant;
import com.yinhe.dtv.DtvManager;
import com.yinhe.dtv.DtvTuner;

public class ParseService extends Service implements
		OnCAThreadFinishedListener, OnEITThreadFinishedListener,
		OnNITThreadFinishedListener, OnPAThreadFinishedListener,
		OnPMThreadFinishedListener, OnSDTThreadFinishedListener {
	public String TAG = "ParseService";
	boolean stopScan = false;
	private DbUtils dbUtils;
	private DtvManager dtvManager;
	private DtvTuner dtvTunner;

	public ParseService() {
		Log.e(TAG, "constractor");
	}

	PATScannerThread patThread;
	PMTScannerThread pmtThread;
	SDTScannerThread sdtThread;
	NITScannerThread nitThread;
	CAScannerThread caThread;

	BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			int frequency = intent.getIntExtra(Constant.EXTRA_FREQUENCY, 0);
			int symbolRate = intent.getIntExtra(Constant.EXTRA_SYMBOLRATE, 0);
			switch (action) {
			case Constant.ACTION_AUTO_SCAN:
				Log.e(TAG, "receivie" + Constant.ACTION_AUTO_SCAN);
				nitThread = new NITScannerThread(ParseService.this, frequency,
						symbolRate, dtvTunner, dtvManager);
				nitThread.setOnNITThreadFinshedListener(ParseService.this);
				nitThread.start();
				break;

			case Constant.ACTION_MANUEL_SCAN:
				Log.e(TAG, "receivie" + Constant.ACTION_MANUEL_SCAN);
				NITInfo nitInfo = new NITInfo();
				nitInfo.setFrequency(frequency);
				nitInfo.setSymbolRate(symbolRate);
				list.add(nitInfo);
				dbUtils.addFrequencyInfo(nitInfo);
				caThread = new CAScannerThread(ParseService.this, dtvTunner,
						dtvManager, nitInfo);
				caThread.setOnCAThreadFinishedListener(ParseService.this);
				caThread.start();
				intentProgress.putExtra(Constant.EXTRA_PROGRESS,
						Constant.AUTO_ADD);
				sendBroadcast(intentProgress);

				break;

			case Constant.ACTION_START_CA:

				break;

			case Constant.ACTION_STOP_SCAN:
				Log.e(TAG, "receivie" + Constant.ACTION_STOP_SCAN);
				stopScan = false;
				if (nitThread != null) {
					Log.e(TAG, "stop nitThread");
					nitThread.stopThread();
				}
				if (caThread != null) {
					Log.e(TAG, "stop caThread");
					caThread.stopThread();
				}

				if (patThread != null) {
					Log.e(TAG, "stop patThread");
					patThread.stopThread();
				}
				if (pmtThread != null) {
					Log.e(TAG, "stop pmtThread");
					pmtThread.stopThread();
				}
				if (sdtThread != null) {
					Log.e(TAG, "stop sdtThread");
					sdtThread.stopThread();
				}

				break;

			default:
				break;
			}
		}

	};

	OnGetTunerListener onGetTunerListener;

	public void setOnGetTunnerListener(OnGetTunerListener onGetTunerListener) {
		this.onGetTunerListener = onGetTunerListener;
	}

	@Override
	public void onCreate() {
		Log.e(TAG, "onCreate");
		dbUtils = new DbUtils(this);
		dtvManager = DtvManager.getInstance();

		if (dtvTunner == null) {
			dtvTunner = dtvManager.getTuner(0);
		}

		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.ACTION_MANUEL_SCAN);
		filter.addAction(Constant.ACTION_AUTO_SCAN);
		filter.addAction(Constant.ACTION_STOP_SCAN);
		filter.addAction(Constant.ACTION_START_CA);
		registerReceiver(mReceiver, filter);
		super.onCreate();
	}

	public class LocalBinder extends Binder {
		public ParseService getService() {
			return ParseService.this;
		}
	}

	private final LocalBinder mBinder = new LocalBinder();

	public DtvTuner getTunner() {
		return dtvTunner;
	}

	public DtvManager getDtvManager() {
		return dtvManager;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onDestroy() {
		Log.e(TAG, "onDestroy");

		unregisterReceiver(mReceiver);
		if(dtvManager!=null){
			dtvManager.release();
			dtvManager=null;
		}
		if (dtvTunner != null) {
			dtvTunner.release();
			dtvTunner = null;
		}

		super.onDestroy();
	}

	@Override
	public void OnEITGetFollow(ArrayList<EITInfo> eitList) {

	}

	@Override
	public void OnEITGetSchedule(ArrayList<EITInfo> eitList,boolean finished) {

	}

	ArrayList<NITInfo> list = new ArrayList<>();
	int count = 0;
	Intent intentProgress = new Intent(Constant.ACTION_PROGRESS);

	@Override
	public void OnNITThreadFinished(ArrayList<NITInfo> nitList) {
		Log.e(TAG, "OnNITThreadFinished");

		intentProgress.putExtra(Constant.EXTRA_PROGRESS, Constant.AUTO_ADD / 2);
		sendBroadcast(intentProgress);
		count = 0;
		list.clear();
		list.addAll(nitList);
		Log.e(TAG, list.toString());
		if (list.size() == 0) {
			sendBroadcast(new Intent(Constant.ACTION_SCAN_FREQUENCY_FAILED));
		}

		if (list.size() > count) {
			NITInfo nitInfo = list.get(count);
			caThread = new CAScannerThread(ParseService.this, dtvTunner,
					dtvManager, nitInfo);
			caThread.setOnCAThreadFinishedListener(this);
			caThread.start();
		}

	}

	@Override
	public void OnCAThreadFinished() {
		// TODO Auto-generated method stub
		if (list.size() > count&&!caThread.getStopThread()) {
			NITInfo nitInfo = list.get(count);
			int frequency = nitInfo.getFrequency();
			int symbolRate = nitInfo.getSymbolRate();
			patThread = new PATScannerThread(ParseService.this, frequency,
					symbolRate, dtvTunner, dtvManager);
			patThread.setOnPAThreadFinishedListener(ParseService.this);
			patThread.start();
		}
	}

	@Override
	public void OnPAThreadFinished(ArrayList<Integer> pidList, int frequency,
			int symbolRate) {
		intentProgress.putExtra(Constant.EXTRA_PROGRESS,
				Constant.PROGRESS_MAX_MANUEL / list.size() / 3);
		sendBroadcast(intentProgress);
		pmtThread = new PMTScannerThread(pidList, frequency, symbolRate,
				dbUtils, dtvTunner, dtvManager);
		pmtThread.setOnPMThreadFinishedListener(ParseService.this);
		pmtThread.start();
	}

	@Override
	public void OnPMThreadFinished(ArrayList<PMTInfo> pmtList, int frequency,
			int symbolRate) {
		intentProgress.putExtra(Constant.EXTRA_PROGRESS,
				Constant.PROGRESS_MAX_MANUEL / list.size() / 3);
		sendBroadcast(intentProgress);
		sdtThread = new SDTScannerThread(frequency, symbolRate, dbUtils,
				dtvTunner, dtvManager);
		sdtThread.setOnSDThreadFinishedListener(ParseService.this);
		sdtThread.start();

	}

	@Override
	public void onSDTThreadFinished(ArrayList<SDTInfo> sdtList) {
		// 发送广播，刷新列表
		ParseService.this
				.sendBroadcast(new Intent(Constant.ACTION_REFRESH_LIST));
		intentProgress.putExtra(Constant.EXTRA_PROGRESS,
				Constant.PROGRESS_MAX_MANUEL / list.size() / 3);

		sendBroadcast(intentProgress);

		count++;
		if (count < list.size()) {
			if (list.size() > count) {
				NITInfo nitInfo = list.get(count);
				CAScannerThread caThread = new CAScannerThread(
						ParseService.this, dtvTunner, dtvManager, nitInfo);
				caThread.setOnCAThreadFinishedListener(this);
				caThread.start();
			}
		} else {
			Intent finishIntent = new Intent(Constant.ACTION_SCAN_FINISH);
			sendBroadcast(finishIntent);

			Intent startCAIntent = new Intent(Constant.ACTION_START_CA);
			sendBroadcast(startCAIntent);

		}
	}

}
