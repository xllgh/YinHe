
package com.yinhe.iptvsetting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yinhe.iptvsetting.R;
import com.yinhe.iptvsetting.common.FuncUtil;

/**
 * 服务器信息。
 * 
 * @author zhbn
 */
public class TelnetInforActivity extends Activity {

    private EditText tvUpdataServiecerAddress = null;

    private final String UPDATE_URL = "update_url";
    private Button myButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.telnet_infor);

        initData();
    }

    private void initData() {

        this.tvUpdataServiecerAddress = (EditText) super
                .findViewById(R.id.tv_updata_serviecer_address);

        myButton = (Button) super.findViewById(R.id.bt_update);
        myButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                String str = tvUpdataServiecerAddress.getText().toString();

                if (!FuncUtil.isNullOrEmpty(str)) {
                    Toast.makeText(TelnetInforActivity.this,
                            R.string.info_save_success, Toast.LENGTH_SHORT)
                            .show();
                    Secure.putString(getContentResolver(), UPDATE_URL, str);
                    Intent inent = new Intent(
                            "com.network.systemupgrade.action.UPGRADE_START");
                    sendBroadcast(inent);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.tvUpdataServiecerAddress.setText(Secure.getString(
                getContentResolver(), UPDATE_URL));
    }

}
