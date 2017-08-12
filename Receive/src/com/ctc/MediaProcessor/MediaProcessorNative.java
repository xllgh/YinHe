package com.ctc.MediaProcessor;

import android.view.Surface;

public class MediaProcessorNative {
	static {
		System.loadLibrary("RTP_MediaProcessor");
	}
	
	public static native int nativeCreateSurface(Surface mySurface, int width, int heigth);
	public static native int nativeInit();
	//com_ctc_demo1_MediaProcessorNative_nativeInit
	public static native int nativeWriteData(String url);
	public static native int nativeSetVideoWindow(int x, int y, int width, int height);
	public static native boolean nativeStartPlay();
	public static native int nativeGetPlayMode();
	public static native boolean nativePause();
	public static native boolean nativeResume();  
	public static native boolean nativeSeek();
	public static native int nativeVideoShow();
	public static native int nativeVideoHide();
	public static native boolean nativeFast();
	public static native boolean nativeStopFast(); 
	public static native boolean nativeStop();
	public static native int nativeGetVolume();
	public static native boolean nativeSetVolume(int volume);
	public static native boolean nativeSetRatio(int nRatio);
	public static native int nativeGetAudioBalance();
	public static native boolean nativeSetAudioBalance(int nAudioBalance);
	public static native void nativeGetVideoPixels();
	public static native boolean nativeIsSoftFit();
	public static native boolean nativeDelete();
	public static native void nativeSetEPGSize(int w, int h);
	public static native void nativeWriteFile(String functionName, String returnValue, String resultValue);
	public static native int nativeGetCurrentPlayTime();
	public static native void nativeInitSubtitle();
	public static native void nativeSwitchSubtitle(int sub_pid);
	public static native void nativeWriteRTPData(byte[] data,int size);
}
