package com.yinhe.iptvsetting.common;

import com.yinhe.iptvsetting.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

/**
 * 输入框输入字符数超过限制不能输入并提醒。
 * 
 * @author zhbn
 * 
 */
public class LimitTextWather implements TextWatcher {

	private Context mContext;
	private int mMaxLength;
	private int mLastInputIndex;
	private String mToastText;
	private Toast mToast;

	@SuppressLint("ShowToast")
	public LimitTextWather(Context context, int length) {
		mContext = context;
		mMaxLength = length;
		mToastText = String.format(
				mContext.getResources().getString(
						R.string.warn_edittext_length_limit), mMaxLength);
		mToast = Toast.makeText(mContext, mToastText, Toast.LENGTH_SHORT);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		mLastInputIndex = Math.abs(start - before);
	}

	@Override
	public void afterTextChanged(Editable s) {
		if (s.length() > mMaxLength) {
			s.delete(mLastInputIndex, ++mLastInputIndex);
			if (mToast != null) {
				mToast.setText(mToastText);
			} else {
				mToast = Toast.makeText(mContext, mToastText,
						Toast.LENGTH_SHORT);
			}

			mToast.show();
			return;
		}
	}

}
