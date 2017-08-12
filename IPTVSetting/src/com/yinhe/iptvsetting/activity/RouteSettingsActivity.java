
package com.yinhe.iptvsetting.activity;

import java.util.HashMap;
import java.util.Iterator;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.yinhe.iptvsetting.R;
import com.yinhe.iptvsetting.common.EditTextBgToStar;
import com.yinhe.iptvsetting.common.FuncUtil;
import com.yinhe.iptvsetting.common.IRouterSetting;
import com.yinhe.iptvsetting.common.LogUtil;
import com.yinhe.iptvsetting.common.PreferencesUtils;
import com.yinhe.iptvsetting.common.RouterSetting;
import com.yinhe.iptvsetting.common.RouterSetting.IRouterSettingCallback;
import com.yinhe.iptvsetting.common.RouterSetting.RequestResult;
import com.yinhe.iptvsetting.view.RouteAwifiFragment;
import com.yinhe.iptvsetting.view.RouteLanSetFragment;
import com.yinhe.iptvsetting.view.RouteRegisterInfoFragment;
import com.yinhe.iptvsetting.view.RouteRestartFragment;
import com.yinhe.iptvsetting.view.RouteRestoreFragment;
import com.yinhe.iptvsetting.view.RouteUserFragment;
import com.yinhe.iptvsetting.view.RouteWanSetFragment;

public class RouteSettingsActivity extends FragmentActivity implements
        OnFocusChangeListener, IRouterSettingCallback {
    private LogUtil mLogUtil = new LogUtil("RouteSettingsActivity");

    private final static int MSG_SHOW_LOGIN_DIALOG = 0;
    private final static int MSG_SHOW_PROGRESS_DIALOG = 1;
    private final static int MSG_DISMISS_PROGRESS_DIALOG = 2;

    private FragmentManager mFragmentManager;
    private ProgressDialog mProgressDialog;

    private IRouterSetting mRouterSetting;

    private HashMap<TextView, Fragment> mMapFragment = new HashMap<TextView, Fragment>();

    private RouteWanSetFragment mRouteWanSetFragment;
    private RouteLanSetFragment mRouteLanSetFragment;
    private RouteAwifiFragment mRouteAwifiFragment;
    private RouteUserFragment mRouteUserFragment;
    private RouteRestartFragment mRouteRestartFragment;
    private RouteRestoreFragment mRouteRestoreFragment;
    private RouteRegisterInfoFragment mRouteRegisterInfoFragment;

    private Fragment mCurrentFragment;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SHOW_LOGIN_DIALOG:
                    createLoginDialog();
                    FuncUtil.showToast(RouteSettingsActivity.this,
                            getString(R.string.hint_login_failed)
                                    + (msg.obj == null ? "" : " : " + (String) msg.obj));
                    break;
                case MSG_SHOW_PROGRESS_DIALOG:
                    if (mProgressDialog == null) {
                        mProgressDialog = new ProgressDialog(RouteSettingsActivity.this);
                    }
                    mProgressDialog.show();
                    break;
                case MSG_DISMISS_PROGRESS_DIALOG:
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }

                    FuncUtil.showToast(RouteSettingsActivity.this, R.string.hint_login_success);
                    break;
                default:
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_set);
        mFragmentManager = getSupportFragmentManager();
        mRouterSetting = RouterSetting.getInstance(this);
        mProgressDialog = new ProgressDialog(this);
        initView();
    }

    private void initView() {
        TextView tvWAN = (TextView) findViewById(R.id.txt_route_set_wan);
        TextView tvLAN = (TextView) findViewById(R.id.txt_route_set_lan);
        TextView tvAwifi = (TextView) findViewById(R.id.txt_route_set_awifi);
        TextView tvUser = (TextView) findViewById(R.id.txt_route_set_user);
        TextView tvRestart = (TextView) findViewById(R.id.txt_route_set_restart);
        TextView tvRestore = (TextView) findViewById(R.id.txt_route_set_restore);
        TextView tvRegisterInfo = (TextView) findViewById(R.id.txt_route_set_register_info);

        tvWAN.setOnFocusChangeListener(this);
        tvLAN.setOnFocusChangeListener(this);
        tvAwifi.setOnFocusChangeListener(this);
        tvUser.setOnFocusChangeListener(this);
        tvRestart.setOnFocusChangeListener(this);
        tvRestore.setOnFocusChangeListener(this);
        tvRegisterInfo.setOnFocusChangeListener(this);

        mRouteWanSetFragment = new RouteWanSetFragment();
        mRouteLanSetFragment = new RouteLanSetFragment();
        mRouteAwifiFragment = new RouteAwifiFragment();
        mRouteUserFragment = new RouteUserFragment();
        mRouteRestartFragment = new RouteRestartFragment();
        mRouteRestoreFragment = new RouteRestoreFragment();
        mRouteRegisterInfoFragment = new RouteRegisterInfoFragment();

        mMapFragment.put(tvWAN, mRouteWanSetFragment);
        mMapFragment.put(tvLAN, mRouteLanSetFragment);
        mMapFragment.put(tvAwifi, mRouteAwifiFragment);
        mMapFragment.put(tvUser, mRouteUserFragment);
        mMapFragment.put(tvRestart, mRouteRestartFragment);
        mMapFragment.put(tvRestore, mRouteRestoreFragment);
        mMapFragment.put(tvRegisterInfo, mRouteRegisterInfoFragment);

        tvWAN.requestFocus();
        mLogUtil.d("OnCreate(");

        createLoginDialog();
    }

    private void setFragment(Fragment fragment) {
        mLogUtil.d("setFragment" + fragment);
        if (fragment == mCurrentFragment) {
            return;
        }
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out);
        if (mCurrentFragment == null) {
            ft.add(R.id.fl_network_ethernet_content, fragment);
            ft.show(fragment);
        } else {
            if (!fragment.isAdded()) {
                ft.add(R.id.fl_network_ethernet_content, fragment);
            }
            ft.hide(mCurrentFragment);
            ft.show(fragment);
        }
        mCurrentFragment = fragment;
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        TextView textView = (TextView) v;
        mLogUtil.d("onFocusChange");
        if (!isFinishing()) {
            refreshTitleFocusStatus(textView, hasFocus);
        }
        // swits
    }

    void refreshTitleFocusStatus(TextView textView, boolean hasFocus) {
        int id = textView.getId();
        textView.setTextColor(Color.WHITE);
        Iterator iter = mMapFragment.entrySet().iterator();
        while (iter.hasNext()) {
            HashMap.Entry entry = (HashMap.Entry) iter.next();
            TextView tv = (TextView) entry.getKey();
            if (tv.getId() == id) {
                if (hasFocus) {
                    textView.setBackgroundResource(R.drawable.epg_btn_f);
                    textView.setTextColor(getResources().getColor(
                            R.color.focused_color));
                    setFragment((Fragment) entry.getValue());
                } else {
                    textView.setBackgroundResource(R.drawable.btn_title);
                }
            } else {
                tv.setBackgroundColor(
                        getResources().getColor(android.R.color.transparent));
            }
        }
    }

    private static final String KEY_ROUTE_ADDRESS = "route_address";

    private void createLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                RouteSettingsActivity.this, R.style.style_dialog);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_route_login, null);

        final EditText etRouteAddress = (EditText) view
                .findViewById(R.id.et_route_address);
        String routeAddress = PreferencesUtils.getString(this, KEY_ROUTE_ADDRESS);
        etRouteAddress
                .setText(FuncUtil.isNullOrEmpty(routeAddress) ? IRouterSetting.DEFAULT_ROUTE_ADDRESS
                        : routeAddress);

        final EditText etUserName = (EditText) view
                .findViewById(R.id.et_username);
        etUserName.setText("user");
        final EditText etPsw = (EditText) view
                .findViewById(R.id.et_password);
        etPsw.setText("6z3z8e");
        final EditTextBgToStar toStar = new EditTextBgToStar();
        etPsw.setTransformationMethod(toStar);
        CheckBox chbShowPwd = (CheckBox) view
                .findViewById(R.id.show_password);
        chbShowPwd.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                etPsw.setTransformationMethod(isChecked ?
                        null
                        : toStar);
            }
        });
        builder.setTitle("Route login");
        builder.setView(view);
        builder.setPositiveButton(R.string.sure,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PreferencesUtils.putString(RouteSettingsActivity.this, KEY_ROUTE_ADDRESS,
                                etRouteAddress.getText().toString());
                        RouterSetting.sRouteAddress = etRouteAddress.getText().toString();
                        mRouterSetting.loginRouter(etUserName.getEditableText().toString(),
                                etPsw.getEditableText().toString());
                        mHandler.sendEmptyMessage(MSG_SHOW_PROGRESS_DIALOG);
                    }
                });
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RouteSettingsActivity.this.finish();
                    }
                });
        builder.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // RouteSettingsActivity.this.finish();TODO
                dialog.dismiss();
            }
        });
        builder.create();
        AlertDialog al = builder.show();
        al.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        final Button btnOK = al.getButton(DialogInterface.BUTTON_POSITIVE);
        btnOK.setEnabled(canLogin(etUserName, etPsw));
        TextWatcher tw = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                btnOK.setEnabled(canLogin(etUserName, etPsw));
            }
        };
        etPsw.addTextChangedListener(tw);
    }

    private boolean canLogin(EditText et1, EditText et2) {
        String str1 = et1.getEditableText().toString();
        String str2 = et2.getEditableText().toString();
        mLogUtil.d("canLogin str1 = " + str1 + ", str2 = " + str2);
        if (FuncUtil.isNullOrEmpty(str1) || FuncUtil.isNullOrEmpty(str2)) {
            return false;
        }

        return str1.length() >= 1 && str2.length() >= 1;
    }

    @Override
    public void onRequestFinish(int requestType, RequestResult requestResult) {
        if (requestType != RouterSetting.TYPE_LOGINROUTER) {
            mLogUtil.e("onRequestFinish requestType != RouterSetting.TYPE_LOGINROUTER");
            return;
        }

        if (requestResult == null) {
            mLogUtil.e("onRequestFinish requestResult is null!");
            Message msg = new Message();
            msg.what = MSG_SHOW_LOGIN_DIALOG;
            mHandler.sendMessage(msg);
            return;
        }

        if (RequestResult.RESULT_SUCCESS.equals(requestResult.getResult())) {
            mHandler.sendEmptyMessage(MSG_DISMISS_PROGRESS_DIALOG);
        } else {
            Message msg = new Message();
            msg.what = MSG_SHOW_LOGIN_DIALOG;
            msg.obj = requestResult.getMsg();
            mHandler.sendMessage(msg);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLogUtil.d("onDestroy()");
        RouterSetting.destroyInstance();
    }

}
