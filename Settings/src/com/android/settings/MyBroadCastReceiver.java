package com.android.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.os.SystemProperties;

import android.content.SharedPreferences;
import com.hisilicon.android.netshare.NativeSamba;

public class MyBroadCastReceiver extends BroadcastReceiver {

    private NativeSamba samba;
    static final String SERVER_USER = "administrator";
    private static final String SAMBA_STATUS = "samba_status";
    private static final String SAMBA_STATUS_DEF_VALUE= "true";
    public static final String BUTTON_GROUP_KEY = "group_edittext";
    private static final String DEFAULT_WORK = "WORKGROUP";
    public static final String KEY_SERVER_USER_MODE = "server_user_mode";
    static final String SERVER_SHARE_MODE = "share";
    static final String SHARED_FILE_NAME = "share";
    public static final String BUTTON_CHANGE_PASS_KEY = "button_change_pass_key";
    static final String BLANK = "";
    public static final String KEY_ENABLE_PASSWORD = "password_enabled";
    public static final String KEY_WRITEABLE = "chkbox_writeable";
    public static final int LOG_NUM = 225;
    static final String CAN_WRITE = "yes";
    static final String CAN_NOT_WRITE = "no";
    private String nCanWrite = CAN_NOT_WRITE;
    static final String SHARED_DIRECTORY = "/mnt";
    private static final String FILE_PERMISSION = "0777";
    private static final String DIRECTORY_PERMISSION = "0777";
    private static final String AVAIL = "yes";
    private static final String BROWSE = "yes";

    @Override
    public void onReceive(Context context, Intent intent) {
        // For Intent.ACTION_BOOT_COMPLETED processing
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.e("Settings", "MyBroadCastReceiver - onReceive Intent.ACTION_BOOT_COMPLETED!");
            // MediaServer Setting for MediaPlayer choices
            SharedPreferences p = context.getSharedPreferences("com.android.settings_preferences", 0);
            String is_enable = p.getString(MediaSettings.PLAYER_KEY, "1");
            Log.e("Settings", "MyBroadCastReceiver - MEDIA_HIPLAYER_ENABLE - " + is_enable);

            // 2 - set 'media.enable.hiplayer' property for the system
            SystemProperties.set(MediaSettings.MEDIA_HIPLAYER_ENABLE, is_enable.equals("1") ? "true" : "false");

            // SAMBA_STATUS
            //SharedPreferences p = context.getSharedPreferences("com.android.settings_preferences", 0);
            String sambaState = p.getString(SAMBA_STATUS,SAMBA_STATUS_DEF_VALUE);

            if ("true".equals(sambaState) && SystemProperties.get("persist.sys.samba.status","true").equals("true")) {
                String password = p.getString(BUTTON_CHANGE_PASS_KEY, BLANK);
                boolean check = p.getBoolean(KEY_ENABLE_PASSWORD, false);

                samba = new NativeSamba();
                samba.setUser(SERVER_USER);

                if (check) {
                samba.setPasswd(password);
                }
                String group = p.getString(BUTTON_GROUP_KEY, DEFAULT_WORK);
                String server_info = p.getString(KEY_SERVER_USER_MODE, SERVER_SHARE_MODE);
                samba.setGlobal(group, BLANK, server_info, LOG_NUM, BLANK);

                boolean isWriteable = p.getBoolean(KEY_WRITEABLE, false);
                if (isWriteable) {
                    nCanWrite = CAN_NOT_WRITE;
                } else {
                    nCanWrite = CAN_WRITE;
                }
                /*Get all the configuration information*/
                String heads = samba.getProperty();
                /*Not share information,Increase,Have share information,read and write edit*/
                if (null == heads) {
                    int fg1 = samba.addProperty(SHARED_FILE_NAME, SHARED_DIRECTORY,
                        FILE_PERMISSION, DIRECTORY_PERMISSION, AVAIL, BROWSE,
                        nCanWrite, SERVER_USER);
                } else {
                    int fg2 = samba.editShare(SHARED_FILE_NAME, SHARED_DIRECTORY,
                        FILE_PERMISSION, DIRECTORY_PERMISSION, AVAIL, BROWSE,
                        nCanWrite, SERVER_USER);
                }
                samba.startSambaServer();
            }
        }
    }
}
