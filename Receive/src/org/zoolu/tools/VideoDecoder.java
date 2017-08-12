package org.zoolu.tools;

import android.annotation.SuppressLint;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by vladlichonos on 6/5/15.
 */
@SuppressLint("NewApi")
public class VideoDecoder implements VideoCodec {

	Worker mWorker;

	public void decodeSample(byte[] data, int offset, int size,
			long presentationTimeUs, int flags) {
		if (mWorker != null) {
			mWorker.decodeSample(data, offset, size, presentationTimeUs, flags);
		}
	}

	public void configure(Surface surface, int width, int height, byte[] csd0,
			int offset, int size) {
		if (mWorker != null) {
			mWorker.configure(surface, width, height,
					/*ByteBuffer.wrap(csd0, offset, size)*/null);
		}
	}

	public void start() {
		if (mWorker == null) {
			mWorker = new Worker();
			mWorker.setRunning(true);
			mWorker.start();
		}
	}

	public void stop() {
		if (mWorker != null) {
			mWorker.setRunning(false);
			mWorker = null;
		}
	}

	class Worker extends Thread {

		volatile boolean mRunning;
		MediaCodec mCodec;
		volatile boolean mConfigured;
		long mTimeoutUs;

		public Worker() {
			mTimeoutUs = 10000l;
		}

		public void setRunning(boolean running) {
			mRunning = running;
		}

		public void configure(Surface surface, int width, int height,
				ByteBuffer csd0) {
			LogUtils.e("configure");
			if (mConfigured) {
				throw new IllegalStateException("Decoder is already configured");
			}
			MediaFormat format = MediaFormat.createVideoFormat(VIDEO_FORMAT,
					width, height);
			mCodec = MediaCodec.createDecoderByType(VIDEO_FORMAT);
			mCodec.configure(format, surface, null, 0);
			mCodec.start();
			mConfigured = true;
		}

		@SuppressWarnings("deprecation")
		public void decodeSample(byte[] data, int offset, int size,
				long presentationTimeUs, int flags) {
			LogUtils.e("decodeSample");

			if (mConfigured) {
				int index = mCodec.dequeueInputBuffer(mTimeoutUs);
				LogUtils.e("decodeSample"+index);
				if (index >= 0) {
					ByteBuffer buffer;
					// since API 21 we have new API to use
//					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//						buffer = mCodec.getInputBuffers()[index];
//						buffer.clear();
//					} else {
//						buffer = mCodec.getInputBuffer(index);
//					}
//					if (buffer != null) {
//						buffer.put(data, offset, size);
//						mCodec.queueInputBuffer(index, 0, size,
//								presentationTimeUs, flags);
//					}
				}
				
				showSurface();
				
			}
		}
		
		public void showSurface(){
			try {
				MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
				while (true) {
					LogUtils.e("VideoDecoder mConfigured"+mConfigured);
					if (mConfigured) {
						int index = mCodec
								.dequeueOutputBuffer(info, mTimeoutUs);
						LogUtils.e("VideoDecoder index"+index);

						if (index >= 0) {
							// setting true is telling system to render frame
							// onto Surface
							mCodec.releaseOutputBuffer(index, true);
							LogUtils.e("releaseOutputBuffer");

							if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
								break;
							}
						}else{ 
							break;
						}
					} else {
						// just waiting to be configured, then decode and render
						try {
							Thread.sleep(10);
						} catch (InterruptedException ignore) {
						}
					}
				}
			} finally {
				if (mConfigured) {
					mCodec.stop();
					mCodec.release();
				}
			}
		}

		@Override
		public void run() {
			try {
				MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
				while (mRunning) {
					LogUtils.e("VideoDecoder mConfigured"+mConfigured);
					if (mConfigured) {
						int index = mCodec
								.dequeueOutputBuffer(info, mTimeoutUs);
						LogUtils.e("VideoDecoder index"+index);

						if (index >= 0) {
							// setting true is telling system to render frame
							// onto Surface
							mCodec.releaseOutputBuffer(index, true);
							LogUtils.e("releaseOutputBuffer");

							if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
								break;
							}
						}else{
							try {
								Thread.sleep(1000);
							} catch (InterruptedException ignore) {
							}
						}
					} else {
						// just waiting to be configured, then decode and render
						try {
							Thread.sleep(10);
						} catch (InterruptedException ignore) {
						}
					}
				}
			} finally {
				if (mConfigured) {
					mCodec.stop();
					mCodec.release();
				}
			}
		}
	}
}
