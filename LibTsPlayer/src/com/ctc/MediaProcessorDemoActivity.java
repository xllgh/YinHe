package com.ctc;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
//import android.os.SystemProperties;

public class MediaProcessorDemoActivity extends Activity {
	private String TAG="MediaProcessorDemoActivity";
	public String result_s = "success";
	public int result_i = 0;
	public boolean result_b = false;
	private Surface mySurface = null;
	private SurfaceHolder myHolder = null;
	private SurfaceView mySurfaceView = null;
	private PropertieList propList = new PropertieList();
	private String url = null; 
	private int playBufferSize = 32;
	private Button pause = null;
	private Button resume = null; 
	private Button seek = null;
	private Button videoshow = null;
	private Button videohide = null;
	private Button fast = null;
	private Button stopfast = null;
	private Button stop = null;
	private Button getVolume = null;
	private Button setVolume = null;
	private Button setRatio = null;
	private Button getAudioBalance = null;
	private Button setAudioBalance = null;
	private Button getVideoPixels = null;
	private Button isSoftFit = null;
	private Button getPlayMode = null;
	private Button setEPGSize = null;
	private Button switchSubtitle = null;
	private TextView Function = null;
	private TextView Return_t = null;
	private TextView Result = null;
	private TextView resultView = null;
	
	private int flag = 0;
	private int PLAYER_INIT = 0;
	private int PLAYER_PALY = 1;
	private int PLAYER_STOP = 2;
	private int player_status = 0;
	private Handler MainHandler = null;

	private String getUrl() {
		return propList.getPlayUrl();
	}
	
	private int getBufferSize() {
		return propList.getPlayBufferSize(32);
	}
	
	class drawSurface implements Runnable{
		public String url;
		public void run() {
			nativeWriteData(this.url, playBufferSize);
		} 
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent msg) {
		Log.d(TAG, "onKeyDown()"); 
		if(keyCode == KeyEvent.KEYCODE_POWER){
			return true;
		}
		return super.onKeyDown(keyCode, msg);
	}

	@Override
	public void onPause(){
		super.onPause(); 
		Log.i(TAG, "onPause");

		player_status = PLAYER_STOP;
		Log.d(TAG, "before nativeStop, time is: " + System.currentTimeMillis());

		nativeStop();  
		Log.d(TAG, "before nativeSetEPGSize, time is: " + System.currentTimeMillis());

		nativeSetEPGSize(1280, 720);
		Log.d(TAG, "after nativeSetEPGSize, time is: " + System.currentTimeMillis());

		Date a = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Log.i(TAG, "release start" + sdf.format(a));  
		if(flag != 1) {
			nativeDelete();
			flag = 0;
		}

		Date b = new Date();
		Log.i(TAG, "release end" + sdf.format(b));
	}

	@Override
	public void onStop(){
		super.onStop();
		Log.i(TAG, "onStop");
	}

	private IntentFilter mFilter = null;

	@Override
	public void onResume() {
		if (nativeInit(url) == 0) {
			Log.i(TAG, "Init: success");
		} else {
			Log.i(TAG, "Init: error");
		}
		Log.d(TAG, "onResume()");
            
		Log.i(TAG, "create surface: next");  
		if (nativeCreateSurface(mySurface, 1280, 720) == 0) 
			Log.i(TAG, "create surface: success");  

		nativeSetEPGSize(1280, 720);
		nativeSetVideoWindow(420, 40, 640, 480);
		nativeStartPlay();
		Log.i(TAG, "play success");

		drawSurface playData = new drawSurface();  
		playData.url = url;
		Thread player = new Thread(playData);
		player.start();

		MainHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Log.d(TAG, "get msg "+msg.what);
				super.handleMessage(msg);
			}
		};

		player_status = PLAYER_PALY;
		super.onResume();
	}

	/** Called when the activity is first created. */ 
	@Override 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main); 

		url = getUrl(); 
		playBufferSize = getBufferSize();

		mFilter = new IntentFilter();
		mFilter.addAction(Intent.ACTION_SCREEN_OFF);
		mFilter.addAction(Intent.ACTION_SCREEN_ON);
		mFilter.addAction("com.android.smart.terminal.iptv.power");
		mFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);

		Log.i(TAG, "onCreate");
		mySurfaceView = (SurfaceView)this.findViewById(R.id.SurfaceView01);
		myHolder = mySurfaceView.getHolder();
		myHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mySurface = myHolder.getSurface(); 

		Function = (TextView)findViewById(R.id.Function);
		Return_t = (TextView)findViewById(R.id.Return_t);
		Result = (TextView)findViewById(R.id.Result);

		//pause
		pause = (Button)findViewById(R.id.pause);
		pause.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				result_b = nativePause();
				Function.setText("Pause");
				if (result_b == true) {
					Return_t.setText("true");
					Result.setTextColor(Color.BLUE);
					Result.setText("success");
					nativeWriteFile("function:Pause", "return:true", "result:success");
				} else {
					Return_t.setText("false"); 
					Result.setTextColor(Color.RED);
					Result.setText("error");
					nativeWriteFile("function:Pause", "return:false", "result:error");
				}
			}
		});

		//resume
		resume = (Button)findViewById(R.id.resume);
		resume.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				result_b = nativeResume();
				Function.setText("Resume");
				if (result_b == true) {
					Return_t.setText("true");
					Result.setTextColor(Color.BLUE);
					Result.setText("success");
					nativeWriteFile("function:Resume", "return:true", "result:success");
				} else {
					Return_t.setText("false");
					Result.setTextColor(Color.RED);
					Result.setText("error");
					nativeWriteFile("function:Resume", "return:false", "result:error");
				}
			}
		});

		//seek
		seek = (Button)findViewById(R.id.seek);
		seek.setOnClickListener(new Button.OnClickListener(){ 
			public void onClick(View v) {
				result_b = nativeSeek();
				Function.setText("Seek");  
				if (result_b == true) {
					Return_t.setText("true");
					Result.setTextColor(Color.BLUE);
					Result.setText("success");
					nativeWriteFile("function:Seek", "return:true", "result:success"); 
				} else {
					Return_t.setText("false");
					Result.setTextColor(Color.RED);
					Result.setText("error");
					nativeWriteFile("function:Seek", "return:false", "result:error"); 
				}
			}
		});

		//fast
		fast = (Button)findViewById(R.id.fast);
		fast.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				result_b = nativeFast(); 
				Function.setText("Fast");
				if (result_b == true) {
					Return_t.setText("true");
					Result.setTextColor(Color.BLUE);
					Result.setText("success");
					nativeWriteFile("function:Fast", "return:true", "result:success"); 
				} else {
					Return_t.setText("false");
					Result.setTextColor(Color.RED);
					Result.setText("error");
					nativeWriteFile("function:Fast", "return:false", "result:error");
				}
			}
		});

		//stopfast
		stopfast = (Button)findViewById(R.id.stopfast);
		stopfast.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				result_b = nativeStopFast();
				Function.setText("StopFast");
				if (result_b == true) {
					result_s = String.valueOf(result_b);
					Return_t.setText(result_s);
					Result.setTextColor(Color.BLUE);
					Result.setText("success");
					nativeWriteFile("function:StopFast", "return:true", "result:success"); 
				} else {
					Return_t.setText("false");
					Result.setTextColor(Color.RED);
					Result.setText("error");
					nativeWriteFile("function:StopFast", "return:false", "result:error"); 
				}
			}
		});

		//videoshow
		videoshow = (Button)findViewById(R.id.videoshow);
		videoshow.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				result_i = nativeVideoShow();
				Function.setText("VideoShow");
				if (result_i == 0) {
					result_s = String.valueOf(result_i);
					Return_t.setText(result_s);
					Result.setTextColor(Color.BLUE);
					Result.setText("success");
					nativeWriteFile("function:VideoShow", "return:"+result_s, "result:success");
				} else {
					result_s = String.valueOf(result_i);
					Return_t.setText(result_s);
					Result.setTextColor(Color.RED);
					Result.setText("error");
					nativeWriteFile("function:VideoShow", "return:"+result_s, "result:error");
				}
			} 
		});

		//videohide 
		videohide = (Button)findViewById(R.id.videohide);
		videohide.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				result_i = nativeVideoHide();
				Function.setText("VideoHide");
				if (result_i == 0) {
					result_s = String.valueOf(result_i);
					Return_t.setText(result_s);
					Result.setTextColor(Color.BLUE);
					Result.setText("success");
					nativeWriteFile("function:VideoHide", "return:"+result_s, "result:success");
				} else {
					result_s = String.valueOf(result_i);
					Return_t.setText(result_s);
					Result.setTextColor(Color.RED);
					Result.setText("error");
					nativeWriteFile("function:VideoHide", "return:"+result_s, "result:error");
				}
			}
		});

		//stop
		stop = (Button)findViewById(R.id.stop);
		stop.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				result_b = nativeStop();
				Function.setText("Stop");
				if (result_b == true) {
					result_s = String.valueOf(result_b);
					Return_t.setText(result_s);
					Result.setTextColor(Color.BLUE);
					Result.setText("success");
					nativeWriteFile("function:Stop", "return:true", "result:success");
				} else {
					Return_t.setText("false");
					Result.setTextColor(Color.RED);
					Result.setText("error");
					nativeWriteFile("function:Stop", "return:false", "result:error");
				}
			}
		}); 

		//getVolume
		getVolume = (Button)findViewById(R.id.getVolume);
		getVolume.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				result_i = nativeGetVolume();
				Function.setText("GetVolume");
				result_s = String.valueOf(result_i);
				Return_t.setText(result_s);
				Result.setTextColor(Color.BLUE);
				Result.setText("success");
				nativeWriteFile("function:GetVolume", "return:"+result_s, "result:success");
			}
		}); 

		//setVolume
		setVolume = (Button)findViewById(R.id.setVolume);
		setVolume.setOnClickListener(new Button.OnClickListener() { 
			public void onClick(View v) {
				result_b = nativeSetVolume(60);
				Function.setText("SetVolume");
				if (result_b == true) {
					result_s = String.valueOf(result_b);
					Return_t.setText(result_s);
					Result.setTextColor(Color.BLUE);
					Result.setText("success");
					nativeWriteFile("function:SetVolume", "return:true", "result:success");
				} else {
					Return_t.setText("false");
					Result.setTextColor(Color.RED);
					Result.setText("error");
					nativeWriteFile("function:SetVolume", "return:false", "result:error");
				}
			}
		});

		//setRatio
		setRatio = (Button)findViewById(R.id.setRatio);
		setRatio.setOnClickListener(new Button.OnClickListener() { 
			public void onClick(View v) {
				result_b = nativeSetRatio(1);
				Function.setText("SetRatio");
				if (result_b == true) {
					result_s = String.valueOf(result_b);
					Return_t.setText(result_s);
					Result.setTextColor(Color.BLUE);
					Result.setText("success");
					nativeWriteFile("function:SetRatio", "return:true", "result:success");
				} else {
					Return_t.setText("false");
					Result.setTextColor(Color.RED);
					Result.setText("error");
					nativeWriteFile("function:SetRatio", "return:false", "result:error");
				}
			}
		});

		//getAudioBalance
		getAudioBalance = (Button)findViewById(R.id.getAudioBalance);
		getAudioBalance.setOnClickListener(new Button.OnClickListener(){ 
			public void onClick(View v) {
				result_i = nativeGetAudioBalance();
				Function.setText("GetAudioBalance");
				result_s = String.valueOf(result_i);
				Return_t.setText(result_s);
				Result.setTextColor(Color.BLUE);
				Result.setText("success");
				nativeWriteFile("function:GetAudioBalance", "return:"+result_s, "result:success");
			}
		}); 

		//setAudioBalance
		setAudioBalance = (Button)findViewById(R.id.setAudioBalance);
		setAudioBalance.setOnClickListener(new Button.OnClickListener(){ 
			public void onClick(View v) {
				result_i = nativeGetAudioBalance();
				result_b = nativeSetAudioBalance(result_i>1?(result_i-1):3);
				Function.setText("SetAudioBalance");
				if (result_b == true) {
					result_s = String.valueOf(result_b);
					Return_t.setText(result_s);
					Result.setTextColor(Color.BLUE);
					Result.setText("success");
					nativeWriteFile("function:SetAudioBalance", "return:"+result_s, "result:success");
				} else { 
					Return_t.setText("false");
					Result.setTextColor(Color.RED);
					Result.setText("error");
					nativeWriteFile("function:GetAudioBalance", "return:false", "result:error");
				}
			}
		}); 

		//getVideoPixels
		getVideoPixels = (Button)findViewById(R.id.getVideoPixels);
		getVideoPixels.setOnClickListener(new Button.OnClickListener(){ 
			public void onClick(View v) {
				nativeGetVideoPixels();
				Function.setText("GetVideoPixels");
				Return_t.setText("void");
				Result.setTextColor(Color.BLUE);
				Result.setText("success");
				nativeWriteFile("function:GetVideoPixels", "return:void", "result:success");
			}
		}); 

		//isSoftFit
		isSoftFit = (Button)findViewById(R.id.isSoftFit);
		isSoftFit.setOnClickListener(new Button.OnClickListener(){  
			public void onClick(View v) {
				result_b = nativeIsSoftFit();
				Function.setText("IsSoftFit");
				if (result_b == true) {
					result_s = String.valueOf(result_b);
					Return_t.setText(result_s);
					Result.setTextColor(Color.BLUE);
					Result.setText("soft fit");
					nativeWriteFile("function:IsSoftFit", "return:true", "result:soft fit");
				} else {
					Return_t.setText("false");
					Result.setTextColor(Color.RED);
					Result.setText("hardware fit");
					nativeWriteFile("function:IsSoftFit", "return:false", "result:hardware fit");
				}
			}
		}); 

		//getPlayMode
		getPlayMode = (Button)findViewById(R.id.getPlayMode);
		getPlayMode.setOnClickListener(new Button.OnClickListener(){ 
			public void onClick(View v) {
				result_i = nativeGetPlayMode();
				Function.setText("GetPlayMode");
				result_s = String.valueOf(result_i);
				Return_t.setText(result_s);
				Result.setTextColor(Color.BLUE);
				Result.setText("success");
				nativeWriteFile("function:GetPlayMode", "return:"+result_s, "result:success");
			}
		}); 

		//setEPGSize
		setEPGSize = (Button)findViewById(R.id.setEPGSize);
		setEPGSize.setOnClickListener(new Button.OnClickListener(){ 
			public void onClick(View v) {
				nativeSetEPGSize(640, 530);
				Function.setText("SetEPGSize");
				Return_t.setText("void");
				Result.setTextColor(Color.BLUE);
				Result.setText("success");
				nativeWriteFile("function:SetEPGSize", "return:void", "result:success"); 
			}
		});  
	}

	static {
		System.loadLibrary("CU_MediaProcessor");   
	}
	private TextView v;

	private static native int nativeCreateSurface(Surface mySurface, int width, int heigth);
	private static native int nativeInit(String url);
	private static native int nativeWriteData(String url, int bufsize);
	private static native int nativeSetVideoWindow(int x, int y, int width, int height);
	private static native boolean nativeStartPlay();
	private static native int nativeGetPlayMode();
	private static native boolean nativePause();
	private static native boolean nativeResume();  
	private static native boolean nativeSeek();
	private static native int nativeVideoShow();
	private static native int nativeVideoHide();
	private static native boolean nativeFast();
	private static native boolean nativeStopFast(); 
	private static native boolean nativeStop();
	private static native int nativeGetVolume();
	private static native boolean nativeSetVolume(int volume);
	private static native boolean nativeSetRatio(int nRatio);
	private static native int nativeGetAudioBalance();
	private static native boolean nativeSetAudioBalance(int nAudioBalance);
	private static native void nativeGetVideoPixels();
	private static native boolean nativeIsSoftFit();
	private static native boolean nativeDelete();
	private static native void nativeSetEPGSize(int w, int h);
	private static native void nativeWriteFile(String functionName, String returnValue, String resultValue);
	private static native int nativeGetCurrentPlayTime();
	private static native void nativeInitSubtitle();
	private static native void nativeSwitchSubtitle(int sub_pid);
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  