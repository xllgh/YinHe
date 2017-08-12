package com.yinhe.neteasenews.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yinhe.neteasenews.R;

public class XListViewFooter extends LinearLayout {

	public static final String NORMAL = "�鿴���";
	public static final String RELESE = "�ɿ�������";

	public Context mContext;
	
	// 刷锟斤拷状态
	public int mState;
	public static final int STATE_NORMAL = 0;
	public static final int STATE_READY = 1;
	public static final int STATE_LOADING = 2;

	// 刷锟铰控硷拷
	private View mContentView;
	private TextView mHintView;
	private ProgressBar mProgressBar;

	public XListViewFooter(Context context) {
		super(context);
		initViews(context);
	}

	public XListViewFooter(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews(context);
	}

	@SuppressLint("NewApi")
	public XListViewFooter(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initViews(context);
	}

	private void initViews(Context context) {
		mContext = context;
		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LinearLayout moreView = (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.xlistview_footer, null);
		addView(moreView, p);
		mContentView=moreView.findViewById(R.id.xlistview_footer_content);
		mHintView = (TextView) moreView.findViewById(R.id.xlistview_footer_text);
		mProgressBar = (ProgressBar) moreView.findViewById(R.id.xlistview_footer_pb);
//		g_RaUp = new RotateAnimation(0.0f, -180.0f, Animation.RELATIVE_TO_SELF,
//				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//		g_RaDown = new RotateAnimation(0.0f, -180.0f,
//				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
//				0.5f);
//		g_RaUp.setDuration(ROTE_ANIM_DURATION);
//		g_RaDown.setDuration(ROTE_ANIM_DURATION);
//		g_RaUp.setFillAfter(true);
//		g_RaDown.setFillAfter(true);
	}

	public void setState(int state) {
		mProgressBar.setVisibility(View.INVISIBLE);
		mHintView.setVisibility(View.INVISIBLE);
		switch (state) {
		case STATE_NORMAL:
			mHintView.setVisibility(View.VISIBLE);
			mHintView.setText(getResources().getString(R.string.loadmore));
			break;

		case STATE_READY:
			mHintView.setVisibility(View.VISIBLE);
			mHintView.setText(getResources().getString(R.string.loading));
			break;
		case STATE_LOADING:
			mProgressBar.setVisibility(View.VISIBLE);
			mHintView.setVisibility(View.GONE);
//			mHintView.setText("锟斤拷锟斤拷锟斤拷...");
			break;
		}
	}

//	public void setVisiableHeight(int height) {
//		if (height < 0)
//			height = 0;
//		LinearLayout.LayoutParams p = (LayoutParams) mContentView.getLayoutParams();
//		p.height = height;
//		mContentView.setLayoutParams(p);
//	}


	public void show() {
		LinearLayout.LayoutParams p=(LayoutParams) mContentView.getLayoutParams();
		p.height=LayoutParams.WRAP_CONTENT;
		mContentView.setLayoutParams(p);
	}

	public void hide() {
		LinearLayout.LayoutParams p=(LayoutParams) mContentView.getLayoutParams();
		p.height=0;
		mContentView.setLayoutParams(p);
	}

	public int getBottomMargin() {
		LinearLayout.LayoutParams p=(LayoutParams) mContentView.getLayoutParams();
		return p.bottomMargin;
	}

	public void setBottomMargin(int height) {
		if(height>0){
			LinearLayout.LayoutParams p=(LayoutParams) mContentView.getLayoutParams();
			p.bottomMargin=height;
			mContentView.setLayoutParams(p);
		}
	}
	
	public void normal(){
		mHintView.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.GONE);
	}

	public void loading(){
		mHintView.setVisibility(View.GONE);
		mProgressBar.setVisibility(View.VISIBLE);
	}
	
}
