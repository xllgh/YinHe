
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
import android.widget.TextView;

import com.yinhe.iptvsetting.R;
import com.yinhe.iptvsetting.common.FuncUtil;
import com.yinhe.iptvsetting.common.IRouterSetting;
import com.yinhe.iptvsetting.common.LogUtil;
import com.yinhe.iptvsetting.common.RouterSetting;
import com.yinhe.iptvsetting.common.RouterSetting.IRouterSettingCallback;
import com.yinhe.iptvsetting.common.RouterSetting.RequestResult;

public class RouteRegisterInfoFragment extends Fragment implements
        IRouterSettingCallback, OnClickListener {

    private LogUtil mLogUtil = new LogUtil("RouteRegisterInfoFragment");

    private Activity mActivity = null;
    private ProgressDialog mProgressDialog;
    private IRouterSetting mRouterSetting;

    private TextView mTvActiveState = null;
    private TextView mTvGwMac = null;
    private TextView mTvGwAddress = null;
    private TextView mTvGwPort = null;
    private TextView mTvSoftVer = null;

    private final static int MSG_SHOW_PROGRESS_DIALOG = 0;
    private final static int MSG_DISMISS_PROGRESS_DIALOG = 1;
    private final static int MSG_GET_REGISTER_INFO_SUCCESS = 2;
    private final static int MSG_GET_REGISTER_INFO_FAILED = 3;
    private final static int MSG_GET_ACTIVESTATE_SUCCESS = 4;
    private final static int MSG_GET_ACTIVESTATE_FAILED = 5;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            RequestResult result = null;
            switch (msg.what) {
                case MSG_SHOW_PROGRESS_DIALOG:
                    if (mProgressDialog == null) {
                        mProgressDialog = new ProgressDialog(mActivity);
                    }
                    if (!mProgressDialog.isShowing()) {
                        mProgressDialog.show();
                    }
                    break;
                case MSG_DISMISS_PROGRESS_DIALOG:
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    break;
                case MSG_GET_REGISTER_INFO_SUCCESS:
                    FuncUtil.showToast(mActivity, R.string.hint_get_register_info_success);
                    result = (RequestResult) msg.obj;
                    mTvGwMac.setText(result.getGwMac());
                    mTvGwAddress.setText(result.getGwAddress());
                    mTvGwPort.setText(result.getGwProt());
                    mTvSoftVer.setText(result.getSoftVer());
                    break;
                case MSG_GET_REGISTER_INFO_FAILED:
                    FuncUtil.showToast(mActivity,
                            getString(R.string.hint_get_register_info_failed)
                                    + (msg.obj == null ? "" : " : " + (String) msg.obj));
                    break;

                case MSG_GET_ACTIVESTATE_SUCCESS:
                    FuncUtil.showToast(mActivity, R.string.hint_get_active_state_success);
                    result = (RequestResult) msg.obj;
                    mLogUtil.d("ActiveState = " + result.getData());
                    mTvActiveState.setText("1".equals(result.getData()) ? R.string.active_state_ed
                            : ("0".equals(result.getData()) ? R.string.active_state_no
                                    : R.string.active_state_unkown));
                    break;
                case MSG_GET_ACTIVESTATE_FAILED:
                    FuncUtil.showToast(mActivity,
                            getString(R.string.hint_get_active_state_failed)
                                    + (msg.obj == null ? "" : " : " + (String) msg.obj));
                    break;
                default:
                    break;
            }
        };
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.route_set_register_info, container, false);
        mActivity = getActivity();
        initView(view);
        return view;
    }

    private void initView(View view) {
        mTvActiveState = (TextView) view.findViewById(R.id.txt_register_info_state);
        mTvGwMac = (TextView) view.findViewById(R.id.txt_register_info_gw_mac);
        mTvGwAddress = (TextView) view.findViewById(R.id.txt_register_info_gw_address);
        mTvGwPort = (TextView) view.findViewById(R.id.txt_register_info_gw_port);
        mTvSoftVer = (TextView) view.findViewById(R.id.txt_register_info_soft_ver);
        Button btnRefresh = (Button) view.findViewById(R.id.btn_refresh);
        btnRefresh.setOnClickListener(this);

        mProgressDialog = new ProgressDialog(mActivity);
    }

    @Override
    public void onResume() {
        super.onResume();
        mLogUtil.d("onResume");
        mHandler.sendEmptyMessage(MSG_SHOW_PROGRESS_DIALOG);
        mRouterSetting = RouterSetting.getInstance(this);
        getInfo();
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
        mLogUtil.d("onRequestFinish requestType = " + requestType);
        if (requestType == RouterSetting.TYPE_GETACTIVESTATE) {
            if (requestResult == null) {
                mLogUtil.e("onRequestFinish requestResult is null!");
                mHandler.sendEmptyMessage(MSG_GET_ACTIVESTATE_FAILED);
            } else {
                if (RequestResult.RESULT_SUCCESS.equals(requestResult.getResult())) {
                    Message msg = new Message();
                    msg.what = MSG_GET_ACTIVESTATE_SUCCESS;
                    msg.obj = requestResult;
                    mHandler.sendMessage(msg);
                } else {
                    Message msg = new Message();
                    msg.what = MSG_GET_ACTIVESTATE_FAILED;
                    msg.obj = requestResult.getMsg();
                    mHandler.sendMessage(msg);
                }
            }
            mRouterSetting.getRegisterInfo();
        }
        if (requestType == RouterSetting.TYPE_GETREGISTERINFO) {
            if (requestResult == null) {
                mLogUtil.e("onRequestFinish requestResult is null!");
                mHandler.sendEmptyMessage(MSG_GET_REGISTER_INFO_FAILED);
            } else {
                if (RequestResult.RESULT_SUCCESS.equals(requestResult.getResult())) {
                    Message msg = new Message();
                    msg.what = MSG_GET_REGISTER_INFO_SUCCESS;
                    msg.obj = requestResult;
                    mHandler.sendMessage(msg);
                } else {
                    Message msg = new Message();
                    msg.what = MSG_GET_REGISTER_INFO_FAILED;
                    msg.obj = requestResult.getMsg();
                    mHandler.sendMessage(msg);
                }
            }
            mHandler.sendEmptyMessage(MSG_DISMISS_PROGRESS_DIALOG);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_refresh:
                mHandler.sendEmptyMessage(MSG_SHOW_PROGRESS_DIALOG);
                getInfo();
                break;
            default:
                break;
        }

    }

    private void getInfo() {
        mRouterSetting.getActiveState();
    }
}
