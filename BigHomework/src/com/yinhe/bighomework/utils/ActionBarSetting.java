package com.yinhe.bighomework.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.yinhe.bighomwork.R;

public class ActionBarSetting {
	Activity activity;
	OnClickListener leftListener;
	OnClickListener rightListener;
	ImageView ivLeft;
	ImageView ivRight;

	public ActionBarSetting(Activity activity, OnClickListener leftListener,
			OnClickListener rightListener) {
		this.activity = activity;
		this.leftListener = leftListener;
		this.rightListener = rightListener;
		init();
	}

	private void init() {
		ivLeft = (ImageView) activity.findViewById(R.id.leftImage);
		ivRight = (ImageView) activity.findViewById(R.id.rightImage);
		if (leftListener != null) {
			((View) ivLeft.getParent()).setOnClickListener(leftListener);
		}
		if (rightListener != null) {
			((View) ivRight.getParent()).setOnClickListener(rightListener);
		}
	}

	public void setLeftIcon(int resId) {
		ivLeft.setBackgroundResource(resId);

	}

	public void setRightIcon(int resId) {
		ivRight.setBackgroundResource(resId);

	}

	public void setActionBarClickListener(OnClickListener listener) {
		if (listener == null) {
			return;
		}
		activity.findViewById(R.id.actionbar).setOnClickListener(listener);
	}
	
	public static void sureQuit(final Activity activity) {
		AlertDialog.Builder ab = new AlertDialog.Builder(activity);
		ab.setTitle(R.string.prompt);
		ab.setMessage(R.string.quitApp);
		ab.setNegativeButton(R.string.cancel, null);
		ab.setPositiveButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						activity.finish();
					}

				});
		ab.show();
	}

}
