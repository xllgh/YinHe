
package com.yinhe.iptvsetting;

import com.yinhe.iptvsetting.common.NetManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NTPBroadCastReceiver extends BroadcastReceiver {

    private static final String TAG = "YHSetting-BroadCastReceiver";
    private static final String NTP_SERVER = "ntp_server";
    private static final String PUT_NTPSERVER = "com.yinhe.iptvsetting.NTPBroadCastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        String action = intent.getAction();
        if (PUT_NTPSERVER.equals(action)) {
            Log.d(TAG, "onReceive " + PUT_NTPSERVER);
            Intent startServiceIntent = new Intent(context, NTPservice.class);
            startServiceIntent.putExtra(NTP_SERVER,
                    intent.getStringExtra(NTP_SERVER));
            context.startService(startServiceIntent);
        }

        if (NetManager.PPPOE_STATE_CHANGED_ACTION.equals(action)) {
            int message = intent.getIntExtra(
                    NetManager.EXTRA_PPPOE_STATE, -1);
            if (message == NetManager.EVENT_CONNECT_SUCCESSED) {
                Log.d(TAG, "onReceive Pppoe connect successed.");
                Intent newIntent = new Intent();
                newIntent.setAction("com.android.ihome.action.pppoe");
                newIntent.putExtra("pppoe", "true");
                context.sendBroadcast(newIntent);
            } else if (message == NetManager.EVENT_DISCONNECT_SUCCESSED) {
                Log.d(TAG, "onReceive Pppoe disconnect successed.");
                Intent disconnectIntent = new Intent();
                disconnectIntent.setAction("com.android.ihome.action.pppoe");
                disconnectIntent.putExtra("pppoe", "false");
                context.sendBroadcast(disconnectIntent);
            }
        }
    }

}
