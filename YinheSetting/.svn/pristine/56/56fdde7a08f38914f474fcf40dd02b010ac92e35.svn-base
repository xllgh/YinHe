package com.yinhe.iptvsetting.fragment;

import java.util.ArrayList;

import com.hisilicon.android.HiDisplayManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.yinhe.iptvsetting.R;
import com.yinhe.iptvsetting.adapter.CheckItemAdapter;
import com.yinhe.iptvsetting.view.BaseFragment;
import com.yinhe.model.ListViewItem;

public class ScreenDisplayFragment extends BaseFragment {
	private ListView listView;
	private ArrayList<ListViewItem> mList;
	private CheckItemAdapter adapter = null;
	private int selectPosition = 0;
	private HiDisplayManager display_manager;
	private static final int DEFAULT_VALUE = 0;
	private static final int FAILED_VALUE = -1;

	public ScreenDisplayFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.ds_screendisplay, container, false);
		initData(view);
		return view;
	}

	private void initData(View view) {
		display_manager = new HiDisplayManager();

		listView = (ListView) view.findViewById(R.id.lv_screen);
		listView.setOnItemClickListener(listViewOnItemClickListener);
		mList = new ArrayList<ListViewItem>();
		mList.add(new ListViewItem(getResources().getString(R.string.full_screen)));
		mList.add(new ListViewItem(getResources().getString(R.string.four_three)));
		mList.add(new ListViewItem(getResources().getString(R.string.sixteen_nine)));
		adapter = new CheckItemAdapter(mList, getActivity()
				.getApplicationContext());
		listView.setAdapter(adapter);
		listView.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean hasFocu) {
				if (hasFocu) {
					listView.setSelector(R.drawable.list_selector);
				} else {
					listView.setSelector(R.drawable.list_null);
				}
			}
		});
	}

	private OnItemClickListener listViewOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			selectPosition = arg2;
			adapter.setdata(selectPosition);
			adapter.notifyDataSetChanged();

			if (arg2 == 0) {
				updateRatio(0);
			} else if (arg2 == 1) {
				updateRatio(1);
			} else if (arg2 == 2) {
				updateRatio(2);
			}
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		int k = display_manager.getAspectRatio();
		if (k == FAILED_VALUE) {
			k = DEFAULT_VALUE;
		}

		adapter.setdata(k);
		adapter.notifyDataSetChanged();
	}

	private void updateRatio(int ratioValue) {
		display_manager.setAspectRatio(ratioValue);
		display_manager.setAspectCvrs(ratioValue == 0 ? 0 : 1);
		display_manager.SaveParam();
	}
}
