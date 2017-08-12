
package com.yinhe.iptvsetting.fragment;

import java.util.ArrayList;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.hisilicon.android.HiAoService;
import com.yinhe.iptvsetting.R;
import com.yinhe.iptvsetting.adapter.CheckItemAdapter;
import com.yinhe.iptvsetting.view.BaseFragment;
import com.yinhe.model.ListViewItem;

public class HDMIFragment extends BaseFragment {

    private static final String TAG = "HDMIFragment";
    private HiAoService mAOService;
    private ListView mListView;
    private ArrayList<ListViewItem> mDataList;
    private String[] mEntries;
    private ArrayList<Integer> mValueArray = new ArrayList<Integer>();
    private CheckItemAdapter mAdapter = null;

    public HDMIFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ds_hdmi, container, false);

        initData(view);
        return view;
    }
    
    private void initData(View view) {
        Log.d(TAG, "initData()");
        mAOService = new HiAoService();
        

        mListView = (ListView) view.findViewById(R.id.lv_hdmi_options);
        mListView.setOnItemClickListener(listViewOnItemClickListener);
        mDataList = new ArrayList<ListViewItem>();
        mEntries = getResources().getStringArray(R.array.hdmi_output_show);
        String[] entryValues = getResources().getStringArray(R.array.hdmi_output_value);
        for (String item : mEntries) {
            mDataList.add(new ListViewItem(item));
        }
        int valuesLength = entryValues.length;
        for (int i = 0; i < valuesLength; i++) {
            mValueArray.add(i, Integer.valueOf(entryValues[i]));
        }
        mAdapter = new CheckItemAdapter(mDataList, getActivity()
                .getApplicationContext());
        mListView.setAdapter(mAdapter);
        mListView.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean hasFocus) {
                if (hasFocus) {
                    mListView.setSelector(R.drawable.list_selector);
                } else {
                    mListView.setSelector(R.drawable.list_null);
                }
            }
        });
    }

    private OnItemClickListener listViewOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int position,
                long id) {
            mAOService.setAudioOutput(1, mValueArray.get(position));
            mAdapter.setdata(position);
            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        int srentry = mAOService.getAudioOutput(1);
        mAdapter.setdata(mValueArray.indexOf(srentry));
        mAdapter.notifyDataSetChanged();
    }
}
