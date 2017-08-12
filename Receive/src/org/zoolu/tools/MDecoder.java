package org.zoolu.tools;

import java.nio.ByteBuffer;

import org.zoolu.tools.MEncoder.FrameListener;

import android.annotation.SuppressLint;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.view.Surface;

@SuppressLint("NewApi")
public class MDecoder implements FrameListener {
	MediaCodec decoder;
	private String VIDEO_FORMAT = "Video/AVC";
	int width = 1280;
	int height = 720;
	private Surface surface;

	public MDecoder(Surface surface) {
		this.surface = surface;
	}

	public void initMDecoder() {
		decoder = MediaCodec.createDecoderByType(VIDEO_FORMAT);
		MediaFormat format = MediaFormat.createVideoFormat(VIDEO_FORMAT, width,
				height);
		decoder.configure(format, surface, null, 0);
	}

	@Override
	public void onFrame(ByteBuffer buffer, int offset, int length, int flag) {
		ByteBuffer[] inputBuffers = decoder.getInputBuffers();
		int inputBufferIndex = decoder.dequeueInputBuffer(-1);
//		if (inputBufferIndex >= 0) {
//			ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
//			inputBuffer.clear();
//			inputBuffer.put(buffer, offset, length);
//			decoder.queueInputBuffer(inputBufferIndex, 0, length, mCount
//					* 1000000 / FRAME_RATE, 0);
//			mCount++;
//		}

		MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
		int outputBufferIndex = decoder.dequeueOutputBuffer(bufferInfo, 0);
		while (outputBufferIndex >= 0) {
			decoder.releaseOutputBuffer(outputBufferIndex, true);
			outputBufferIndex = decoder.dequeueOutputBuffer(bufferInfo, 0);
		}
	}

}
