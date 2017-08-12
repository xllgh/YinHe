
package com.yinhe.iptvsetting.view;

import android.app.Activity;
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

public class RouteRestoreFragment extends Fragment implements OnClickListener,
        IRouterSettingCallback {

    private LogUtil mLogUtil = new LogUtil("RouteRestoreFragment");

    private Activity mActivity = null;
    private IRouterSetting mRouterSetting;

    private Button mBtnSure = null;
    private Button mBtnCancle = null;

    private final static int MSG_RESTORE_FAILED = 0;
    private final static int MSG_SHOW_PROGRESS_DIALOG = 1;
    private final static int MSG_RESTORE_SUCCESS = 2;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_RESTORE_FAILED:
                    FuncUtil.showToast(mActivity,
                            getString(R.string.hint_restore_failed)
                                    + (msg.obj == null ? "" : " : " + (String) msg.obj));
                    break;
                case MSG_SHOW_PROGRESS_DIALOG:
                    break;
                case MSG_RESTORE_SUCCESS:
                    FuncUtil.showToast(mActivity, R.string.hint_restore_success);
                    break;
                default:
                    break;
            }
        };
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.route_set_restore, container, false);
        initView(view);
        mActivity = getActivity();
        return view;
    }

    private void initView(View view) {
        mBtnSure = (Button) view.findViewById(R.id.btn_sure);
        mBtnCancle = (Button) view.findViewById(R.id.btn_cancel);
        mBtnSure.setOnClickListener(this);
        mBtnCancle.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mLogUtil.d("onResume");
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
        if (requestType != RouterSetting.TYPE_RESTORE) {
            mLogUtil.e("onRequestFinish requestType != RouterSetting.TYPE_RESTORE" + ", type = "
                    + requestType);
            return;
        }

        if (requestResult == null) {
            mLogUtil.e("onRequestFinish requestResult is null!");
            mHandler.sendEmptyMessage(MSG_RESTORE_FAILED);
            return;
        }

        if (RequestResult.RESULT_SUCCESS.equals(requestResult.getResult())) {
            mHandler.sendEmptyMessage(MSG_RESTORE_SUCCESS);
        } else {
            Message msg = new Message();
            msg.what = MSG_RESTORE_FAILED;
            msg.obj = requestResult.getMsg();
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sure:
                mRouterSetting.restore();
                break;
            default:
                break;
        }
    }
}
