
package com.yinhe.iptvsetting.common;

public interface IRouterSetting {
    public static final String DEFAULT_ROUTE_ADDRESS = "192.168.125.1";
    public static final String COMMAND_PROTOCOL = "http://";
    public static final String COMMAND_PORT = ":80/app/";
    public static final String COMMAND_LOGINROUTER = "loginRouter?username=%1$s&passwd=%2$s";
    public static final String COMMAND_SETINTERNET = "setInternet?type=%1$s";
    public static final String COMMAND_SETNETWORK = "setNetwork?Ipaddress=%1$s&Mask=%2$s";
    public static final String COMMAND_SETWIFI = "setWifi?ssid=%1$s&pwd=%2$s";
    public static final String COMMAND_GETLOGINNAME = "getLoginName";
    public static final String COMMAND_GETACTIVESTATE = "getActiveState";
    public static final String COMMAND_SETLOGINPWD = "setLoginPwd?oldpasswd=%1$s&newpasswd=%2$s";
    public static final String COMMAND_RESTART = "restart";
    public static final String COMMAND_RESTORE = "restore";
    public static final String COMMAND_GETREGISTERINFO = "getRegisterInfo";

    public static final String JSON_RESULT = "result";
    public static final String JSON_MSG = "msg";
    public static final String JSON_DATA = "";

    public final class InternetType {
        private InternetType() {
        };

        public static final String DHCP = "1";
        public static final String PPPOE = "2";
        public static final String MANUAL = "3";
    }

    /**
     * 5.2.1登录路由器
     */
    public void loginRouter(String userName, String passwd);

    /**
     * 5.2.2设置上网方式
     */
    public void setInternet(String type, String ipAddress, String mask, String gateWay, String dns,
            String userName, String passwd);

    /**
     * 5.2.3设置路由器内网
     */
    public void setNetwork(String ipAddress, String mask, String gateWay);

    /**
     * 5.2.4设置无线参数
     */
    public void setWifi(String ssid, String pwd, String name, String channel);

    /**
     * 5.2.5获取路由器登录名
     */
    public void getLoginName();

    /**
     * 5.2.6获取路由器激活状态
     */
    public void getActiveState();

    /**
     * 5.2.7设置路由器登录密码
     */
    public void setLoginPwd(String oldPasswd, String newPasswd);

    /**
     * 5.2.8重启路由器
     */
    public void restart();

    /**
     * 5.2.9恢复出厂设置
     */
    public void restore();

    /**
     * 5.2.10获取路由设备激活信息
     */
    public void getRegisterInfo();
}
