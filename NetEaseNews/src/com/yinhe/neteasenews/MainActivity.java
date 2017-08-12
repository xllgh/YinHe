package com.yinhe.neteasenews;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.yinhe.neteasenews.adapter.GridDisplayAdapter;
import com.yinhe.neteasenews.adapter.GridDisplayAdapter.LocationListener;
import com.yinhe.neteasenews.adapter.GridUndisplayAdapter;
import com.yinhe.neteasenews.adapter.GridUndisplayAdapter.UndisplayLocationListener;
import com.yinhe.neteasenews.adapter.NewsFragmentAdapter;
import com.yinhe.neteasenews.db.DBUtils;
import com.yinhe.neteasenews.entry.Category;
import com.yinhe.neteasenews.entry.News;
import com.yinhe.neteasenews.fragment.NewsCommonFragment;
import com.yinhe.neteasenews.ui.CateNavigation;
import com.yinhe.neteasenews.ui.CateNavigation.OnItemClickListener;
import com.yinhe.neteasenews.ui.MyGridView;
import com.yinhe.neteasenews.utils.ActionBarSetting;
import com.yinhe.neteasenews.utils.FileUtils;
import com.yinhe.neteasenews.utils.SPContract;
import com.yinhe.neteasenews.utils.XMLParse;

/**
 * 主界面Activity
 * 
 * @author xll
 * @version 1.0
 */

public class MainActivity extends FragmentActivity implements LocationListener,
		UndisplayLocationListener {

	private boolean mIsChangeByCate = false;
	private boolean mIsChangeByPager = false;;

	private ArrayList<View> viewList;
	private ArrayList<Category> mListCate;
	private ArrayList<Category> mListCateHide;
	private List<News> mListNews;
	ArrayList<Fragment> list;

	private ViewPager viewPager;

	private CateNavigation mCateNavigation;

	private DBUtils dbUtils;

	/*************** lcr *************/
	private TextView mMoreTextView;

	private boolean mIsPopupWindowOpen;
	private PopupWindow mPopupWindow;

	private MyGridView mDisplayGridView;
	private MyGridView mUndisplayGridView;
	private GridUndisplayAdapter mGridUndisplayAdapter;
	private GridDisplayAdapter mDisplayAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		checkLogin();
		 initData();
		 initView();

	}

	private void initData() {
		if (dbUtils == null) {
			dbUtils = new DBUtils(getApplicationContext());
		}
		mListCate = dbUtils.getCategoryList();
		mListCateHide = dbUtils.getHideCategoryList();
		mListNews = new XMLParse(getApplicationContext()).getNewsList(0);
	}

	private void actionbarSetting() {

		ActionBarSetting setting = new ActionBarSetting(this,
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						sureQuit();
					}
				}, new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 显示“更多”的PopupWindow页面
						showPopupWindow(v);
					}
				});

	}

	private void sureQuit() {
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle(R.string.prompt);
		ab.setMessage(R.string.quitApp);
		ab.setNegativeButton(R.string.cancel, null);
		ab.setPositiveButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						MainActivity.this.finish();
					}

				});
		ab.show();
	}

	private void testMethod() {
		list = new ArrayList<Fragment>();
		for (int i = 0; i < mListCate.size(); i++) {
			Fragment f = NewsCommonFragment.getInstance(i);
			list.add(f);
		}
	}

	private void initView() {
		actionbarSetting();
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		testMethod();
		viewPager.setAdapter(new NewsFragmentAdapter(
				getSupportFragmentManager(), list));
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				if (mIsChangeByCate)
					return;
				mIsChangeByPager = true;
				mCateNavigation.setCateNames(getCateNames(), arg0);
				mIsChangeByPager = false;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		mCateNavigation = (CateNavigation) findViewById(R.id.act_main_cate_nav);
		mCateNavigation.setCateNames(getCateNames());
		mCateNavigation.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(int position) {
				if (mIsChangeByPager)
					return;
				mIsChangeByCate = true;
				viewPager.setCurrentItem(position);
				mIsChangeByCate = false;
			}
		});

		mMoreTextView = (TextView) findViewById(R.id.tv_more);
	}

	private void checkLogin() {
		SharedPreferences mSpSettings = getSharedPreferences(
				SPContract.SPNAME_SETTING, MODE_PRIVATE);
		boolean isFiristLogin = mSpSettings.getBoolean(
				SPContract.SP_KEY_IS_FIRST_LOGIN, true);
		if (!isFiristLogin)
			return;
		Editor editor = mSpSettings.edit();
		editor.putBoolean(SPContract.SP_KEY_IS_FIRST_LOGIN, false);
		editor.commit();
		String files[] = null;
		try {
			files = MainActivity.this.getAssets().list("server");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File dirNews = new File(files[0]);
		dirNews.mkdirs();
		String srcPath = dirNews.getAbsolutePath();
		String desPath = Environment.getExternalStorageDirectory()
				+ File.separator + "WangYi";
		// FileUtils.copyFile(srcPath,desPath);

		try {
			FileUtils.copyAsset(MainActivity.this.getAssets(), File.separator
					+ "server", desPath);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// new FileUtils(getApplicationContext()).copyAsserts("server",
		// dirNews.getPath());
		List<Category> cateList = new XMLParse(getApplicationContext())
				.getDefaultCates();
		if (dbUtils == null) {
			dbUtils = new DBUtils(getApplicationContext());
		}
		dbUtils.addCategoryList(cateList);
	}

	private void showPopupWindow(View v) {
		if (mIsPopupWindowOpen && mPopupWindow.isShowing()) {
			// 当PopupWindow已经开启时，点击控件进行关闭
			mPopupWindow.dismiss();
			mIsPopupWindowOpen = false;
			return;
		}
		View contentView = LayoutInflater.from(MainActivity.this).inflate(
				R.layout.window_more, null);
		mPopupWindow = new PopupWindow(contentView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setOutsideTouchable(false);
		mPopupWindow.setAnimationStyle(R.style.anim_window_more);
		mPopupWindow.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.shape_window_more_bg));
		mPopupWindow.setFocusable(true);

		// 初始化GridView
		initGridView(contentView);
		mPopupWindow.showAsDropDown(v);
		mIsPopupWindowOpen = true;

		// 设置关闭监听事件
		mPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				mCateNavigation.setCateNames(getCateNames());
			}
		});
	}

	private void initGridView(View view) {

		mDisplayGridView = (MyGridView) view
				.findViewById(R.id.mgv_display_column);
		mDisplayAdapter = new GridDisplayAdapter(MainActivity.this, mListCate,
				mPopupWindow, mMoreTextView);
		mDisplayGridView.setAdapter(mDisplayAdapter);

		mUndisplayGridView = (MyGridView) view
				.findViewById(R.id.mgv_undisplay_column);
		mGridUndisplayAdapter = new GridUndisplayAdapter(MainActivity.this,
				mListCateHide, mDisplayGridView);
		mUndisplayGridView.setAdapter(mGridUndisplayAdapter);

		// 设置监听
		mGridUndisplayAdapter.setUndisplayLocationListener(MainActivity.this);
		mDisplayAdapter.setLocationListener(MainActivity.this);
	}

	private List<String> getCateNames() {
		List<String> names = new ArrayList<String>();
		for (Category cate : mListCate) {
			names.add(cate.getName());
		}
		return names;
	}

	@Override
	public void getLocation(int location, boolean visible) {
		if (!visible) {
			mPopupWindow.dismiss();
			mIsPopupWindowOpen = false;
			mMoreTextView.setEnabled(true);
			mMoreTextView.setFocusable(true);
			mCateNavigation.setCateNames(getCateNames(), location);
		} else {
			list.remove(location);
			dbUtils.changeCategoryRank(mListCate.get(location).getId(), -1);
			mListCateHide.add(mListCate.get(location));
			mListCate.remove(location);
			mGridUndisplayAdapter.notifyDataSetChanged();
			mUndisplayGridView.setAdapter(mGridUndisplayAdapter);
			mDisplayAdapter.notifyDataSetChanged();
			mDisplayGridView.setAdapter(mDisplayAdapter);
		}
	}

	@Override
	public void getUndisplayLocation(int location) {

		dbUtils.changeCategoryRank(mListCateHide.get(location).getId(),
				mListCate.get(mListCate.size() - 1).getRanking() + 1);
		mListCate.add(mListCateHide.get(location));
		mListCateHide.remove(location);
		mGridUndisplayAdapter.notifyDataSetChanged();
		mUndisplayGridView.setAdapter(mGridUndisplayAdapter);
		mDisplayAdapter.notifyDataSetChanged();
		mDisplayGridView.setAdapter(mDisplayAdapter);
	}

	@Override
	public void changeFocusable() {
		mPopupWindow.setFocusable(false);
		mMoreTextView.setFocusable(true);
		mMoreTextView.requestFocus();
		mMoreTextView.setFocusableInTouchMode(true);
		mPopupWindow.dismiss();
	}

	@Override
	protected void onStop() {
		if (mIsPopupWindowOpen && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
		}
		super.onStop();
	}

}
