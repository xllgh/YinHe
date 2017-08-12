package com.yinhe.neteasenews.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yinhe.neteasenews.R;

public class CateNavigation extends HorizontalScrollView {

	private static final String TAG = "CateNavigation";

	private static final int TEXT_PADDING_TOP_AND_BOTTOM = 4;
	private static final int TEXT_PADDING_LEFT_AND_RIGHT = 20;
	private static final int INDICATOR_HEIGHT = 10;
	
	private boolean mIsReLoadFocusView;
	
	/** 每屏最多显示标签个数 */
	private int mCountOnScreen;
	/** 屏幕的宽度 */
	private int mScreenWitdh;
	/** 子元素的宽度 */
	private int mChildWidth;
	/** 子元素的高度 */
	private int mChildHeight;
	/** 当前最后一张图片的index */
	private int mLastIndex;
	/** 当前第一张图片的下标 */
	private int mFristIndex;
	/** 当前拥有焦点的标签的序列号 */
	private int mCurrFocusIndex;
	/** 存储分类名的集合 */
	private List<String> mListCates;

	/** 前一个有焦点的控件 */
	private TextView mTvPreFocus;
	/** 当前有焦点的控件 */
	private TextView mTvCurrFocus;
	/** 存放分类TextView的LinearLayout */
	private LinearLayout mItemContainer;

	/** 用于TextView的焦点变化监听器 */
	private OnTextViewFocusChangeListerner mOnTextViewFocusChangeListerner;
	/** 用于TextView的单击监听器 */
	private OnTextViewClickListener mOnTextViewClickListener;

	private OnItemClickListener mOnItemClickListener;

	/** 保存View与位置的键值对 */
	private Map<View, Integer> mViewPos;
	private Context context;

	public CateNavigation(Context context) {
		super(context);
		init(context);
	}

	public CateNavigation(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CateNavigation(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	/**
	 * 初始化组件
	 */
	private void init(Context context) {
		initData();

		this.context = context;

		// 对HorizontalScrollView进行相关设置
		setHorizontalScrollBarEnabled(false);
		setVerticalScrollBarEnabled(false);
		// 对mItemContainer进行相关设置
		mItemContainer = new LinearLayout(getContext());
		mItemContainer.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		mItemContainer.setOrientation(LinearLayout.HORIZONTAL);
		addView(mItemContainer);
		// 初始化监听器
		mOnTextViewFocusChangeListerner = new OnTextViewFocusChangeListerner();
		mOnTextViewClickListener = new OnTextViewClickListener();
	}

	/**
	 * 初始化相关数据
	 */
	private void initData() {
		mFristIndex = -1;
		mLastIndex = -1;
		mIsReLoadFocusView = false;
		mChildWidth = 0;
		mChildHeight = 0;
		mListCates = new ArrayList<String>();
		mTvPreFocus = null;
		mTvCurrFocus = null;
		mViewPos = new HashMap<View, Integer>();
		// 测量屏幕大小
		WindowManager wm = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		mScreenWitdh = outMetrics.widthPixels;
	}

	/**
	 * 设置分类集合
	 * 
	 * @param cates
	 *            分类集合
	 */
	public void setCateNames(List<String> cates) {
		setCateNames(cates, 0);
	}

	/**
	 * 设置分类集合，分类导航栏以firstIndex序号开始循环排
	 * 
	 * @param cates
	 * @param firstIndex
	 */
	public void setCateNames(List<String> cates, int firstIndex) {
		// 重置标记数据
		resetWidget();
		mListCates.addAll(cates);
		// 初始化最头上的标签
		TextView item = null;
		item = initTextView(firstIndex);
		// 强制计算当前View的宽和高
		if (mChildWidth == 0 && mChildHeight == 0) {
			int w = View.MeasureSpec.makeMeasureSpec(0,
					View.MeasureSpec.UNSPECIFIED);
			int h = View.MeasureSpec.makeMeasureSpec(0,
					View.MeasureSpec.UNSPECIFIED);
			item.measure(w, h);
			mChildHeight = item.getMeasuredHeight();
			mChildWidth = item.getMeasuredWidth();
			Log.e(TAG, item.getMeasuredWidth() + "," + item.getMeasuredHeight());
			// 计算每次加载多少个View
			mCountOnScreen = (mScreenWitdh / mChildWidth == 0) ? mScreenWitdh
					/ mChildWidth + 1 : mScreenWitdh / mChildWidth + 2;

			Log.e(TAG, "mCountOnScreen = " + mCountOnScreen
					+ " ,mChildWidth = " + mChildWidth);

		}
		mItemContainer.addView(item);
		mViewPos.put(item, firstIndex);
		mFristIndex = firstIndex;
//		item.requestFocus();
		decorateTextView(item);
		mTvCurrFocus = item;
		int index = firstIndex+1;
		// 循环添加足够标签
		for (int i = 1;i < mCountOnScreen-1; i++) {
			index = (firstIndex+i) % mListCates.size();
			item = initTextView(index);
			mItemContainer.addView(item);
			mLastIndex = index;
		}
		mFristIndex = firstIndex-1<0?(mListCates.size()-1):(firstIndex-1);
		item = initTextView(mFristIndex);
		mItemContainer.addView(item, 0);
		mItemContainer.getChildAt(1).requestFocus();
	}

	/**
	 * 初始化标签
	 * 
	 * @param i
	 *            标签序号
	 * @return 初始化完成的标签TextView
	 */
	private TextView initTextView(int i) {
		final TextView item = new TextView(getContext());
		item.setPadding(TEXT_PADDING_LEFT_AND_RIGHT,
				TEXT_PADDING_TOP_AND_BOTTOM, TEXT_PADDING_LEFT_AND_RIGHT,
				TEXT_PADDING_TOP_AND_BOTTOM);
		item.setGravity(Gravity.CENTER);
		item.setTextSize(getResources().getDimensionPixelSize(
				R.dimen.cate_text_size));
		item.setFocusable(true);
		item.setFocusableInTouchMode(false);
		item.setText(mListCates.get(i));
		item.setOnFocusChangeListener(mOnTextViewFocusChangeListerner);
		item.setOnClickListener(mOnTextViewClickListener);
		item.setTag(i);
		// if (i == mCurrFocusIndex) {
		// // 如果是有焦点的标签，直接完成焦点标签设置
		// decorateTextView(item);
		// item.getViewTreeObserver().addOnPreDrawListener(
		// new OnPreDrawListener() {
		//
		// private boolean isFirstDraw = true;
		//
		// @Override
		// public boolean onPreDraw() {
		// if (isFirstDraw) {
		// Drawable drawable = new ColorDrawable(
		// getResources().getColor(
		// R.color.bg_title_text_focus));
		// drawable.setBounds(0, 0, item.getWidth()
		// - TEXT_PADDING_LEFT_AND_RIGHT * 2,
		// INDICATOR_HEIGHT);
		// item.setCompoundDrawables(null, null, null,
		// drawable);
		// isFirstDraw = false;
		// }
		// return true;
		// }
		// });
		// mTvPreFocus = item;
		// }
		int color;
		if (i == mCurrFocusIndex) {
			color = getResources().getColor(R.color.bg_title_text_focus);
			mTvPreFocus = item;
			mIsReLoadFocusView = true;
		}else{
			color = Color.TRANSPARENT;
		}
		int w = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		item.measure(w, h);
		int itemWidth = item.getMeasuredWidth();
		Drawable drawable = new ColorDrawable(color);
		drawable.setBounds(0, 0, itemWidth - TEXT_PADDING_LEFT_AND_RIGHT
				* 2, INDICATOR_HEIGHT);
		item.setCompoundDrawables(null, null, null, drawable);

		// 每完成一个标签初始化，在Map中记录
		mViewPos.put(item, i);
		return item;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_MOVE:
			// Log.e(TAG, getScrollX() + "");

			int scrollX = getScrollX();
			// 如果当前scrollX为view的宽度，加载下一张，移除第一张
			if (scrollX >= mCountOnScreen*mChildWidth-mScreenWitdh) {
				loadNextImg();
			}
			// 如果当前scrollX = 0， 往前设置一张，移除最后一张
			if (scrollX == 0) {
				loadPreImg();
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * 加载下一张图片
	 */
	protected void loadNextImg() {
		mLastIndex = (mLastIndex + 1) % (mListCates.size());
		// 移除第一张图片，且将水平滚动位置置0
		mViewPos.remove(mItemContainer.getChildAt(0));
		mItemContainer.removeViewAt(0);
		// 获取下一张图片
		Log.e(TAG, "加载标签" + mLastIndex);
		View view = initTextView(mLastIndex);
		mItemContainer.addView(view);
		scrollTo(0, 0);
		mViewPos.put(view, mLastIndex);
		// 当前第一张图片下标
		mFristIndex = (mFristIndex + 1) % (mListCates.size());
	}

	/**
	 * 加载前一张图片
	 */
	protected void loadPreImg() {
		// 获得当前应该显示为第一张图片的下标
		int index = mLastIndex - mCountOnScreen;
		index = index < 0 ? mListCates.size() + index : index;
		if (index >= 0) {
			// 移除最后一张
			int oldViewPos = mItemContainer.getChildCount() - 1;
			mViewPos.remove(mItemContainer.getChildAt(oldViewPos));
			mItemContainer.removeViewAt(oldViewPos);

			// 将此View放入第一个位置
			View view = initTextView(index);
			mItemContainer.addView(view, 0);
			// 水平滚动位置向左移动view的宽度个像素
			scrollTo(mChildWidth, 0);
			// 重新计算头尾序号
			mLastIndex = (mLastIndex - 1) < 0 ? mListCates.size() + mLastIndex
					- 1 : mLastIndex - 1;
			mFristIndex = (mFristIndex - 1) < 0 ? mListCates.size()
					+ mFristIndex - 1 : mFristIndex - 1;
		}
	}

	/**
	 * 重置标签导航栏
	 */
	private void resetWidget() {
		mItemContainer.removeAllViews();
		initData();
	}

	/**
	 * TextView焦点变化监听器
	 * 
	 * @author lhz
	 * 
	 */
	private class OnTextViewFocusChangeListerner implements
			OnFocusChangeListener {

		/** 标记焦点是否在本控件内部 */
		private boolean isFocusFromOutside = true;

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			TextView item = (TextView) v;
			if (hasFocus) {
				if (isFocusFromOutside && null != mTvPreFocus) {
					// 非初始化状态且焦点从外部控件传入
					if (null != mTvPreFocus.getParent()) {
						// 前一个有焦点的控件仍在可见范围内，此时可以直接让该控件请求焦点
						mTvPreFocus.requestFocus();
					} else {
						// 前一个有焦点的控件不在可见范围内，代表此控件已移除，此时应以该序号位置开始重新加载数据
						List<String> temp = new ArrayList<String>();
						temp.addAll(mListCates);
						setCateNames(temp, mCurrFocusIndex);
					}
					
				} else {
					// 焦点一直在控件内部转移
					mTvCurrFocus = item;
					doFocusChangedAnim();

					Integer index = (Integer) item.getTag();
					// 转移到开始或者结束位置是继续加载两边的标签
					if (index.intValue() == mFristIndex) {
						loadPreImg();
					} else if (index.intValue() == mLastIndex) {
						loadNextImg();
					} else {
						;
					}
				}
			} else {
				// 每当标签失去焦点是检查控件是否还拥有焦点，并标记
				mTvPreFocus = item;
				if (null != checkFocus()) {
					isFocusFromOutside = false;
				} else {
					isFocusFromOutside = true;
					removeFocusState();
				}
			}
		}

		/**
		 * 检查焦点是否在本控件内
		 * 
		 * @return
		 */
		private View checkFocus() {
			int childCount = mItemContainer.getChildCount();
			for (int i = 0; i < childCount; i++) {
				if (mItemContainer.getChildAt(i).isFocused()) {
					return mItemContainer.getChildAt(i);
				}
			}
			return null;
		}
	}

	/**
	 * 标签TextView单击事件监听器
	 * 
	 * @author lhz
	 * 
	 */
	private class OnTextViewClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Log.e(TAG, "点击事件");
			TextView item = (TextView) v;
			if (mIsReLoadFocusView)
				mTvCurrFocus = mTvPreFocus;
			if (item != mTvCurrFocus) {
				mTvPreFocus = mTvCurrFocus;
				mTvCurrFocus = item;
			}
			doFocusChangedAnim();
		}
	}

	/**
	 * 选择标签变化改变标签样式
	 */
	private void doFocusChangedAnim() {
		if (mTvPreFocus == mTvCurrFocus)
			return;
		decorateTextView(mTvCurrFocus);
		if (mTvPreFocus == null)
			return;
		mTvPreFocus.setTextColor(getResources().getColor(
				R.color.title_text_normal));
		Drawable drawable = new ColorDrawable(getResources().getColor(
				R.color.bg_title_text_normal));
		drawable.setBounds(0, 0, mTvPreFocus.getMeasuredWidth()
				- TEXT_PADDING_LEFT_AND_RIGHT * 2, INDICATOR_HEIGHT);
		mTvPreFocus.setCompoundDrawables(null, null, null, drawable);
		// 单击并不是控件获得焦点，改变标签后立刻让该标签标记为失去焦点
		mTvPreFocus = mTvCurrFocus;
	}

	/**
	 * 将TextView修饰为有焦点样式，并记录焦点序号，同时出发标签单击监听器
	 * 
	 * @param tv
	 *            待修饰TextView
	 */
	private void decorateTextView(TextView tv) {
		tv.setTextColor(getResources().getColor(R.color.title_text_focus));

		Drawable drawable = new ColorDrawable(getResources().getColor(
				R.color.bg_title_text_focus));
		drawable.setBounds(0, 0, tv.getMeasuredWidth() - TEXT_PADDING_LEFT_AND_RIGHT
				* 2, INDICATOR_HEIGHT);
		tv.setCompoundDrawables(null, null, null, drawable);
		mCurrFocusIndex = (Integer) tv.getTag();

		if (null != mOnItemClickListener) {
			mOnItemClickListener.onItemClick(mCurrFocusIndex);
		}
	}
	
	private void removeFocusState(){
		Drawable drawable = new ColorDrawable(getResources().getColor(
				R.color.bg_title_text_normal));
		drawable.setBounds(0, 0, mTvCurrFocus.getMeasuredWidth() - TEXT_PADDING_LEFT_AND_RIGHT
				* 2, INDICATOR_HEIGHT);
		mTvCurrFocus.setCompoundDrawables(null, null, null, drawable);
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		mOnItemClickListener = onItemClickListener;
	}

	/**
	 * 标签单击事件处理借口
	 * 
	 * @author lhz
	 */
	public interface OnItemClickListener {
		public void onItemClick(int position);
	}
}
