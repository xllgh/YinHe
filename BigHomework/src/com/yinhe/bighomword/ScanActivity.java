package com.yinhe.bighomword;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.yinhe.bighomework.adapter.EpgItemAdapter;
import com.yinhe.bighomework.adapter.ServiceAdapter;
import com.yinhe.bighomework.obj.EITInfo;
import com.yinhe.bighomework.obj.NITInfo;
import com.yinhe.bighomework.obj.PMTInfo;
import com.yinhe.bighomework.obj.SDTInfo;
import com.yinhe.bighomework.obj.ServerInfo;
import com.yinhe.bighomework.scanthread.EITScannerThread;
import com.yinhe.bighomework.scanthread.EITScannerThread.OnEITThreadFinishedListener;
import com.yinhe.bighomework.service.ParseService;
import com.yinhe.bighomework.service.ParseService.LocalBinder;
import com.yinhe.bighomework.sqlite3.DBContract.EITTable;
import com.yinhe.bighomework.sqlite3.DbUtils;
import com.yinhe.bighomework.utils.ActionBarSetting;
import com.yinhe.bighomework.utils.Constant;
import com.yinhe.bighomwork.R;
import com.yinhe.dtv.DtvDemux;
import com.yinhe.dtv.DtvDescrambler;
import com.yinhe.dtv.DtvManager;
import com.yinhe.dtv.DtvTuner;
import com.yinhe.dtv.DvbPlayer;
import com.yinhe.dtv.ca.CdcasManager;

public class ScanActivity extends Activity implements OnClickListener,
		OnItemClickListener, SurfaceHolder.Callback,
		CdcasManager.ICdcasListener, OnEITThreadFinishedListener,
		OnItemSelectedListener {
	private EditText etFrequency;
	private ListView listView;
	private ImageView playIcon;

	private Button scanManual;
	private Button scanAuto;
	private Button scanStop;
	private ProgressBar progressBar;
	private TextView tvPercent;
	private TextView tvFrequency;
	private TextView tvTime;
	private TextView tvMsgPromt;

	private TextView tvPName;
	private TextView tvPStartTime;
	private TextView tvPDuration;

	private TextView tvFname;
	private TextView tvFStartTime;
	private TextView tvFDurateion;

	private ServiceAdapter serviceAdapter;
	Thread patScan;
	Thread pmtScan;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;

	public int symbolRate = 6875000;
	public int defaultFrequency = 443000;
	int frequency = 0;

	private static final String TAG = "ScanActivity";

	ArrayList<NITInfo> nitList = new ArrayList<NITInfo>();
	ArrayList<Integer> patList = new ArrayList<Integer>();
	ArrayList<SDTInfo> sdtList = new ArrayList<SDTInfo>();
	ArrayList<EITInfo> eitFollowList = new ArrayList<EITInfo>();
	ArrayList<EITInfo> eitScheduleList = new ArrayList<EITInfo>();
	ArrayList<ServerInfo> serverList = new ArrayList<ServerInfo>();

	private DtvManager dtvManager;
	private DtvTuner dtvCATunner;
	private DtvDemux dtvDemux;
	private DtvDemux dtvDemuxCA;
	private DvbPlayer dvbPlayerCA;
	private DtvDescrambler mDescrambler;

	private int lastPostion = -1;
	private boolean play = true;

	private DbUtils dbUtils;

	private CdcasManager mCdcasManager = null;

	int emmPid = -1;
	int frequencyRecord = 0;

	boolean stop = false;
	SharedPreferences share;

	private Spinner spinner;
	private ListView ecgInfoListView;
	private ProgressBar epgProgressBar;

	BroadcastReceiver scanReceiver = new BroadcastReceiver() {

		public void onReceive(android.content.Context context, Intent intent) {
			String action = intent.getAction();
			switch (action) {
			case Constant.ACTION_REFRESH_LIST:
				sdtList.clear();
				sdtList.addAll(dbUtils.getAllSDTInfo());
				serviceAdapter.notifyDataSetChanged();
				break;

			case Constant.ACTION_PROGRESS:

				int increase = intent.getIntExtra(Constant.EXTRA_PROGRESS, 0);
				progressBar.incrementProgressBy(increase);
				int percent = progressBar.getProgress() * 100
						/ Constant.PROGRESS_MAX_AUTO;
				String strPercent = String.format(
						getString(R.string.tvPercent), percent + "");
				tvPercent.setText(strPercent + "%");
				break;

			case Constant.ACTION_FREQUENCY_CHANGE:
				int frequency = intent.getIntExtra(Constant.EXTRA_FREQUENCY, 0);
				tvFrequency.setText(String.format(
						getString(R.string.tvFrequency), frequency));
				break;

			case Constant.ACTION_SCAN_FINISH:
				scanDialog.dismiss();

				break;

			case Constant.ACTION_SCAN_FREQUENCY_FAILED:
				scanDialog.dismiss();
				if (timerTask != null) {
					timerTask.cancel();
				}
				timer.cancel();
				Toast.makeText(ScanActivity.this, R.string.scanNoFrequency,
						Toast.LENGTH_SHORT).show();
				break;

			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);

		DtvManager dtv = dtvManager.getInstance();
		DtvTuner tuner = dtv.getTuner(0);
		DtvDemux demux = dtv.getDemux(0);
		demux.linkTuner(tuner);
		demux.release();
		tuner.setParameter(new DtvTuner.DvbcPamameter(387000, 6875000,
				DtvTuner.QAM64, DtvTuner.SPECTRUM_AUTO));
		tuner.release();

		mCdcasManager = CdcasManager.getInstance();
		mCdcasManager.setListener(this);

		actionbar();
		initWork();
		setListener();
		getDataFromDB();
	}

	private SimpleAdapter spinnerAdapter;
	private EpgItemAdapter epgAdapter;

	private void initWork() {

		// /////////////start service and bind service///////////////
		Intent intent = new Intent(ScanActivity.this, ParseService.class);
		intent.putExtra(Constant.EXTRA_FREQUENCY, frequency);
		intent.putExtra(Constant.EXTRA_SYMBOLRATE, symbolRate);
		startService(intent);
		bindService(intent, conn, Context.BIND_AUTO_CREATE);

		// //////////////register broadcastReceiver/////////////
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.ACTION_REFRESH_LIST);
		filter.addAction(Constant.ACTION_PROGRESS);
		filter.addAction(Constant.ACTION_FREQUENCY_CHANGE);
		filter.addAction(Constant.ACTION_SCAN_FINISH);
		filter.addAction(Constant.ACTION_SCAN_FREQUENCY_FAILED);
		registerReceiver(scanReceiver, filter);

		createFolder();
		dbUtils = new DbUtils(this);
		tvMsgPromt = (TextView) findViewById(R.id.msgPromt);
		surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);

		listView = (ListView) findViewById(R.id.listView);
		serviceAdapter = new ServiceAdapter(this, sdtList);
		listView.setAdapter(serviceAdapter);

		playIcon = (ImageView) findViewById(R.id.imageView);

		spinnerAdapter = new SimpleAdapter(this, dateList,
				android.R.layout.simple_expandable_list_item_1,
				new String[] { EITTable.DATE },
				new int[] { android.R.id.text1 });

		spinner = (Spinner) findViewById(R.id.spinner);
		spinner.setAdapter(spinnerAdapter);

		epgAdapter = new EpgItemAdapter(this, eitList);
		ecgInfoListView = (ListView) findViewById(R.id.ecgInfoList);
		ecgInfoListView.setAdapter(epgAdapter);

		// /////find p/f view;
		tvPDuration = (TextView) findViewById(R.id.pEndtime);
		tvPName = (TextView) findViewById(R.id.pserverName);
		tvPStartTime = (TextView) findViewById(R.id.ptime);

		tvFDurateion = (TextView) findViewById(R.id.fEndtime);
		tvFname = (TextView) findViewById(R.id.fserverName);
		tvFStartTime = (TextView) findViewById(R.id.ftime);

		epgProgressBar = (ProgressBar) findViewById(R.id.epgProgress);

	}

	private void setListener() {
		listView.setOnItemClickListener(this);
		spinner.setOnItemSelectedListener(this);
	}

	private void getDataFromDB() {

		// 节目单列表
		sdtList.clear();
		sdtList.addAll(dbUtils.getAllSDTInfo());
		serviceAdapter.notifyDataSetChanged();
		if (sdtList.size() > 0) {
			frequencyRecord = sdtList.get(0).getFrequency();
		}

		if (sdtList.size() > 0) {
			// ecg的日期列表
			int firstSdtServerId = sdtList.get(0).getServerId();
			dateList.clear();
			dateList.addAll(dbUtils.getEITDate(firstSdtServerId));
			spinnerAdapter.notifyDataSetChanged();
			if (dateList.size() > 0) {
				// ecg的节目列表
				eitList.clear();
				eitList.addAll(dbUtils.getEITScheduleInfo(
						dateList.get(0).get(EITTable.DATE), firstSdtServerId));
				Log.e(TAG, eitList.toString());
				epgAdapter.notifyDataSetChanged();
			}
		}

	}

	long startCount = 0;
	TimerTask timerTask;
	Timer timer;

	private void delAllDBInfo() {
		dbUtils.delAllServerInfo();
		dbUtils.delAllFrequencyInfo();
		dbUtils.delAllCAInfo();
		dbUtils.delAllEITInfo();

		sdtList.clear();
		serverList.clear();
		nitList.clear();
		serviceAdapter.notifyDataSetChanged();

		dateList.clear();
		spinnerAdapter.notifyDataSetChanged();

		eitList.clear();
		epgAdapter.notifyDataSetChanged();

		setPresentEITInfo(null);
		setFollowingEITInfo(null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.manual:
			// countTime();
			// startCount = 0;

			setWidgetClickable(false);
			frequency = Integer.valueOf(etFrequency.getText().toString());
			Intent intentManual = new Intent(Constant.ACTION_MANUEL_SCAN);
			intentManual.putExtra(Constant.EXTRA_FREQUENCY, frequency);
			intentManual.putExtra(Constant.EXTRA_SYMBOLRATE, symbolRate);

			sendBroadcast(intentManual);
			delAllDBInfo();

			progressContainer.setVisibility(View.VISIBLE);

			break;
		case R.id.auto:
			// countTime();
			// startCount = 0;

			setWidgetClickable(false);
			count = 0;
			String input = etFrequency.getText().toString();
			frequency = TextUtils.isEmpty(input) ? defaultFrequency : Integer
					.valueOf(input);

			// send broadcast,start to auto-scan
			Intent intentAuto = new Intent(Constant.ACTION_AUTO_SCAN);
			intentAuto.putExtra(Constant.EXTRA_FREQUENCY, frequency);
			intentAuto.putExtra(Constant.EXTRA_SYMBOLRATE, symbolRate);
			sendBroadcast(intentAuto);

			// send broadcast,show progress
			Intent intentProgress = new Intent(Constant.ACTION_PROGRESS);
			intentProgress.putExtra(Constant.EXTRA_PROGRESS,
					Constant.AUTO_ADD / 2);
			sendBroadcast(intentProgress);
			progressContainer.setVisibility(View.VISIBLE);
			progressBar.setMax(Constant.PROGRESS_MAX_AUTO);

			// clear database
			delAllDBInfo();

			break;
		case R.id.stop:
			stop = true;
			scanDialog.dismiss();
			setWidgetClickable(true);
			sendBroadcast(new Intent(Constant.ACTION_STOP_SCAN));
			break;

		}

	}

	int clickPostion = -1;

	boolean init = false;

	private void DescramblerCADvbPlayer(int emmpid, int frequency,
			int symbolRate) {
		Log.e(">>>>>>>>>>>>>>>", "initCADvbPlayer");
		init = true;
		dtvCATunner.setParameter(new DtvTuner.DvbcPamameter(frequency,
				symbolRate, DtvTuner.QAM64, DtvTuner.SPECTRUM_AUTO));
		dtvDemuxCA = dtvManager.getDemux(0);
		dtvDemuxCA.linkTuner(dtvCATunner);
		dvbPlayerCA = dtvManager.createDvbPlayer(dtvDemuxCA);

		mDescrambler = dtvDemuxCA.createDescrambler(DtvDescrambler.CA_NORMAL,
				DtvDescrambler.CSA2, DtvDescrambler.ENTROPY_REDUCTION_CLOSE);
		mCdcasManager.setEmmPid(emmpid);
	}

	private void associateCAPlayer(int serviceId, int ecmpid, int videoPid,
			int audioPid) {
		hasDisconnect = false;
		dvbPlayerCA.start();
		mDescrambler.associatePid(dtvDemuxCA, videoPid);
		mDescrambler.associatePid(dtvDemuxCA, audioPid);
		mCdcasManager.startService(ecmpid, serviceId);
	}

	private void disassociateCAPlayer(int videoPid, int audioPid, int stopMode) {

		hasDisconnect = true;
		mDescrambler.disassociatePid(dtvDemuxCA, videoPid);
		mDescrambler.disassociatePid(dtvDemuxCA, audioPid);
		mCdcasManager.stopService();
		dvbPlayerCA.stop(stopMode);
	}

	private void setVideoWindowSize() {
		int[] location = new int[2];
		surfaceView.getLocationOnScreen(location);
		if (dvbPlayerCA != null) {
			dvbPlayerCA.setVideoWindow(false, location[0], location[1],
					surfaceView.getWidth(), surfaceView.getHeight());

		}

	}

	private void releaseDvbSource() {
		if (dvbPlayerCA != null) {
			dvbPlayerCA.stop(DvbPlayer.STOP_MODE_BLACK);
			dvbPlayerCA.release();
			dvbPlayerCA = null;
		}

		if (dtvDemuxCA != null) {
			dtvDemuxCA.release();
			dtvDemuxCA = null;
		}

		if (mDescrambler != null) {
			mDescrambler.release();
			mDescrambler = null;
		}
		System.gc();
	}

	int basicNum = 1;
	int count = 0;

	PMTInfo selectPMT = null;
	SDTInfo selectSDT = null;
	ArrayList<EITInfo> eitList = new ArrayList<>();
	int selectServerId;

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		Log.e(TAG, "position:" + position);
		selectSDT = sdtList.get(position);
		selectServerId = selectSDT.getServerId();
		selectPMT = dbUtils.getPMTInfo(selectServerId);
		if (selectPMT != null) {
			playCA(selectPMT, position);

		} else {
			Log.e("error", "NO SERVICE");
		}
	}

	int lastVideoPid = -1;
	int lastAudeoPid = -1;
	int lastFrequency = 0;
	boolean itemFirstClick = true;
	boolean lastCanPlay = true;
	boolean getSchedule = true;

	private void getEIT(SDTInfo selectSDT, int position) {

		int serverId = selectSDT.getServerId();

		int frequency = selectPMT.getFrequency();
		int symbolRate = selectPMT.getSymbolRate();
		dateList.clear();
		spinnerAdapter.notifyDataSetChanged();
		eitList.clear();
		epgAdapter.notifyDataSetChanged();
		setFollowingEITInfo(null);
		setPresentEITInfo(null);

		if (frequency == 0 || symbolRate == 0) {
			Toast.makeText(ScanActivity.this, R.string.eitFailed,
					Toast.LENGTH_SHORT).show();
			return;

		}

		// epg日期列表
		dateList.clear();
		dateList.addAll(dbUtils.getEITDate(serverId));
		spinnerAdapter.notifyDataSetChanged();

		if (dateList.size() == 0) {
			getSchedule = true;
			epgProgressBar.setVisibility(View.VISIBLE);
		} else {
			getSchedule = false;
			// epg节目单列表
			eitList.clear();
			eitList.addAll(dbUtils.getEITScheduleInfo(
					dateList.get(0).get(EITTable.DATE), serverId));
			epgAdapter.notifyDataSetChanged();
		}

		EITScannerThread eitThread = new EITScannerThread(this, selectSDT,
				handler, dtvCATunner, dtvManager, getSchedule);
		eitThread.setOnEITThreadFinishedListener(this);
		eitThread.start();

	}

	boolean hasDisconnect = false;

	private void playCA(PMTInfo selectPMT, int position) {
		Log.e(TAG, selectPMT.toString());
		int video_pid = selectPMT.getVideoPid();
		int video_decoder = selectPMT.getVideoType();
		int audio_pid = selectPMT.getAudioPid();
		int audio_decoder = selectPMT.getAudioType();
		int frequency = selectPMT.getFrequency();
		int symbolRate = selectPMT.getSymbolRate();
		int serviceId = selectPMT.getServerId();
		int pcrPid = selectPMT.getPcrPid();
		int ecmPid = selectPMT.getEcmPid();
		Log.e("ecmPid", String.format("%02x", ecmPid));
		lastCanPlay = true;

		if (lastPostion != position) {
			itemFirstClick = true;
			epgProgressBar.setVisibility(View.GONE);
			getEIT(selectSDT, position);
		} else {
			itemFirstClick = false;
		}

		/*
		 * 如果遇到不能播放的视频
		 */
		if (video_decoder == 0 && audio_decoder == 0 || frequency == 0
				|| symbolRate == 0) {
			lastCanPlay = false;
			lastPostion = position;
			Toast.makeText(ScanActivity.this, R.string.connectFailed,
					Toast.LENGTH_SHORT).show();
			if (dvbPlayerCA != null) {
				dvbPlayerCA.stop(DvbPlayer.STOP_MODE_BLACK);
			}
			return;
		}
		if (itemFirstClick) {

			/*
			 * 移动位置 
			 * 1、关闭上一次播放的视频
			 * 2、当视频的频率发生变化时，释放掉上一次的DvbCAPlayer,重新初始化DvbCAPlayer 
			 * 3、设置需要播放视频的参数
			 * 4、开始播放 
			 * 5、设置播放的标志位
			 */
			// 1
			if (lastVideoPid != -1 && lastAudeoPid != -1 && lastCanPlay) {
				disassociateCAPlayer(lastVideoPid, lastAudeoPid,
						DvbPlayer.STOP_MODE_BLACK);
			}

			// 2
			if (lastFrequency != frequency) {
				// 当节目的频率发生变化时，重新初始化CAPlayer
				int emmpid = dbUtils.getEmmPid(frequency);
				releaseDvbSource();
				Log.e(TAG, "重新初始化播放器，重新获取  emmpid:" + emmpid);
				DescramblerCADvbPlayer(emmpid, frequency, symbolRate);
			}

			// 3
			dvbPlayerCA.setVideoParameter(video_pid, video_decoder);
			dvbPlayerCA.setAudioParameter(audio_pid, audio_decoder);
			dvbPlayerCA.setPcrPid(pcrPid);
			setVideoWindowSize();

			Log.e("<<<<<<<<<<<<<<", "dvbPlayerCA.setPcrPid");

			// 4
			associateCAPlayer(serviceId, ecmPid, video_pid, audio_pid);

			// 5
			play = false;
		} else {
			/*
			 * 在原位置播放暂停的操作
			 */
			if (play) {
				play = false;
				dvbPlayerCA.start();
			} else {
				play = true;
				dvbPlayerCA.stop(DvbPlayer.STOP_MODE_STILL);
			}
		}
		lastVideoPid = video_pid;
		lastAudeoPid = audio_pid;
		lastFrequency = frequency;
		lastPostion = position;
		lastCanPlay = true;
		setIcon(play);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(scanReceiver);
		unbindService(conn);
		releaseDvbSource();
		mCdcasManager.setListener(null);
		if (lastVideoPid != -1 && lastAudeoPid != -1 && lastCanPlay
				&& !hasDisconnect) {
			disassociateCAPlayer(lastVideoPid, lastAudeoPid, DvbPlayer.STOP_MODE_BLACK);
		}
		super.onDestroy();
	}

	ParseService parseService;

	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			LocalBinder binder = (LocalBinder) service;
			parseService = binder.getService();
			dtvCATunner = parseService.getTunner();
			dtvManager = parseService.getDtvManager();

		}
	};

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		releaseDvbSource();
	}

	private void setPresentEITInfo(EITInfo eitInfo) {
		if (eitInfo != null) {
			tvPDuration.setText(String.format(getString(R.string.playEndtime),
					eitInfo.getEndTime()));
			tvPName.setText(eitInfo.getName());
			tvPStartTime.setText(String.format(getString(R.string.playTime),
					eitInfo.getStartTime()));
		} else {
			tvPDuration.setText(Constant.NULL);
			tvPName.setText(Constant.NULL);
			tvPStartTime.setText(Constant.NULL);
		}

	}

	private void setFollowingEITInfo(EITInfo eitInfo) {
		if (eitInfo != null) {
			tvFDurateion.setText(String.format(getString(R.string.playEndtime),
					eitInfo.getEndTime()));
			tvFname.setText(eitInfo.getName());
			tvFStartTime.setText(String.format(getString(R.string.playTime),
					eitInfo.getStartTime()));
		} else {
			tvFDurateion.setText(Constant.NULL);
			tvFname.setText(Constant.NULL);
			tvFStartTime.setText(Constant.NULL);
		}
	}

	ArrayList<HashMap<String, String>> dateList = new ArrayList<>();

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Constant.MSG_EIT_FOLLOW:
				eitFollowList.clear();
				eitFollowList.addAll((ArrayList<EITInfo>) msg.obj);
				if (eitFollowList.size() == 0) {
					Toast.makeText(ScanActivity.this, R.string.noEventInfo,
							Toast.LENGTH_SHORT).show();
				}
				for (int i = 0; i < eitFollowList.size(); i++) {
					EITInfo eitInf = eitFollowList.get(i);
					if (eitInf.getSectionNum() == 0) {
						setPresentEITInfo(eitInf);
					} else {
						setFollowingEITInfo(eitInf);
					}
				}
				break;

			case Constant.MSG_NO_EIT_SCHEDULE:
				Toast.makeText(ScanActivity.this, R.string.noEventInfo,
						Toast.LENGTH_SHORT).show();
				if (progressBar != null
						&& progressBar.getVisibility() == View.VISIBLE) {
					progressBar.setVisibility(View.GONE);
				}
				break;

			case Constant.MSG_EIT_SCHEDULE:

				// epg日期列表
				epgProgressBar.setVisibility(View.GONE);
				dateList.clear();
				dateList.addAll(dbUtils.getEITDate(selectServerId));
				spinnerAdapter.notifyDataSetChanged();

				if (dateList.size() < 7) {
					getSchedule = true;
				} else {
					getSchedule = false;
					// epg节目单列表
				}
				if (dateList.size() > 0) {
					eitList.clear();
					eitList.addAll(dbUtils.getEITScheduleInfo(dateList.get(0)
							.get(EITTable.DATE), selectServerId));
					epgAdapter.notifyDataSetChanged();
				}
				break;

			case Constant.MSG_TIMER:
				tvTime.setText((String) msg.obj);
				break;

			default:
				break;
			}

		};
	};

	@Override
	public void ScrSetCW(int ecmPid, byte[] oddKey, byte[] evenKey) {

		Log.e("ScrSetCW", "ScrSetCW");
		// TODO Auto-generated method stub
		final byte[] ok = oddKey;
		final byte[] ek = evenKey;

		handler.post(new Runnable() {
			@Override
			public void run() {
				if (mDescrambler != null) {
					mDescrambler.setOddKey(ok);
					mDescrambler.setEvenKey(ek);
				}
			}
		});

	}

	@Override
	public void ShowBuyMessage(final String message, int messageType) {
		Log.e("ShowBuyMessage", message + "," + messageType);
		tvMsgPromt.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				tvMsgPromt.setVisibility(View.VISIBLE);
				tvMsgPromt.setText(message);
				tvMsgPromt.postDelayed(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						tvMsgPromt.setVisibility(View.INVISIBLE);
					}
				}, 5000);
			}
		});

	}

	private void createFolder() {
		String sdDirectory = Environment.getExternalStorageDirectory()
				.getPath();
		File cdcas = new File(sdDirectory, "cdcas");

		if (!cdcas.exists()) {
			cdcas.mkdirs();
		}

	}

	@SuppressLint("NewApi")
	private void actionbar() {
		android.app.ActionBar actionBar = getActionBar();
		actionBar.hide();
		new ActionBarSetting(this, new OnClickListener() {

			@Override
			public void onClick(View v) {
				ActionBarSetting.sureQuit(ScanActivity.this);
			}
		}, new OnClickListener() {

			@Override
			public void onClick(View v) {
				scanDialog();

			}
		});
	}

	View scanDialogView;
	AlertDialog scanDialog;
	RelativeLayout progressContainer;

	private void scanDialog() {
		AlertDialog.Builder abScan = new AlertDialog.Builder(ScanActivity.this);
		abScan.setCancelable(false);
		scanDialogView = LayoutInflater.from(this).inflate(
				R.layout.scan_dialog, null);
		abScan.setTitle(R.string.search);
		abScan.setIcon(R.drawable.smile);
		abScan.setView(scanDialogView);
		etFrequency = (EditText) scanDialogView.findViewById(R.id.input);
		scanManual = (Button) scanDialogView.findViewById(R.id.manual);
		scanStop = (Button) scanDialogView.findViewById(R.id.stop);
		scanAuto = (Button) scanDialogView.findViewById(R.id.auto);
		progressBar = (ProgressBar) scanDialogView
				.findViewById(R.id.progressBar);
		progressContainer = (RelativeLayout) scanDialogView
				.findViewById(R.id.progressContainer);

		tvPercent = (TextView) scanDialogView.findViewById(R.id.percent);
		tvFrequency = (TextView) scanDialogView.findViewById(R.id.frequency);
		tvTime = (TextView) scanDialogView.findViewById(R.id.time);

		scanAuto.setOnClickListener(this);
		scanManual.setOnClickListener(this);
		scanStop.setOnClickListener(this);
		scanDialog = abScan.create();
		scanDialog.show();
	}

	private void setWidgetClickable(boolean clickable) {
		scanManual.setEnabled(clickable);
		scanAuto.setEnabled(clickable);
		// listView.setClickable(clickable);
		scanAuto.setClickable(clickable);
		if (!clickable) {
			Log.e("tag", "not clickable");
		}
	}

	private void setIcon(boolean play) {
		if (play) {
			playIcon.setBackgroundResource(R.drawable.play);
		} else {
			playIcon.setBackgroundResource(R.drawable.pause);
		}

	}

	@Override
	public void onInsertSmartCard() {

		Log.e(TAG, "insertSmartCard");
	}

	@Override
	public void onRemoveSmartCard() {
		Log.e(TAG, "onRemoveSmartCard");

	}

	@Override
	public void OnEITGetFollow(ArrayList<EITInfo> eitList) {
		// TODO Auto-generated method stub
		handler.obtainMessage(Constant.MSG_EIT_FOLLOW, eitList).sendToTarget();

	}

	@Override
	public void OnEITGetSchedule(ArrayList<EITInfo> eitList, boolean finished) {
		// TODO Auto-generated method stub
		handler.obtainMessage(Constant.MSG_EIT_SCHEDULE, eitList)
				.sendToTarget();
		if (finished && eitList.size() == 0) {
			handler.obtainMessage(Constant.MSG_NO_EIT_SCHEDULE).sendToTarget();
		}

	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		String date = dateList.get(arg2).get(EITTable.DATE);
		eitList.clear();
		eitList.addAll(dbUtils.getEITScheduleInfo(date, selectServerId));
		epgAdapter.notifyDataSetChanged();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         