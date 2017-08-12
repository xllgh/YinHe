package com.nercms.receive;

import java.nio.ByteBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;

public class Videoplay extends SurfaceView {
	public int width = 352;
	public int height = 288;
	public byte[] mPixel = new byte[width * height * 2];
	public ByteBuffer buffer = ByteBuffer.wrap(mPixel);
	public Bitmap VideoBit = Bitmap.createBitmap(width, height, Config.RGB_565);

	public Videoplay(Context context, AttributeSet attrs) // ¹¹Ôìº¯Êý
	{
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		VideoBit.copyPixelsFromBuffer(buffer);
		canvas.drawBitmap(VideoBit, 0, 0, null);
		super.onDraw(canvas);

	}
}
