package net.kuisec.r8c.Utils;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.SystemClock;


/**
 * WiFi工具类
 * 用于获取WiFi各种信息
 *
 * @author Jinsn
 */
public class WifiUtil {

    private static WifiManager wifiManager;
    private static boolean init = false;

    /**
     * 初始化WiFi工具类
     *
     * @param context 上下文
     */
    public static void init(Context context) {
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        init = true;
    }

    /**
     * wifi地址解析
     * 将一串数字解析为日常看到的ipv4地址
     *
     * @param address wifi地址，为一串数字
     * @return 返回处理得到的网关信息
     */
    private static String wifiAddress(int address) {
        return (address & 0xff) + "." + (address >> 8 & 0xff) + "." + (address >> 16 & 0xff) + "." + (address >> 24 & 0xff);
    }

    /**
     * 获得wifi网关地址
     */
    public static String getWifiGateWay() {
        if (init) {
            String gateway = null;
            if (wifiManager != null) {
                DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
                int gatewayInfo = wifiManager.getDhcpInfo().gateway;
                gateway = wifiAddress(gatewayInfo);
            } else {
                gateway = "0.0.0.0";
            }
            return gateway;
        } else {
            return "0.0.0.0";
        }
    }

    /**
     * 获取wifi信息
     *
     * @return 返回WiFiInfo对象
     */
    private static WifiInfo getWifiInfo() {
        return (wifiManager != null) ? wifiManager.getConnectionInfo() : null;
    }

    /**
     * 得到wifi名称
     *
     * @return 返回WiFiName
     */
    public static String getWifiSsid() {
        if (init) {
            WifiInfo wifiInfo = getWifiInfo();
            String ssid = null;
            if (wifiInfo != null) {
                ssid = wifiInfo.getSSID();
            } else {
                ssid = "\"未连接\"";
            }
            return ssid.substring(1, ssid.length() - 1);
        } else {
            return "\"未连接\"";
        }
    }

    /**
     * wifi状态监听器
     */
    public static void wifiStateListener() {
        if (init) {
            ThreadUtil.createThread(() -> {
                //初始化wifi名称
                String ssid;
                String ssidTemp;
                boolean bkrcFlag;
                //wifi网关标志
                boolean wifiGateWayFlag;
                //wifi状态标志
                boolean wifiStateFlag;
                //是否是空wifi名称
                boolean wifiSsidEmpty;
                while (true) {
                    ssid = getWifiSsid();
                    wifiSsidEmpty = "unknown ssid".equals(ssid);
                    wifiGateWayFlag = "0.0.0.0".equals(getWifiGateWay());
                    //WiFi名称辨识
                    if (ssid.length() >= 4) {
                        if ("BKRC".equals(ssid.substring(0, 4))) {
                            ssidTemp = ssid + "(竞赛平台)";
                            bkrcFlag = true;
                        } else {
                            if (wifiSsidEmpty) {
                                ssidTemp = "未连接WiFi";
                            } else {
                                ssidTemp = ssid + "(非竞赛平台)";
                            }
                            bkrcFlag = false;
                        }
                    } else {
                        ssidTemp = ssid + "(非竞赛平台)";
                        bkrcFlag = false;
                    }
                    //wifi绿标控制
                    wifiStateFlag = wifiGateWayFlag || "未连接".equals(ssid) || wifiSsidEmpty || !bkrcFlag;
                    if (!wifiStateFlag) {
                        //wifi连接成功时
                        HandlerUtil.sendMsg(HandlerUtil.WIFI_STATE_FLAG, HandlerUtil.WIFI_OPEN, ssidTemp);
                    } else {
                        HandlerUtil.sendMsg(HandlerUtil.WIFI_STATE_FLAG, HandlerUtil.WIFI_CLOSE, ssidTemp);
                    }
                    //线程休眠一秒
                    SystemClock.sleep(1500);
                }
            });
        }
    }
}
