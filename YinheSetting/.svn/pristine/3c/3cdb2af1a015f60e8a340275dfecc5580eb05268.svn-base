
package com.yinhe.iptvsetting;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yinhe.iptvsetting.adapter.FragmentTabAdapter;
import com.yinhe.iptvsetting.common.FuncUtil;
import com.yinhe.iptvsetting.common.NetManager;
import com.yinhe.iptvsetting.common.NetManager.NetworkStateChangedListener;
import com.yinhe.iptvsetting.fragment.DHCPPlusFragment;
import com.yinhe.iptvsetting.fragment.LANFragment;
import com.yinhe.iptvsetting.fragment.LANFragment.OnAddressLoadCompletedListener;
import com.yinhe.iptvsetting.fragment.LanDHCPFragment;
import com.yinhe.iptvsetting.fragment.PPPOEFragment;
import com.yinhe.iptvsetting.object.NetAddress;
import com.yinhe.iptvsetting.view.BaseFragment;
import com.yinhe.iptvsetting.view.BaseFragment.OnNetTypeChangeListener;

public class WireConnectFragmentActivity extends FragmentActivity implements
        OnFocusChangeListener, OnClickListener {

    private static final String LOG_TAG = "WireConnectFragmentActivity";

    private Button btOn = null;
    private Button btOFF = null;
    private TextView mTvEthState;
    private boolean mIsEthUp = true;
    private List<TextView> tvData = new ArrayList<TextView>();
    private FragmentTabAdapter myFragmentTabAdapter = null;
    private PPPOEFragment mPPPOEFragment;
    private LANFragment mLANFragment;
    private List<BaseFragment> mFragmentList = new ArrayList<BaseFragment>();
    private LinearLayout myLayout = null;
    private SharedPreferences mySPreferences = null;
    private ContentResolver mContentResolver;
    private ProgressDialog myProgressDialog = null;
    private TextView tvLANDHCP = null;

    private ServiceConnection mServiceConnection;

    private NetManager mNetManager;

    /* 当前FragmentID */
    private int mCurrentFramentID;

    // SharedPreferences中项目的名称，用于保存选中Fragment的索引。
    private static final String SP_ITEM_NAME = "index";
    // 默认选中Fragment的索引。
    private static final int DEFAULT_INDEX = 0;

    private static final int MSG_UPDATE = 0;
    private static final int MSG_OBTAIN_IP_ADRESS = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE:
                    logD("mHandler MSG_UPDATE");
                    refreshCurrentFragment();
                    break;
                case MSG_OBTAIN_IP_ADRESS:
                    logD("mHandler MSG_OBTAIN_IP_ADRESS");
                    if (mNetManager.isEthernetOn()) {
                        post(mObatainIpRunnable);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private OnNetTypeChangeListener mOnNetTypeChangeListener = new OnNetTypeChangeListener() {
        @Override
        public void execute(int type) {
            logD("mOnNetTypeChangeListener type = " + type);
            refreshObtainIpState(true);
            if (type != OnNetTypeChangeListener.LAN_ON) {
                mNetManager.setNetAddress(null);
                mHandler.sendEmptyMessage(MSG_UPDATE);
            }

            // if (type == OnNetTypeChangeListener.DHCP_PLUS_ON) {
            // logD("OnNetTypeChangeListener send");
            // sendMsgToObtainIP(2000);
            // }
        }
    };

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        super.setContentView(R.layout.wire_connect);
        logD("onCreate");

        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetManager.destroyNetManagerInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mNetManager.registerBroadcastReceiver(this, mNetworkStateChangedListener);

        boolean isWireTurnOn = mNetManager.isEthernetStateEnabled();
        myLayout.setBackgroundResource(isWireTurnOn ? R.drawable.on
                : R.drawable.off);

        // 获取上次保存的Fragment索引，若取不到，默认为DHCP。
        int index = this.mySPreferences.getInt(SP_ITEM_NAME, DEFAULT_INDEX);
        if (tvData != null && tvData.size() > DEFAULT_INDEX) {
            TextView tv = tvData.get(index);
            myFragmentTabAdapter.showFocuseFragment(tv);
            tv.requestFocus();
        }

        if (isWireTurnOn && mNetManager.isEthernetOn()) {
            refreshObtainIpState(true);
            logD("onStart send");
            sendMsgToObtainIP(0);
        } else {
            mNetManager.setNetAddress(null);
            refreshObtainIpState(false);
            mHandler.sendEmptyMessage(MSG_UPDATE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeMessages(MSG_OBTAIN_IP_ADRESS);
        mNetManager.unRegisterBroadcastReceiver(this);
    }

    private void initData() {
        myProgressDialog = new ProgressDialog(this);

        mNetManager = NetManager.createNetManagerInstance(this);

        mySPreferences = getSharedPreferences("LAST_CLICK",
                Activity.MODE_WORLD_READABLE | Activity.MODE_WORLD_WRITEABLE);
        this.myLayout = (LinearLayout) super.findViewById(R.id.ll_bg);

        this.mTvEthState = (TextView) findViewById(R.id.tv_ethernet_state);

        this.btOn = (Button) super.findViewById(R.id.button_on);
        this.btOFF = (Button) super.findViewById(R.id.button_off);
        this.btOn.setOnFocusChangeListener(this);
        this.btOFF.setOnFocusChangeListener(this);
        btOn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.removeMessages(MSG_OBTAIN_IP_ADRESS);
                mNetManager.enableEthernet(true);
                myLayout.setBackgroundResource(R.drawable.on);
                sendMsgToObtainIP(10000);
                refreshObtainIpState(true);
                mNetManager.setNetAddress(null);
                mHandler.sendEmptyMessage(MSG_UPDATE);
            }
        });
        btOFF.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.removeMessages(MSG_OBTAIN_IP_ADRESS);
                mNetManager.enableEthernet(false);
                myLayout.setBackgroundResource(R.drawable.off);
                refreshObtainIpState(false);
                mNetManager.setNetAddress(null);
                mHandler.sendEmptyMessage(MSG_UPDATE);
            }
        });

        TextView tvPPPOE = (TextView) super.findViewById(R.id.tv_PPPOE);
        tvPPPOE.setOnFocusChangeListener(this);
        tvPPPOE.setOnClickListener(this);
        tvData.add(tvPPPOE);

        TextView tvDHCP = (TextView) super.findViewById(R.id.tv_DHCP);
        tvDHCP.setVisibility(View.GONE);
        // tvDHCP.setOnFocusChangeListener(this);
        // tvDHCP.setOnClickListener(this);
        // tvData.add(tvDHCP);

        final TextView tvLAN = (TextView) super.findViewById(R.id.tv_LAN);
        tvLAN.setOnFocusChangeListener(this);
        tvLAN.setOnClickListener(this);
        tvData.add(tvLAN);

        tvLANDHCP = (TextView) super.findViewById(R.id.tv_LAN_DHCP);
        tvLANDHCP.setOnFocusChangeListener(this);
        tvLANDHCP.setOnClickListener(this);

        tvData.add(tvLANDHCP);

        mPPPOEFragment = new PPPOEFragment();
        mLANFragment = new LANFragment();

        mFragmentList.add(mPPPOEFragment);
        mFragmentList.add(new DHCPPlusFragment());
        mLANFragment
                .setOnAddressLoadCompletedListener(new OnAddressLoadCompletedListener() {
                    @Override
                    public void OnAddressLoadCompleted() {
                        if (mCurrentFramentID == R.id.tv_LAN) {
                            tvLAN.requestFocus();
                        }
                    }
                });
        mFragmentList.add(mLANFragment);
        mFragmentList.add(new LanDHCPFragment());

        for (BaseFragment element : mFragmentList) {
            element.setOnNetTypeChangeListener(mOnNetTypeChangeListener);
        }

        LinearLayout myLinearLayout = (LinearLayout) super
                .findViewById(R.id.myLinearLayout);

        myFragmentTabAdapter = new FragmentTabAdapter(this, mFragmentList,
                R.id.myFrameLayout, myLinearLayout);
    }

    private NetworkStateChangedListener mNetworkStateChangedListener = new NetworkStateChangedListener() {
        @Override
        public void changed(String action, int state) {
            if (action
                    .equals(NetManager.ETHERNET_STATE_CHANGED_ACTION)) {
                switch (state) {
                    case NetManager.EVENT_PHY_LINK_UP:
                        mIsEthUp = true;
                        mNetManager.setEthernetOnOrOff(true);
                        mTvEthState.setVisibility(View.INVISIBLE);
                        refreshObtainIpState(true);
                        mHandler.sendEmptyMessage(MSG_UPDATE);
                        sendMsgToObtainIP(0);
                        break;
                    case NetManager.EVENT_PHY_LINK_DOWN:
                        mIsEthUp = false;
                        mNetManager.setEthernetOnOrOff(false);
                        mTvEthState.setVisibility(View.VISIBLE);
                        mNetManager.setNetAddress(null);
                        refreshObtainIpState(false);
                        mHandler.sendEmptyMessage(MSG_UPDATE);
                        break;
                    case NetManager.EVENT_DHCP_CONNECT_SUCCESSED:
                        sendMsgToObtainIP(0);
                        break;
                    case NetManager.EVENT_STATIC_CONNECT_SUCCESSED:
                        sendMsgToObtainIP(0);
                        break;
                    default:
                        break;
                }
            } else if (action
                    .equals(NetManager.PPPOE_STATE_CHANGED_ACTION)) {
                switch (state) {
                    case NetManager.EVENT_CONNECT_SUCCESSED:
                        sendMsgToObtainIP(0);
                        mPPPOEFragment.setPppoeStatus(NetManager.EVENT_CONNECT_SUCCESSED);
                        break;
                    case NetManager.EVENT_CONNECT_FAILED:
                        mPPPOEFragment.setPppoeStatus(NetManager.EVENT_CONNECT_FAILED);
                        break;
                    case NetManager.EVENT_DISCONNECT_SUCCESSED:
                        sendMsgToObtainIP(0);
                        break;
                    case NetManager.EVENT_DISCONNECT_FAILED:
                        break;
                    case NetManager.EVENT_CONNECTING:
                        mPPPOEFragment.setPppoeStatus(NetManager.EVENT_CONNECTING);
                        break;
                    case NetManager.EVENT_CONNECT_FAILED_AUTH_FAIL:
                        mPPPOEFragment.setPppoeStatus(NetManager.EVENT_CONNECT_FAILED_AUTH_FAIL);
                        break;
                }
                if (myFragmentTabAdapter.getCorrentFragment() == mPPPOEFragment) {
                    mPPPOEFragment.onResume();
                }
            }
        }
    };

    /**
     * 设置当前获取IP的状态。
     * 
     * @param isObtainingAddress
     */
    private void refreshObtainIpState(boolean isObtainingAddress) {
        mHandler.removeMessages(MSG_OBTAIN_IP_ADRESS);
        mNetManager.setIsObtainingAddress(isObtainingAddress);
    }

    /**
     * 刷新当前Fragment UI。
     */
    private void refreshCurrentFragment() {

        logD("refreshCurrentFragment");

        BaseFragment fragment = myFragmentTabAdapter.getCorrentFragment();
        logD("current index = "
                + myFragmentTabAdapter.getCorrentFragmentNumber());
        if (fragment != null) {
            fragment.onResume();
        }
    }

    private void logD(String msg) {
        Log.d(LOG_TAG, msg);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        // 启用、禁用，按钮
        if (v instanceof Button) {
            Button btn = (Button) v;
            btn.setTextColor(hasFocus ? getResources().getColor(
                    R.color.focused_color) : Color.WHITE);
            return;
        }

        int id = v.getId();
        mCurrentFramentID = id;
        TextView textView = (TextView) v;
        textView.setTextColor(Color.WHITE);
        for (int i = 0; i < this.tvData.size(); i++) {
            if (this.tvData.get(i).getId() == id) {
                if (hasFocus) {
                    textView.setBackgroundResource(R.drawable.epg_btn_f);
                    textView.setTextColor(getResources().getColor(
                            R.color.focused_color));
                    this.myFragmentTabAdapter.showFocuseFragment(textView);
                } else {
                    textView.setBackgroundResource(R.drawable.btn_title);
                }
            } else {
                this.tvData.get(i).setBackgroundColor(
                        getResources().getColor(android.R.color.transparent));
            }
        }
    }

    @Override
    public void onClick(View v) {

        // 保存当前选中Fragment的索引。
        int index = DEFAULT_INDEX;
        int currentViewId = v.getId();
        for (int i = 0; i < tvData.size(); i++) {
            if (currentViewId == tvData.get(i).getId()) {
                index = i;
            }
        }
        SharedPreferences.Editor editor = this.mySPreferences.edit();
        editor.putInt(SP_ITEM_NAME, index);
        editor.commit();
    }

    private Runnable mObatainIpRunnable = new Runnable() {
        @Override
        public void run() {
            logD("obataintNetAddress start at " + java.lang.System.currentTimeMillis());
            NetAddress netAddress = mNetManager.obataintNetAddress();
            logD("obataintNetAddress end at " + java.lang.System.currentTimeMillis());

            logD("mNetAddress = " + netAddress);
            if (netAddress != null) {
                logD("mNetAddress IP = " + netAddress.getIPaddress());
            }

            if (mHandler != null) {
                if (netAddress != null
                        && !FuncUtil.isNullOrEmpty(netAddress.getIPaddress())) {
                    mNetManager.setNetAddress(netAddress);
                    refreshObtainIpState(false);
                    mHandler.sendEmptyMessage(MSG_UPDATE);
                } else {
                    logD("mObatainIpRunnable send");
                    sendMsgToObtainIP(2000);
                }
            }
        }
    };

    private void sendMsgToObtainIP(long delayMillis) {
        mHandler.removeMessages(MSG_OBTAIN_IP_ADRESS);
        if (delayMillis > 0) {
            mHandler.sendEmptyMessageDelayed(MSG_OBTAIN_IP_ADRESS, delayMillis);
        } else {
            mHandler.sendEmptyMessage(MSG_OBTAIN_IP_ADRESS);
        }
    }

}
