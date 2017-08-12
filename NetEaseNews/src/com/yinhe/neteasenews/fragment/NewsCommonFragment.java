package com.yinhe.neteasenews.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yinhe.neteasenews.R;
import com.yinhe.neteasenews.adapter.NewsBaseAdapter;
import com.yinhe.neteasenews.adapter.PicturePager;
import com.yinhe.neteasenews.db.DBUtils;
import com.yinhe.neteasenews.entry.News;
import com.yinhe.neteasenews.ui.CateNavigation;
import com.yinhe.neteasenews.ui.PullScrollView;
import com.yinhe.neteasenews.ui.PullScrollView.onRefreshListener;
import com.yinhe.neteasenews.ui.XListViewFooter;
import com.yinhe.neteasenews.utils.OnNewsLoadMore;
import com.yinhe.neteasenews.utils.XMLParse;

public class NewsCommonFragment extends Fragment implements
		OnPageChangeListener, OnNewsLoadMore, onRefreshListener,
		OnItemClickListener {
	private View rootView;
	public static final String PARAM1 = "num";
	private static final String key1 = "key1";
	private static final String WHICH_FRAGMENT = "which";

	private LinearLayout actionbar;
	private TextView dot1, dot2, dot3, dot4;

	private TextView[] tvList;
	private String TAG = "tag";
	boolean DEBUG = true;

	private ArrayList<View> viewList = new ArrayList<View>();
	private List<News> mListNews = new ArrayList<News>();
	private DBUtils db;
	private NewsBaseAdapter mNewsBaseAdapter;
	private CateNavigation mCateNavigation;

	private static final int END_LOAD = 1;
	private static final int END_REFRESH = 2;

	private ListView listView;
	private ViewPager viewPager;
	private XListViewFooter footerView;
	private ProgressBar pb;
	private PullScrollView pullScrollView;
	private PicturePager pa;

	private int whichFragment = 0;

	public static Fragment getInstance(int which) {

		NewsCommonFragment fragment = new NewsCommonFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(WHICH_FRAGMENT, which);
		fragment.setArguments(bundle);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle b = getArguments();
		whichFragment = (b != null ? b.getInt(WHICH_FRAGMENT) : 1);
		Log.e(this.getClass().getName(), "onCreate whichFragment="
				+ whichFragment);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e(this.getClass().getName(), "onCreateView");
		rootView = inflater.inflate(R.layout.fragment_news3, null);
		init();
		return rootView;
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		Log.e(TAG, "onDestroyView");
		super.onDestroyView();
	}

	private void init() {

		findView();
		setListener();
		setAdapter();
		getNews(whichFragment);
		setIndicator(0);
	}

	public void findView() {
		pullScrollView = (PullScrollView) rootView
				.findViewById(R.id.pullScrollView);
		actionbar = (LinearLayout) getActivity().findViewById(
				R.id.layout_actionbar);

		mCateNavigation = (CateNavigation) getActivity().findViewById(
				R.id.act_main_cate_nav);
		viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);

		listView = (ListView) rootView.findViewById(R.id.listView);
		footerView = new XListViewFooter(getActivity());
		listView.addFooterView(footerView);

		dot1 = (TextView) rootView.findViewById(R.id.dot1);
		dot2 = (TextView) rootView.findViewById(R.id.dot2);
		dot3 = (TextView) rootView.findViewById(R.id.dot3);
		dot4 = (TextView) rootView.findViewById(R.id.dot4);
		tvList = new TextView[] { dot1, dot2, dot3, dot4 };

	}

	private void setListener() {
		viewPager.setOnPageChangeListener(this);
		pullScrollView.setOnRefreshListener(this);
		listView.setOnItemClickListener(this);
	}

	private void setIndicator(int position) {

		for (int i = 0; i < 4; i++) {
			if (position == i) {
				tvList[i].setEnabled(true);
			} else {
				tvList[i].setEnabled(false);
			}
		}

	}

	public boolean isFirst = true;

	private void setAdapter() {

		getPagerPicture(whichFragment);
		pa = new PicturePager(viewList, 3);
		viewPager.setAdapter(pa);

		mNewsBaseAdapter = new NewsBaseAdapter(getActivity(), mListNews);
		listView.setAdapter(mNewsBaseAdapter);
		setListViewHeight(listView);
	}

	private void getNews(int which) {
		mListNews.clear();
		mListNews.addAll(new XMLParse(getActivity().getApplicationContext())
				.getNewsList(which));
		mNewsBaseAdapter.notifyDataSetChanged();
	}

	private void getPagerPicture(int which) {

		String imgUrl = getActivity().getFilesDir().toString()
				+ "/server/pagerPicture/" + (which % 4 + 1) + "/";

		Log.e("tag", imgUrl);

		View view1 = LayoutInflater.from(this.getActivity()).inflate(
				R.layout.item_picture_pager, null);
		View view2 = LayoutInflater.from(this.getActivity()).inflate(
				R.layout.item_picture_pager, null);
		View view3 = LayoutInflater.from(this.getActivity()).inflate(
				R.layout.item_picture_pager, null);
		View view4 = LayoutInflater.from(this.getActivity()).inflate(
				R.layout.item_picture_pager, null);

		ImageView iv1, iv2, iv3, iv4;
		iv1 = (ImageView) view1.findViewById(R.id.imgPicture);
		iv2 = (ImageView) view2.findViewById(R.id.imgPicture);
		iv3 = (ImageView) view3.findViewById(R.id.imgPicture);
		iv4 = (ImageView) view4.findViewById(R.id.imgPicture);

		switch (which % 2) {
		case 0:
			iv1.setBackgroundResource(R.drawable.page1);
			iv2.setBackgroundResource(R.drawable.page2);
			iv3.setBackgroundResource(R.drawable.page3);
			iv4.setBackgroundResource(R.drawable.page4);
			break;
		default:
			iv1.setBackgroundResource(R.drawable.page11);
			iv2.setBackgroundResource(R.drawable.page12);
			iv3.setBackgroundResource(R.drawable.page13);
			iv4.setBackgroundResource(R.drawable.page14);
			break;
		}

		viewPager.setPageMargin(30);
		viewList = new ArrayList<View>();
		viewList.add(view1);
		viewList.add(view2);
		viewList.add(view3);
		viewList.add(view4);
	}

	private void setListViewHeight(ListView listView) {

		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenHeight = dm.heightPixels;

		actionbar.measure(0, 0);
		int actionbarHeight = actionbar.getMeasuredHeight();

		viewPager.measure(0, 0);
		int viewPagerHeight = viewPager.getMeasuredHeight();

		mCateNavigation.measure(0, 0);
		int horizontalScrollViewHeight = mCateNavigation.getMeasuredHeight();

		int listViewHeight = screenHeight - viewPagerHeight - actionbarHeight
				- horizontalScrollViewHeight;
		ViewGroup.LayoutParams lp = listView.getLayoutParams();

		lp.height = listViewHeight;
		listView.setLayoutParams(lp);
	}

	int mCurrentPage = 0;

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPageSelected(int position) {
		// TODO Auto-generated method stub
		setIndicator(position);
		mCurrentPage = position;

		Animation animation_big = AnimationUtils.loadAnimation(getActivity(),
				R.anim.pager_animation_scale_big);
		viewList.get(position).startAnimation(animation_big);

	}

	private void loadMore() {
		footerView.loading();
		handler.sendMessageDelayed(handler.obtainMessage(END_LOAD), 1000);

	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case END_LOAD:

				footerView.normal();
				// xlistview_footer_text.setText(R.string.loadmore);
				Log.e("loadmore", "loadmore_end");
				break;

			case END_REFRESH:
				pullScrollView.stopRefresh();
				Log.e(this.getClass().getName(), "refresh end");
			}
		};
	};

	@Override
	public void newsLoadMore() {
		// TODO Auto-generated method stub

	}

	@Override
	public void refresh() {
		getNews(whichFragment);

		handler.sendMessageDelayed(handler.obtainMessage(END_REFRESH), 1000);

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View itemView, int arg2,
			long arg3) {

		Log.e("onItemClick", arg2 + "," + itemView.getId());
		if (itemView == footerView) {
			Log.e("onItemClick", "loadmore");
			loadMore();
		}

	}

}
