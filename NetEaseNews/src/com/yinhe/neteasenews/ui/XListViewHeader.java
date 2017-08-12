package com.yinhe.neteasenews.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yinhe.neteasenews.R;

public class XListViewHeader extends LinearLayout {



	public static final int STATE_NORMAL = 0;
	public static final int STATE_READY = 1;
	public static final int STATE_REFERSH = 2;
	// ˢ��״̬
	public int mState = STATE_NORMAL;


	private LinearLayout mContainer;
	private ImageView mArrowImageView;
	private TextView mHintTextView;
	private ProgressBar mProgressBar;

	private RotateAnimation mRotateUpAnim;
	private RotateAnimation mRotateDownAnim;
	private final int ROTE_ANIM_DURATION = 180;

	public XListViewHeader(Context context) {
		super(context);
		initViews(context);
	}

	public XListViewHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews(context);
	}

	@SuppressLint("NewApi")
	public XListViewHeader(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initViews(context);
	}

	private void initViews(Context context) {
		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, 0);
		mContainer = (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.xlistview_header, null);
		addView(mContainer, p);
		setGravity(Gravity.BOTTOM);

		mArrowImageView = (ImageView) findViewById(R.id.xlistview_header_arrow);
		mHintTextView = (TextView) findViewById(R.id.xlistview_header_hint_textview);
		mProgressBar = (ProgressBar) findViewById(R.id.xlistview_header_pb);

		mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateUpAnim.setDuration(ROTE_ANIM_DURATION);
		mRotateUpAnim.setFillAfter(true);

		mRotateDownAnim = new RotateAnimation(0.0f, -180.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateDownAnim.setDuration(ROTE_ANIM_DURATION);
		mRotateDownAnim.setFillAfter(true);
	}

	public void setState(int state) {
		if (state == mState)
			return;
		if (state == STATE_REFERSH) {
			mArrowImageView.clearAnimation();
			mArrowImageView.setVisibility(View.INVISIBLE);
			mProgressBar.setVisibility(View.VISIBLE);
		} else {
			mArrowImageView.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.INVISIBLE);
		}

		switch (state) {
		case STATE_NORMAL:
			if (mState == STATE_READY)
				mArrowImageView.startAnimation(mRotateDownAnim);
			if (mState == STATE_REFERSH)
				mArrowImageView.clearAnimation();
			mHintTextView.setText(R.string.freshing);
			break;

		case STATE_READY:
			if (mState != STATE_READY) {
				mArrowImageView.clearAnimation();
				mArrowImageView.startAnimation(mRotateUpAnim);
				mHintTextView.setText(R.string.readyFresh);
			}
			break;
		case STATE_REFERSH:
			mHintTextView.setText(R.string.freshing);
			break;
		}
		mState = state;
	}

	public void setVisiableHeight(int height) {
		if (height < 0)
			height = 0;
		LayoutParams p = (LayoutParams) mContainer.getLayoutParams();
		p.height = height;
		mContainer.setLayoutParams(p);
	}

	public int getVisiableHeight() {
		return mContainer.getHeight();
	}

	public void show() {
		mContainer.setVisibility(View.VISIBLE);
	}

	public void hide() {
		mContainer.setVisibility(View.GONE);
	}

}
