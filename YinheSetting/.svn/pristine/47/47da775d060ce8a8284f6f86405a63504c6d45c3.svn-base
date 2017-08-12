
package com.yinhe.iptvsetting.fragment;

import java.util.ArrayList;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.hisilicon.android.HiDisplayManager;
import com.yinhe.iptvsetting.R;
import com.yinhe.iptvsetting.adapter.CheckItemAdapter;
import com.yinhe.iptvsetting.view.BaseFragment;
import com.yinhe.model.Rectangle;
import com.yinhe.model.ListViewItem;

public class VideoFragment extends BaseFragment {
    private Toast mToast;
    private ListView listView;
    private ArrayList<ListViewItem> mList;
    private String[] mEntries;
    private ArrayList<Integer> mValueArray = new ArrayList<Integer>();
    private CheckItemAdapter mAdapter = null;
    private HiDisplayManager display_manager;

    public VideoFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        mActivity = getActivity();
        View view = inflater.inflate(R.layout.ds_video, container, false);
        initData(view);
        return view;
    }

    private void initData(View view) {
        display_manager = new HiDisplayManager();

        listView = (ListView) view.findViewById(R.id.lv_video);
        listView.setOnItemClickListener(listViewOnItemClickListener);
        mList = new ArrayList<ListViewItem>();
        mEntries = getResources().getStringArray(R.array.set_video_tv_list);
        String[] entryValues = getResources().getStringArray(R.array.set_video_tv_list_value);
        for (String item : mEntries) {
            mList.add(new ListViewItem(item));
        }
        int valuesLength = entryValues.length;
        for (int i = 0; i < valuesLength; i++) {
            mValueArray.add(i, Integer.valueOf(entryValues[i]));
        }
        mAdapter = new CheckItemAdapter(mList, getActivity()
                .getApplicationContext());
        listView.setAdapter(mAdapter);
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
            int ret = display_manager.setFmt(mValueArray.get(position));
            if (ret != 0) {
                if (mToast == null) {
                    mToast = Toast.makeText(mActivity, R.string.warn_set_format_failed,
                            Toast.LENGTH_SHORT);
                } else {
                    mToast.setText(R.string.warn_set_format_failed);
                }
                mToast.show();
                return;
            }
            mAdapter.setdata(position);
            mAdapter.notifyDataSetChanged();
            display_manager.SaveParam();
            display_manager.setOptimalFormatEnable(0);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        int newfmt = display_manager.getFmt();
        mAdapter.setdata(mValueArray.indexOf(newfmt));
        mAdapter.notifyDataSetChanged();
    }
}
