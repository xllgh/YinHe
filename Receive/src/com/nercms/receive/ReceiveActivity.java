package com.nercms.receive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.sipdroid.net.RtpPacket;
import org.sipdroid.net.RtpSocket;
import org.sipdroid.net.SipdroidSocket;
import org.zoolu.tools.VideoDecoder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;

import com.ctc.MediaProcessor.MediaProcessorNative;

public class ReceiveActivity extends Activity {
	private volatile boolean isRunning;
	private RtpSocket rtp_socket = null;
	private RtpPacket rtp_packet = null;
	private byte[] socketBuffer = new byte[2048];
	Videoplay view = null;
	private String cachePath = "/sdcard/RTPCache/jni.mpg";
	private String path007 = "/sdcard/007.mpg";
	boolean test = false;
	long startTime = 0;

	Handler mHander = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				MediaProcessorNative.nativeWriteData(cachePath);
				break;

			default:
				break;
			}
		};
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_1:
			test = false;
			if (rtp_socket != null) {
				isRunning = false;
				rtp_socket.close();
				rtp_socket = null;
			}
			if (rtp_socket == null) {
				try {
					rtp_socket = new RtpSocket(new SipdroidSocket(5004));
				} catch (SocketException e) {
					e.printStackTrace();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				rtp_packet = new RtpPacket(socketBuffer, 0);
				isRunning = true;
				new Decoder().start();
				MediaProcessorNative.nativeStartPlay();
			}
			return true;
		case KeyEvent.KEYCODE_2:
			test = false;
			if (rtp_socket != null) {
				isRunning = false;
				rtp_socket.close();
				rtp_socket = null;
			}
			MediaProcessorNative.nativeStop();
			close();
			finish();
			return true;

		case KeyEvent.KEYCODE_3:
			if (rtp_socket != null) {
				isRunning = false;
				rtp_socket.close();
				rtp_socket = null;
			}
			if (rtp_socket == null) {
				try {
					rtp_socket = new RtpSocket(new SipdroidSocket(5004));
				} catch (SocketException e) {
					e.printStackTrace();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				rtp_packet = new RtpPacket(socketBuffer, 0);
				isRunning = true;
				new Decoder().start();
				 MediaProcessorNative.nativeStartPlay();
			}
			startTime = System.currentTimeMillis();
			test = true;
			break;

		case KeyEvent.KEYCODE_4:
			MediaProcessorNative.nativeStartPlay();
			try {
				FileInputStream fis = new FileInputStream(new File(cachePath));
				byte[] datas = new byte[1316];
				while (fis.read(datas) > 0) {
					MediaProcessorNative
							.nativeWriteRTPData(datas, datas.length);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case KeyEvent.KEYCODE_5:
			MediaProcessorNative.nativeStartPlay();
			MediaProcessorNative.nativeWriteData(path007);
			break;

		case KeyEvent.KEYCODE_6:
			MediaProcessorNative.nativeStartPlay();
			MediaProcessorNative.nativeWriteData(cachePath);
			break;
			
//		case 
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void close() {
		isRunning = false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		MediaProcessorNative.nativeInit();
		MediaProcessorNative.nativeSetVideoWindow(0, 0, 1280, 720);
	}

	@Override
	public void finalize() {
		try {
			super.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		close();
	}

	
	private boolean parsePMT(byte[] data){
		int tableId=data[0];
		if(tableId!=0x02){
			return false;
		}
		
		int sectionLen=data[1]&0x0f+data[2]&0xff;
		int current_next_indicator=data[7]&0x01;
		int section_number=data[8]&0xff;
		int last_setion_number=data[9]&0xff;
		int program_info_length=data[10]&0x0f+data[11]&0xff;
		
		
		return true;
	}
	
	File file;

	@SuppressLint("NewApi")
	class Decoder extends Thread {

		int receivedSize = 0;
		boolean firstTime = true;
		FileOutputStream fos = null;

		@Override
		public void run() {
			Log.e("ttt", isRunning + "");
			File file = new File(cachePath);
			if (file.exists()) {
				file.delete();
			} else {
				// file.mkdirs();
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				fos = new FileOutputStream(file);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				while (isRunning) {
					rtp_socket.receive(rtp_packet);
					byte[] content = rtp_packet.getPayload();
					Log.e("RTP", rtp_packet.byteToHex(rtp_packet.getPacket()));
					Log.e("RTP", rtp_packet.getPayloadType()+"");
					Log.e("kkk getPayloadLength",
							rtp_packet.getSequenceNumber() + "");
					Log.e("kkk getPayloadLength", content.length + "");
//					Log.e("kkk", rtp_packet.byteToHex(rtp_packet.getPayload()));
					if (test) {
						fos.write(content, 0, content.length);
					} else {
						Log.e("kkk", "nativeWriteRTPData");
						MediaProcessorNative.nativeWriteRTPData(content,
								content.length);
					}

				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				if (test && fos != null) {
					fos.close();
					fos = null;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mHander.obtainMessage(1).sendToTarget();

			if (rtp_socket != null) {
				isRunning = false;
				rtp_socket.close();
				rtp_socket = null;
			}
		}
	}

	VideoDecoder mVideoDecoder;
}
