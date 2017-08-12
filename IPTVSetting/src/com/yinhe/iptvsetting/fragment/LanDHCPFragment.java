
package com.yinhe.iptvsetting.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yinhe.iptvsetting.R;
import com.yinhe.iptvsetting.common.FuncUtil;
import com.yinhe.iptvsetting.common.NetManager;
import com.yinhe.iptvsetting.object.NetAddress;
import com.yinhe.iptvsetting.view.BaseFragment;

/**
 * 局域网DHCP。
 * 
 * @author zhbn
 */
public class LanDHCPFragment extends BaseFragment {

    private static final String LOG_TAG = "LanDHCPFragment";

    private static final int MSG_UPDATE = 0;

    private Button buttonSure = null;
    private Button buttonCancle = null;
    private TextView mTvAddressStatus;
    private EditText etIpAddress = null;
    private EditText etNetmask = null;
    private EditText etDefaultGateway = null;

    private NetManager mNetManager;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE:
                    NetAddress netAddress = mNetManager.getNetAddress();
                    if (netAddress != null) {
                        etIpAddress.setText(netAddress.getIPaddress());
                        etNetmask.setText(netAddress.getNetMask());
                        etDefaultGateway.setText(netAddress.getGateWay());
                    } else {
                        etIpAddress.setText(FuncUtil.STR_EMPTY);
                        etNetmask.setText(FuncUtil.STR_EMPTY);
                        etDefaultGateway.setText(FuncUtil.STR_EMPTY);
                    }
                    break;
            }
        }
    };

    public LanDHCPFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        logD("onCreateView");
        mActivity = getActivity();
        mNetManager = NetManager.getInstance();

        View view = inflater.inflate(R.layout.lan_dhcp_fragment, container,
                false);
        initData(view);

        return view;
    }

    private void initData(View view) {
        this.mTvAddressStatus = (TextView) view
                .findViewById(R.id.obtain_status);

        this.etIpAddress = (EditText) view.findViewById(R.id.et_ip_address);
        this.etNetmask = (EditText) view.findViewById(R.id.et_netmask);
        this.etDefaultGateway = (EditText) view
                .findViewById(R.id.et_default_gateway);

        buttonSure = (Button) view.findViewById(R.id.button_sure);
        buttonSure.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!mNetManager.isEthernetStateEnabled()) {
                    FuncUtil.showToast(mActivity, R.string.warn_wire_disconnected);
                    mActivity.findViewById(R.id.button_on).requestFocus();
                    return;
                }
                
                if (!mNetManager.isEthernetOn()) {
                    FuncUtil.showToast(mActivity, R.string.eth_phy_link_down_check);
                    return;
                }
                
                logD("Sure button onClick: current DHCP status is "
                        + mNetManager.getEthernetMode());
                mNetManager.startDhcp();
                FuncUtil.showToast(mActivity, R.string.info_save_setting);

                reObtainNetAddress(OnNetTypeChangeListener.LAN_DHCP_ON);

                mActivity.findViewById(R.id.tv_LAN_DHCP).requestFocus();
            }
        });
        buttonCancle = (Button) view.findViewById(R.id.bt_canle);
        buttonCancle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // 点击取消按钮后，光标回到标签，
                mActivity.findViewById(R.id.tv_LAN_DHCP).requestFocus();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        
        if (mNetManager.isObtainingAddress()) {
            mTvAddressStatus
                    .setText(R.string.net_address_obtaining);
        } else if (mNetManager.isPppoeMode()) {
            mTvAddressStatus.setText(R.string.pppoe_ip_obtain);
        } else if (mNetManager.isStaticIpMode()) {
            mTvAddressStatus.setText(R.string.static_ip_setting);
        } else if (mNetManager.isDhcpMode()) {
            mTvAddressStatus.setText(R.string.auto_get);
        }
        
        mHandler.sendEmptyMessage(MSG_UPDATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        mHandler.removeCallbacksAndMessages(null);
    }

    private void logD(String msg) {
        Log.d(LOG_TAG, msg);
    }
}
