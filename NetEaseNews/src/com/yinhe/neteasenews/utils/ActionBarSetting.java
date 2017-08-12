package com.yinhe.neteasenews.utils;

import android.app.Activity;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.yinhe.neteasenews.R;

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
			ivLeft.setOnClickListener(leftListener);
		}
		if (rightListener != null) {
			ivRight.setOnClickListener(rightListener);
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

}
