
package com.yinhe.iptvsetting.common;

public final class ConstantInfo {

    private ConstantInfo() {
    }

    public static final class ViewAction {

        private ViewAction() {
        }

        public static final String VIEW_ACTION_ETHERNET = "com.jstelcom.settings.view.ethernet";
        public static final String VIEW_ACTION_WIRELESS = "com.jstelcom.settings.view.wireless";
        public static final String VIEW_ACTION_TETHER = "com.jstelcom.settings.view.tether";
        public static final String VIEW_ACTION_BLUETOOTH = "com.jstelcom.settings.view.bluetooth";
        public static final String VIEW_ACTION_RESOLUTION = "com.jstelcom.settings.view.resolution";
        public static final String VIEW_ACTION_BOUNDARY = "com.jstelcom.settings.view.boundary";
        public static final String VIEW_ACTION_APPLICATION = "com.jstelcom.settings.view.application";
        public static final String VIEW_ACTION_SENIOR_SET = "com.jstelcom.settings.view.seniorset";
        public static final String VIEW_ACTION_ABOUT = "com.jstelcom.settings.view.about";
    }

    public static final class NetWork {

        private NetWork() {
        }

        /*
         * 有线接入方式：自动获取、手动设置、PPPoE拨号
         */
        public static final String ETHERNET_MODE_DHCP = "dhcp";
        public static final String ETHERNET_MODE_MANUAL = "manual";
        public static final String ETHERNET_MODE_PPPOE = "pppoe";
        public static final String ETHERNET_MODE_NONE = "none";

        public static final String DEFAULT_DNS = "0.0.0.0";

        // EthernetManager.ETHERNET_STATE_CHANGED_ACTION;
        public static final String ETHERNET_STATE_CHANGED_ACTION = "android.net.ethernet.ETHERNET_STATE_CHANGE";
        public static final String EXTRA_ETHERNET_STATE = "ethernet_state";// EthernetManager.EXTRA_ETHERNET_STATE;
        public static final int EVENT_PHY_LINK_UP = 18;// EthernetManager.EVENT_PHY_LINK_UP;
        public static final int EVENT_PHY_LINK_DOWN = 19;// EthernetManager.EVENT_PHY_LINK_DOWN;
        public static final int EVENT_DHCP_CONNECT_SUCCESSED = 10;// EthernetManager.EVENT_DHCP_CONNECT_SUCCESSED;
        public static final int EVENT_DHCP_CONNECT_FAILED = 11;// EthernetManager.EVENT_DHCP_CONNECT_FAILED;
        public static final int EVENT_STATIC_CONNECT_SUCCESSED = 14;// EthernetManager.EVENT_STATIC_CONNECT_SUCCESSED;
        public static final int EVENT_STATIC_CONNECT_FAILED = 15;// EthernetManager.EVENT_STATIC_CONNECT_FAILED;

        public static final String PPPOE_STATE_CHANGED_ACTION = "PPPOE_STATE_CHANGED";// PppoeManager.PPPOE_STATE_CHANGED_ACTION;
        public static final String EXTRA_PPPOE_STATE = "pppoe_state";// PppoeManager.EXTRA_PPPOE_STATE;
        public static final int EVENT_PPPOE_CONNECT_SUCCESSED = 0;// PppoeManager.EVENT_CONNECT_SUCCESSED;
        public static final int EVENT_PPPOE_CONNECT_FAILED = 1;// PppoeManager.EVENT_CONNECT_FAILED;
        public static final int EVENT_PPPOE_CONNECT_FAILED_AUTH_FAIL = 2;// PppoeManager.EVENT_CONNECT_FAILED_AUTH_FAIL;
        public static final int EVENT_PPPOE_CONNECTING = 3;// PppoeManager.EVENT_CONNECTING;
        public static final int EVENT_PPPOE_DISCONNECT_SUCCESSED = 4;// PppoeManager.EVENT_DISCONNECT_SUCCESSED;
        public static final int EVENT_PPPOE_DISCONNECT_FAILED = 5;// PppoeManager.EVENT_DISCONNECT_FAILED;
        public static final int EVENT_PPPOE_AUTORECONNECTING = 6;// PppoeManager.EVENT_AUTORECONNECTING
    }
    
    public static final class Display {

        private Display() {
        }
        
        public static final int RET_FLAG_RESOLUTION_SUPPORT = 0;
        public static final int RET_FLAG_RESOLUTION_UNSUPPORT = -1;
        
        public static final int RESOLUTION_TYPE_AUTO = 0;
        public static final int RESOLUTION_TYPE_2K_30 = 1;
        public static final int RESOLUTION_TYPE_2K_25 = 2;
        public static final int RESOLUTION_TYPE_1080P_60 = 3;
        public static final int RESOLUTION_TYPE_1080P_50 = 4;
        public static final int RESOLUTION_TYPE_720P_60 = 5;
        public static final int RESOLUTION_TYPE_720P_50 = 6;
        
        public static final int RET_FLAG_VIDEO_RATIO_FAILED = -1;
        public static final int VIDEO_RATIO_AUTO = 0;
        public static final int VIDEO_RATIO_FULL_SCREEN = 1;
        
    }

}
