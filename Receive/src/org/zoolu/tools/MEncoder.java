package org.zoolu.tools;

import java.nio.ByteBuffer;

import android.annotation.SuppressLint;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;

@SuppressLint("NewApi")
public class MEncoder {
	private String VIDEO_FORMAT="Video/AVC";
	int width=1280;
	int height=720;
	FrameListener frameListener;
	
	public MEncoder(FrameListener frameListener) {
		this.frameListener=frameListener;
	}
	
	public interface FrameListener{
		public void onFrame(ByteBuffer buffer,int offset,int length,int flag);
	}
	
	MediaCodec encode;
	
	public void initEncode(){
		encode=MediaCodec.createByCodecName(VIDEO_FORMAT);
		MediaFormat mediaFormat=MediaFormat.createVideoFormat(VIDEO_FORMAT, width, height);
		mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 1024*1024*2);
		mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
		mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV411Planar);
		mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5);
		encode.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
		encode.start();
	}
	
	
	public void encodePacket(byte[] buff,int offset,int length,int flag){
		ByteBuffer[] inputBuffer=encode.getInputBuffers();
		ByteBuffer[] outputBuffer=encode.getOutputBuffers();
		int inputBufferIndex=encode.dequeueInputBuffer(-1);
		if(inputBufferIndex>=0){
			ByteBuffer buffer=inputBuffer[inputBufferIndex];           
			buffer.clear();
			buffer.put(buff,offset,length);
			encode.queueInputBuffer(inputBufferIndex, 0, length, 0, 0);
		}
		
		MediaCodec.BufferInfo info=new MediaCodec.BufferInfo();
		int outputBufferIndex=encode.dequeueOutputBuffer(info, 0);
		while(outputBufferIndex>=0){
			ByteBuffer oBuffer=outputBuffer[outputBufferIndex];
			if(frameListener!=null){
				frameListener.onFrame(oBuffer,0,length,0);
			}
		    encode.releaseOutputBuffer(outputBufferIndex, false);
		    outputBufferIndex=encode.dequeueOutputBuffer(info, 0);
		}
		
		
	}

}
