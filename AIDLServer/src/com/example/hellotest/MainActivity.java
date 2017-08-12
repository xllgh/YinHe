package com.example.hellotest;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	Button hello;
	Button toastDelay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		hello = (Button) findViewById(R.id.hello);
		toastDelay = (Button) findViewById(R.id.toastDelay);
		toastDelay.setOnClickListener(this);
		hello.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Animation animation = AnimationUtils.loadAnimation(
						MainActivity.this, R.anim.pager_animation);
				hello.startAnimation(animation);
			}
		});
		Log.d("xll", "onCreate");
	}

	Handler handler = new Handler();

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.toastDelay:
//			toastDelay(MainActivity.this);
			showToast(MainActivity.this,R.string.app_name,5000);
			break;

		default:
			break;
		}

	}
	
	public static void showToast(final Activity activity, int stringID,int time) {
		final String  msg = activity.getResources().getString(stringID);
		if(time != 0){
			Timer timer = new Timer();
			//2s÷¥––“ª¥Œ£ø
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(activity, "hahahhaha", Toast.LENGTH_SHORT).show();
						}
					});
				}
			}, time);
		}
	}

	public void toastDelay(final Context context) {
		Toast.makeText(context, "hahah out", Toast.LENGTH_SHORT).show();

		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 1:
					Toast.makeText(context, "hahah", Toast.LENGTH_SHORT).show();
					break;
				}
			}
		};

		handler.sendEmptyMessageDelayed(1, 5000);
	}
}
