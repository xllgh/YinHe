package com.yinhe.neteasenews.ui;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.yinhe.neteasenews.R;

public class XListView extends ListView implements OnScrollListener {

	private float mLastY = -1f;
	// ���ڻع�
	private Scroller mScroller;
	private OnScrollListener mScrollListener;
	private IXListViewListener mListViewListener;

	private XListViewHeader mHeaderView;
	private RelativeLayout mHeaderViewContent;
	private TextView mHeaderTimeView;
	private int mHeaderHeight;
	private boolean mEnablePullRefresh = true;
	private boolean mPullRefreshing;

	private XListViewFooter mFooterView;
	private boolean mEnablePullLoad;
	private boolean mPullLoading;
	private boolean mIsFooterReady; // footer

	private int mTotalItemCount;

	private int mScrollBack;

	private static final int SCROLLBACK_HEADER = 0;
	private static final int SCROLLBACK_FOOTER = 1;

	private final static int SCROLL_DURATION = 400; // ����ʱ��

	private final static int PULL_LOAD_MORE_DELTA = 50; // ���ظ��ľ���
	private final static float OFFSET_RADIO = 1.8f;

	private Handler mHandler; // 延时停止刷洗

	public XListView(Context context) {
		super(context);
		initWithContext(context);
	}

	public XListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWithContext(context);
	}

	public XListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWithContext(context);
	}

	private void initWithContext(Context context) {
		mScroller = new Scroller(context, new DecelerateInterpolator());
		super.setOnScrollListener(this);
		mHeaderView = new XListViewHeader(context);
		mHeaderViewContent = (RelativeLayout) mHeaderView
				.findViewById(R.id.xlistview_header_content);
		mHeaderTimeView = (TextView) mHeaderView
				.findViewById(R.id.xlistview_header_time);
		mHeaderView.setFocusable(false);
		mHeaderView.setClickable(false);
		mHeaderView.setEnabled(false);
//		mHeaderView.hide();
		addHeaderView(mHeaderView);

		mFooterView = new XListViewFooter(context);

		mFooterView.setFocusable(true);
		mFooterView.setFocusableInTouchMode(true);
		mFooterView.setBackgroundResource(R.drawable.selector_item_news);
		mFooterView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startLoadMore();
			}
		});

		mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						mHeaderHeight = mHeaderViewContent.getHeight();
						getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
					}
				});
		mHandler = new Handler();
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		// ȷ��footer���һ����Ӳ���ֻ���һ��
		if (!mIsFooterReady) {
			mIsFooterReady = true;
			addFooterView(mFooterView);
		}
		super.setAdapter(adapter);
	}

	public void setPullRefreshEnable(boolean enable) {
		mEnablePullRefresh = enable;
		if (!mEnablePullRefresh) {
			mHeaderViewContent.setVisibility(View.INVISIBLE);
//			 mHeaderView.hide();
		} else {
//			 mHeaderView.show();
			mHeaderViewContent.setVisibility(View.VISIBLE);
		}
	}

	public void setPullLoadEnable(boolean enable) {
		mEnablePullLoad = enable;
		if (!mEnablePullLoad) {
			mFooterView.hide();
			mFooterView.setOnClickListener(null);
		} else {
			mPullLoading = false;
			mFooterView.show();
			mFooterView.setState(XListViewFooter.STATE_NORMAL);
			mFooterView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startLoadMore();
				}
			});
		}
	}

	public void stopRefresh() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (mPullRefreshing) {
					mPullLoading = false;
					resetHeaderHeight();
//					mHeaderView.hide();
				}
			}
		}, 500);
	}

	public void stopLoadMore() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (mPullLoading) {
					mPullLoading = false;
					mFooterView.setState(XListViewFooter.STATE_NORMAL);
				}
			}
		}, 500);
	}

	public void setReFreshingTime(String time) {
		mHeaderTimeView.setText(time);
	}

	private void invokeOnScrolling() {
		if (mScrollListener instanceof OnXScrollListener) {
			OnXScrollListener l = (OnXScrollListener) mScrollListener;
			l.onXScrolling(this);
		}
	}

	private void updateHeaderHeight(float f) {
		mHeaderView
				.setVisiableHeight((int) f + mHeaderView.getVisiableHeight());

		if (mEnablePullRefresh && !mPullRefreshing) {
			if (mHeaderView.getVisiableHeight() > mHeaderHeight) {
				mHeaderView.setState(XListViewHeader.STATE_READY);
			} else {
				mHeaderView.setState(XListViewHeader.STATE_NORMAL);
			}
		}
		setSelection(0); // scroll to top each time
	}

	private void resetHeaderHeight() {
		int height = mHeaderView.getVisiableHeight();
		if (height == 0) {
			return;
		}
		// if (mPullRefreshing && height <= mHeaderHeight)
		// return;
		int finalHeight = 0;
		if (mPullRefreshing && height > mHeaderHeight) {
			finalHeight = mHeaderHeight;
		}
		mScrollBack = SCROLLBACK_HEADER;
		mScroller.startScroll(0, height, 0, finalHeight - height,
				SCROLL_DURATION);
		invalidate();
	}

	private void updateFooterHeight(float f) {
		int height = mFooterView.getBottomMargin() + (int) f;
		if (mEnablePullLoad && !mPullLoading) {
			if (height > PULL_LOAD_MORE_DELTA) {
				mFooterView.setState(XListViewFooter.STATE_READY);
			} else {
				mFooterView.setState(XListViewFooter.STATE_NORMAL);
			}
		}
		mFooterView.setBottomMargin(height);
	}

	private void resetFooterHeight() {
		int bottomMargin = mFooterView.getBottomMargin();
		if (bottomMargin > 0) {
			mScrollBack = SCROLLBACK_FOOTER;
			mScroller.startScroll(0, bottomMargin, 0, -bottomMargin,
					SCROLL_DURATION);
			invalidate();
		}
	}

	private void startLoadMore() {
		mPullLoading = true;
		mFooterView.setState(XListViewFooter.STATE_LOADING);
		if (mListViewListener != null) {
			mListViewListener.onLoadMore();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		mHeaderView.show();
		if (mLastY == -1) {
			mLastY = ev.getRawY();
		}
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastY = ev.getRawY();
			break;

		case MotionEvent.ACTION_MOVE:
			final float deltaY = ev.getRawY() - mLastY;
			mLastY = ev.getRawY();
			if (getFirstVisiblePosition() == 0
					&& (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {
				updateHeaderHeight(deltaY / OFFSET_RADIO);
				invokeOnScrolling();
			} else if (getLastVisiblePosition() == mTotalItemCount
					&& (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
				updateFooterHeight(-deltaY / OFFSET_RADIO);
			}
			break;

		default:
			mLastY = -1;
			if (getFirstVisiblePosition() == 0) {
				if (mHeaderView.getVisiableHeight() > mHeaderHeight
						&& mEnablePullRefresh) {
					mPullRefreshing = true;
					mHeaderView.setState(XListViewHeader.STATE_REFERSH);
					if (mListViewListener != null) {
						mListViewListener.onRefresh();
					}
				}
				resetHeaderHeight();
			} else if (getLastVisiblePosition() == mTotalItemCount - 1) {
				if (mEnablePullLoad
						&& mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA
						&& !mPullLoading) {
					startLoadMore();
				}
				resetFooterHeight();
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (mScrollBack == SCROLLBACK_HEADER) {
				mHeaderView.setVisiableHeight(mScroller.getCurrY());
			} else {
				mFooterView.setBottomMargin(mScroller.getCurrY());
			}
			postInvalidate();
			invokeOnScrolling();
		}
		super.computeScroll();
	}

	private float xDistance, yDistance, xLast, yLast;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				xDistance = yDistance = 0f;
				xLast = ev.getX();
				yLast = ev.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				final float curX = ev.getX();
				final float curY = ev.getY();
				xDistance += Math.abs(curX - xLast);
				yDistance += Math.abs(curY - yLast);
				xLast = curX;
				yLast = curY;
				if (xDistance > yDistance) {
					return false;
				}
		}
		return super.onInterceptTouchEvent(ev);
	}

	public void setXListViewListener(IXListViewListener l) {
		mListViewListener = l;
	}

	public interface OnXScrollListener extends OnScrollListener {
		void onXScrolling(View view);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mScrollListener != null) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mTotalItemCount = totalItemCount;
		if (mScrollListener != null) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);
		}
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		mScrollListener = l;
	}
}
