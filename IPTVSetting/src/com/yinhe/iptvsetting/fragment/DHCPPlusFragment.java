
package com.yinhe.iptvsetting.fragment;

import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.yinhe.iptvsetting.R;
import com.yinhe.iptvsetting.common.EditTextBgToStar;
import com.yinhe.iptvsetting.common.FuncUtil;
import com.yinhe.iptvsetting.common.LimitTextWather;
import com.yinhe.iptvsetting.common.NetManager;
import com.yinhe.iptvsetting.view.BaseFragment;

/**
 * 有线接入->电信DHCP。
 * 
 * @author zhbn
 */
public class DHCPPlusFragment extends BaseFragment {

    private static final String TAG = "TmtDHCPFragment";

    private EditText etUserName = null;
    private EditText etPassword = null;
    private Spinner mySpinner = null;
    private ContentResolver mContentResolver;
    private final String KEY_USE_CNET_IP_NEW = "staticnetwork_use_CNET_ip_dhcpcd";

    private NetManager mNetManager;

    private Button button = null;

    private int falut_value = 0;

    public DHCPPlusFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.tmt_dhcp_fragment, container,
                false);
        mActivity = getActivity();
        initData(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        etUserName.setText(mNetManager.getDHCPUserName());
        etPassword.setText(mNetManager.getDHCPPassword());
    }

    private void initData(View view) {
        mContentResolver = mActivity.getContentResolver();
        this.etUserName = (EditText) view.findViewById(R.id.et_user_name);
        this.etUserName.addTextChangedListener(new LimitTextWather(mActivity, 36));
        this.etPassword = (EditText) view.findViewById(R.id.et_net_password);
        this.etPassword.setTransformationMethod(new EditTextBgToStar());
        this.etPassword.addTextChangedListener(new LimitTextWather(mActivity, 16));

        mNetManager = NetManager.getInstance();
        etUserName.setText(mNetManager.getDHCPUserName());
        etPassword.setText(mNetManager.getDHCPPassword());

        this.mySpinner = (Spinner) view.findViewById(R.id.spinner);
        final TextView tvComment = (TextView) view
                .findViewById(R.id.txt_comment);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                mActivity, R.array.authentication,
                R.layout.yinhe_spinner_item);// 外面显示的
        adapter.setDropDownViewResource(R.layout.yinhe_spinner_dropdown_item); // 改里面的值
        this.mySpinner.setAdapter(adapter);
        this.mySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                    int positon, long arg3) {
                Log.d(TAG, "mySpinner onItemSelected" + positon);

                falut_value = positon;

                tvComment.setText(getResources().getStringArray(
                        R.array.authentication)[1 - positon]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        this.mySpinner.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mySpinner.performClick();
                }
            }
        });

        if (Settings.Secure.getInt(mContentResolver, KEY_USE_CNET_IP_NEW, 0) == 1) {
            this.mySpinner.setSelection(0);
        } else {
            this.mySpinner.setSelection(1);
        }

        button = (Button) view.findViewById(R.id.bt_sure);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!mNetManager.isEthernetStateEnabled()) {
                    FuncUtil.showToast(mActivity,
                            R.string.warn_wire_disconnected);
                    mActivity.findViewById(R.id.button_on).requestFocus();
                    return;
                }

                if (!mNetManager.isEthernetOn()) {
                    FuncUtil.showToast(mActivity, R.string.eth_phy_link_down_check);
                    return;
                }

                String userName = etUserName.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (FuncUtil.isNullOrEmpty(userName)
                        || FuncUtil.isNullOrEmpty(password)) {
                    FuncUtil.showToast(mActivity, R.string.please_iuput_righr);
                    return;
                }

                // 打开DHCP
                mNetManager.startDhcpPlus(userName, password);
                FuncUtil.showToast(mActivity, R.string.info_save_setting);

                reObtainNetAddress(OnNetTypeChangeListener.DHCP_PLUS_ON);

                if (falut_value == 0) {
                    Secure.putInt(mContentResolver, KEY_USE_CNET_IP_NEW, 1);
                } else {
                    Secure.putInt(mContentResolver, KEY_USE_CNET_IP_NEW, 0);
                }

                mActivity.findViewById(R.id.tv_DHCP).requestFocus();
            }
        });

        Button btnCancle = (Button) view.findViewById(R.id.bt_canle);
        btnCancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mNetManager.stopDhcpPlus(mNetManager.getDHCPUserName(),
                        mNetManager.getDHCPPassword());
                FuncUtil.showToast(mActivity, R.string.dhcp_plus_disconnected);
                mActivity.findViewById(R.id.tv_DHCP).requestFocus();
            }
        });
    }
}
