package com.hb.test.httpservertest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnInfoListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

public class MainActivity extends Activity implements OnInfoListener {

	private static final String TAG = "HttpServerTest";
	private static final int PORT = 8080;
	// private static final String FILE_NAME="/storage/extSdCard/Temp/test.ts";
	private static final String FILE_NAME = "/storage/emulated/0/r.ts";

	private Button mButtonStart = null;
	private Button mButtonStop = null;
	private VideoView mVideoView = null;

	private HttpServer mHttpServer = null;
	private long startTime = 0;
	private long endTime = 0;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mButtonStart = (Button) findViewById(R.id.buttonStart);
		mButtonStop = (Button) findViewById(R.id.buttonStop);
		mVideoView = (VideoView) findViewById(R.id.videoView);
		mVideoView.setOnInfoListener(this);
		mButtonStart.setEnabled(true);
		mButtonStop.setEnabled(false);
		HttpServer.HttpHandler[] handlers = { new HttpServer.HttpHandler("*",
				new HttpFileHandler()) };
		mHttpServer = new HttpServer(PORT, 4, handlers);
		mHttpServer.start();

		if (Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void buttonStart_onClick(View v) {
		// if (mHttpServer != null)
		// return;
		startTime = System.currentTimeMillis();
		mVideoView.setVideoURI(Uri.parse("http://localhost:8080"));
		LogUtils.time("buttonStart_onClick");
		mVideoView.start();
		mButtonStart.setEnabled(false);
		mButtonStop.setEnabled(true);
	}

	public void buttonStop_onClick(View v) {
		if (mHttpServer == null)
			return;
		mVideoView.stopPlayback();
		mHttpServer.Stop();
		// mHttpServer = null;
		mButtonStart.setEnabled(true);
		mButtonStop.setEnabled(false);
	}

	@Override
	protected void onStop() {
		buttonStop_onClick(null);
		super.onStop();
	}

	private class HttpFileHandler implements HttpRequestHandler {

		@Override
		public void handle(HttpRequest request, HttpResponse response,
				HttpContext context) throws HttpException, IOException {

			Header[] headers = request.getAllHeaders();
			for (Header h : headers) {
				Log.i(TAG, "HttpFileHandler Header " + h.toString());
			}

			Log.i(TAG, "HttpFileHandler request : "
					+ request.getRequestLine().toString());
			String method = request.getRequestLine().getMethod()
					.toUpperCase(Locale.ENGLISH);
			Log.i(TAG, "HttpFileHandler method : " + method);
			if (!method.equals("GET")) {
				throw new MethodNotSupportedException(method
						+ " method not supported");
			}
			String target = request.getRequestLine().getUri();
			Log.i(TAG, "HttpFileHandler target : " + target);

			File file = new File(FILE_NAME);

			response.setStatusCode(HttpStatus.SC_OK);
			// FileEntity body = new FileEntity(file, "");
			InputStreamEntity body = new InputStreamEntity(new FileInputStream(
					file), file.length());
			body.setContentType("video/mp2ts");
			response.setEntity(body);
			Log.i(TAG, "Serving file " + file.getPath());
		}

	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
			LogUtils.time("MEDIA_INFO_VIDEO_RENDERING_START:"
					+ (System.currentTimeMillis() - startTime));
			return true;
		}
		return false;
	}

}
