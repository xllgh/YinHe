
package com.yinhe.iptvsetting.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.yinhe.iptvsetting.R;
import com.yinhe.iptvsetting.common.FuncUtil;
import com.yinhe.iptvsetting.common.IRouterSetting;
import com.yinhe.iptvsetting.common.LogUtil;
import com.yinhe.iptvsetting.common.RouterSetting;
import com.yinhe.iptvsetting.common.RouterSetting.IRouterSettingCallback;
import com.yinhe.iptvsetting.common.RouterSetting.RequestResult;

public class RouteLanSetFragment extends Fragment implements IRouterSettingCallback,
        OnClickListener {
    private LogUtil mLogUtil = new LogUtil("RouteWanSetFragment");

    private IRouterSetting mRouterSetting;
    private ProgressDialog mProgressDialog;
    private Activity mActivity = null;

    private AddressInput mTvIpAddress = null;
    private AddressInput mTvNetmask = null;
    private AddressInput mTvDefaultGateway = null;

    private Button mBtnSure = null;
    private Button mBtnCancle = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mActivity = getActivity();
        View view = inflater.inflate(R.layout.route_set_lan, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        mTvIpAddress = (AddressInput) view.findViewById(R.id.et_ip_address);
        mTvNetmask = (AddressInput) view.findViewById(R.id.et_netmask);
        mTvDefaultGateway = (AddressInput) view.findViewById(R.id.et_default_gateway);

        mProgressDialog = new ProgressDialog(mActivity);

        mBtnSure = (Button) view.findViewById(R.id.btn_sure);
        mBtnCancle = (Button) view.findViewById(R.id.btn_cancel);
        mBtnSure.setOnClickListener(this);
        mBtnCancle.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mLogUtil.d("onResume()");
        mRouterSetting = RouterSetting.getInstance(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        mLogUtil.d("onHiddenChanged " + hidden);
        if (!hidden) {
            mRouterSetting = RouterSetting.getInstance(this);
        }
    }

    @Override
    public void onRequestFinish(int requestType, RequestResult requestResult) {
        if (requestType != RouterSetting.TYPE_SETNETWORK) {
            mLogUtil.e("onRequestFinish requestType != RouterSetting.TYPE_SETNETWORK" + ", type = "
                    + requestType);
            return;
        }

        if (requestResult == null) {
            mLogUtil.e("onRequestFinish requestResult is null!");
            mHandler.sendEmptyMessage(MSG_SET_LAN_FAILED);
            return;
        }

        if (RequestResult.RESULT_SUCCESS.equals(requestResult.getResult())) {
            mHandler.sendEmptyMessage(MSG_SET_LAN_SUCCESS);
        } else {
            Message msg = new Message();
            msg.what = MSG_SET_LAN_FAILED;
            msg.obj = requestResult.getMsg();
            mHandler.sendMessage(msg);
        }
    }

    private final static int MSG_SHOW_PROGRESS_DIALOG = 0;
    private final static int MSG_SET_LAN_SUCCESS = 1;
    private final static int MSG_SET_LAN_FAILED = 2;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SHOW_PROGRESS_DIALOG:
                    if (mProgressDialog == null) {
                        mProgressDialog = new ProgressDialog(mActivity);
                    }
                    mProgressDialog.show();
                    break;
                case MSG_SET_LAN_FAILED:
                    FuncUtil.showToast(mActivity,
                            getString(R.string.hint_setup_failed)
                                    + (msg.obj == null ? "" : " : " + (String) msg.obj));
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    break;
                case MSG_SET_LAN_SUCCESS:
                    FuncUtil.showToast(mActivity, R.string.hint_setup_success);
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    break;
                default:
                    break;
            }
        };
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sure:
                String ip = FuncUtil.ipToHexString(mTvIpAddress.getAddress());
                String mask = FuncUtil.ipToHexString(mTvNetmask.getAddress());
                // String gateway =
                // FuncUtil.ipToHexString(mTvDefaultGateway.getAddress());
                String gateway = null;
                if (FuncUtil.isNullOrEmpty(ip) || FuncUtil.isNullOrEmpty(mask)
                /* || FuncUtil.isNullOrEmpty(gateway) */) {
                    FuncUtil.showToast(mActivity, "address is illegal");
                } else {
                    mHandler.sendEmptyMessage(MSG_SHOW_PROGRESS_DIALOG);
                    mRouterSetting.setNetwork(ip, mask, gateway);
                }
                break;
            case R.id.btn_cancel:
                break;
            default:
                break;
        }
    }
}
