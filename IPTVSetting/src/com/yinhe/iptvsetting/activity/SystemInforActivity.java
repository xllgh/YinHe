
package com.yinhe.iptvsetting.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.hisilicon.android.hisysmanager.HiSysManager;

import com.android.settings.yinhe.Native;
import com.yinhe.iptvsetting.R;
import com.yinhe.iptvsetting.common.FuncUtil;
import com.yinhe.iptvsetting.common.NetManager;

/**
 * 系统版本信息。
 * 
 * @author zhbn
 */
public class SystemInforActivity extends Activity {

    private static final String TAG = "SystemInfo";

    private static final int POSITION_FACTORY_RESET = 1;

    private final static int[] WH_720P = {
            394, 220
    };
    private final static int[] WH_1080P = {
            591, 330
    };

    private String resStrStorageInfo;
    private Activity mActivity;
    private LayoutInflater mInflater;
    private NetManager mNetManager;
    private SystemInfoAdapter mSystemInfoAdapter;
    private String[] mArrInfoName;
    private String[] mArrInfoValue;

    private boolean is720P = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.system_infor);
        mActivity = this;

        resStrStorageInfo = mActivity.getResources().getString(
                R.string.storage_info);
        mInflater =
                LayoutInflater.from(mActivity);
        mNetManager = NetManager.createNetManagerInstance(this);

        is720P = FuncUtil.is720pMode(this);
        Log.d(TAG, "is720P : " + is720P);

        initData();
    }

    private void initData() {
        mArrInfoName = getResources().getStringArray(R.array.system_info);
        mArrInfoValue = new String[mArrInfoName.length];
        mArrInfoValue[0] = Build.BRAND;
        mArrInfoValue[1] = "";
        mArrInfoValue[2] = Build.MANUFACTURER;
        mArrInfoValue[3] = Build.MODEL;
        mArrInfoValue[4] = Native.getsn();
        mArrInfoValue[5] = mNetManager.getEthernetMacAddress();
        mArrInfoValue[6] = Build.VERSION.RELEASE;
        mArrInfoValue[7] = Build.VERSION.INCREMENTAL;
        mArrInfoValue[8] = FuncUtil.getCpuInfo();
        mArrInfoValue[9] = formatStorageInfo(FuncUtil.getMemoryInfo(mActivity));
        mArrInfoValue[10] = formatStorageInfo(FuncUtil.getExternalStorageInfo(mActivity));

        ListView listView = (ListView) findViewById(R.id.lv_system_info);
        mSystemInfoAdapter = new SystemInfoAdapter();
        listView.setAdapter(mSystemInfoAdapter);

        final OnFocusChangeListener focusChangeListener = new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v instanceof Button) {
                    Button btn = (Button) v;
                    btn.setTextColor(hasFocus ? Color.YELLOW : Color.WHITE);
                }
            }
        };

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == POSITION_FACTORY_RESET) {

                    LayoutInflater inflater = getLayoutInflater();
                    View viewDialog = inflater.inflate(R.layout.dialog, null);
                    final Dialog myDialog = new Dialog(SystemInforActivity.this,
                            R.style.dialog);
                    LayoutParams lp = new LayoutParams(is720P ? WH_720P[0] : WH_1080P[0],
                            is720P ? WH_720P[1] : WH_1080P[1]);
                    myDialog.setContentView(viewDialog, lp);
                    myDialog.show();

                    Button btCancle = (Button) viewDialog.findViewById(R.id.bt_cancel);
                    Button btSure = (Button) viewDialog.findViewById(R.id.bt_sure);
                    btSure.setOnFocusChangeListener(focusChangeListener);
                    btCancle.setOnFocusChangeListener(focusChangeListener);
                    btCancle.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myDialog.dismiss();
                        }
                    });

                    btSure.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ActivityManager.isUserAMonkey()) {
                                Log.e(TAG, "Reset action by monkey!");
                                return;
                            }
                            HiSysManager hisys = new HiSysManager();
                            hisys.reset();
                            sendBroadcast(new Intent("android.intent.action.MASTER_CLEAR"));
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetManager.destroyNetManagerInstance();
    }

    private String formatStorageInfo(String info) {
        String strFromat = "";
        if (info != null) {
            String[] arryInfo = info.split(FuncUtil.STR_SLASH);
            if (arryInfo.length == 2) {
                strFromat = String.format(resStrStorageInfo, arryInfo[0], arryInfo[1]);
            }
        }
        return strFromat;
    }

    class SystemInfoAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mArrInfoName.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.system_info_item, null);
                holder = new Holder();
                holder.tvInfoName = (TextView) convertView
                        .findViewById(R.id.tv_item_name);
                holder.tvInfoValue = (TextView) convertView
                        .findViewById(R.id.tv_item_value);
                holder.ivFactoryReset = (ImageView) convertView
                        .findViewById(R.id.iv_factory_reset);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.tvInfoName.setText(mArrInfoName[position]);
            if (position == POSITION_FACTORY_RESET) {
                holder.tvInfoValue.setVisibility(View.GONE);
                holder.ivFactoryReset.setVisibility(View.VISIBLE);
            } else {
                holder.ivFactoryReset.setVisibility(View.GONE);
                holder.tvInfoValue.setVisibility(View.VISIBLE);
                holder.tvInfoValue.setText(mArrInfoValue[position]);
            }
            return convertView;
        }

        class Holder {
            TextView tvInfoName;
            TextView tvInfoValue;
            ImageView ivFactoryReset;
        }
    }

}
