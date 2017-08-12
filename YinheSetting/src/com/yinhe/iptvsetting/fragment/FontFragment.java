package com.yinhe.iptvsetting.fragment;

import java.util.ArrayList;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.app.ActivityManagerNative;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;

import com.yinhe.iptvsetting.R;
import com.yinhe.iptvsetting.MainActivity;
import com.yinhe.iptvsetting.SoundAndDisplayFragmentActivity;
import com.yinhe.iptvsetting.adapter.CheckItemAdapter;
import com.yinhe.iptvsetting.view.BaseFragment;
import com.yinhe.model.ListViewItem;

public class FontFragment extends BaseFragment {

	private static final String TAG = "FontFragment";

	private ListView listView;
	private ArrayList<ListViewItem> mList;
	private CheckItemAdapter adapter = null;
	private int mLastSelectPosition = 0;
	private Configuration mCurConfig = new Configuration();
	private TypedValue mTextSizeTyped;
	private DisplayMetrics mDisplayMetrics;

	public FontFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.ds_font, container, false);
		mTextSizeTyped = new TypedValue();
		TypedArray styledAttributes = getActivity().obtainStyledAttributes(
				R.styleable.TextView);
		styledAttributes.getValue(R.styleable.TextView_android_textSize,
				mTextSizeTyped);
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		mDisplayMetrics = new DisplayMetrics();
		mDisplayMetrics.density = metrics.density;
		mDisplayMetrics.heightPixels = metrics.heightPixels;
		mDisplayMetrics.scaledDensity = metrics.scaledDensity;
		mDisplayMetrics.widthPixels = metrics.widthPixels;
		mDisplayMetrics.xdpi = metrics.xdpi;
		mDisplayMetrics.ydpi = metrics.ydpi;
		styledAttributes.recycle();
		initData(view);
		return view;
	}

	private void initData(View view) {
		listView = (ListView) view.findViewById(R.id.lv_hont);
		listView.setOnItemClickListener(listViewOnItemClickListener);
		mList = new ArrayList<ListViewItem>();
		mList.add(new ListViewItem(getResources().getString(R.string.small)));
		mList.add(new ListViewItem(getResources().getString(R.string.middle)));
		mList.add(new ListViewItem(getResources().getString(R.string.big)));
		adapter = new CheckItemAdapter(mList, getActivity()
				.getApplicationContext());
		listView.setAdapter(adapter);
		listView.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				if (hasFocus) {
					listView.setSelector(R.drawable.list_selector);
				} else {
					listView.setSelector(R.drawable.list_null);
				}
			}
		});
	}

	private OnItemClickListener listViewOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long id) {
		    if (mLastSelectPosition == position) {
		        return;
		    }
			adapter.setdata(position);
			adapter.notifyDataSetChanged();
			if (position == 0) {
				mCurConfig.fontScale = .85f;
			} else if (position == 1) {
				mCurConfig.fontScale = 1.0f;
			} else {
				mCurConfig.fontScale = 1.15f;
			}

			createDialog();
		}
	};

	public void onResume() {
		super.onResume();
		try {
			mCurConfig.updateFrom(ActivityManagerNative.getDefault()
					.getConfiguration());
		} catch (RemoteException e) {
		}
		if (mCurConfig.fontScale == 0.85f) {
		    mLastSelectPosition = 0;
			adapter.setdata(0);
			adapter.notifyDataSetChanged();
		} else if (mCurConfig.fontScale == 1.0f) {
		    mLastSelectPosition = 1;
			adapter.setdata(1);
			adapter.notifyDataSetChanged();
		} else if (mCurConfig.fontScale == 1.15f) {
		    mLastSelectPosition = 2;
			adapter.setdata(2);
			adapter.notifyDataSetChanged();
		}
	};

	protected void createDialog() {
		AlertDialog.Builder builder = new Builder(getActivity(), R.style.style_dialog);
		builder.setMessage(getResources().getString(R.string.notifcation));
		builder.setTitle(getResources().getString(R.string.title_dialog));
		builder.setPositiveButton(getResources().getString(R.string.wifi_save),
				new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						try {
							ActivityManagerNative.getDefault()
									.updatePersistentConfiguration(mCurConfig);
						} catch (RemoteException e) {
							Log.w(TAG, "Unable to write font size");
						}

//						MainActivity.FLAG = true;
						getActivity().findViewById(R.id.title_hont).requestFocus();
						getActivity().setResult(SoundAndDisplayFragmentActivity.RESULT_VALUE);
						getActivity().finish();
						dialog.dismiss();
					}
				});
		builder.setNegativeButton(getResources()
				.getString(R.string.wifi_cancel), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			    adapter.setdata(mLastSelectPosition);
	            adapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

}
