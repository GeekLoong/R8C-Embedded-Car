package net.kuisec.r8c.Network;

import android.util.Log;

import net.kuisec.r8c.Utils.LogUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CameraCmdUtil {
    private static final String TAG = "摄像头控制台";
    private static String IP = "0.0.0.0";
    private final static long TIMEOUT = 5000;
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
            //call不超时
            .callTimeout(0, TimeUnit.MILLISECONDS)
            .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
            .build();
    private static final byte[] sendData = new byte[]{68, 72, 1, 1};
    private static DatagramSocket socket = null;
    private static final byte[] receiveData = new byte[1024];


    /**
     * 获取过滤的 IP 地址
     *
     * @return 返回 IP 地址
     */
    public static String getIP() {
        return IP;
    }


    /**
     * 更新摄像头IP
     */
    public static void updateIP() {
        IP = "0.0.0.0";
        try {
            //封装网关
            InetAddress local = InetAddress.getByName("255.255.255.255");
            if (socket != null) {
                socket.close();
                socket = null;
            }
            socket = new DatagramSocket(3565);
            DatagramPacket send = new DatagramPacket(sendData, sendData.length, local, 8600);
            DatagramPacket receive = new DatagramPacket(receiveData, receiveData.length);
            socket.send(send);
            socket.receive(receive);
            String ip = new String(receiveData, 0, receive.getLength(), StandardCharsets.UTF_8).trim();
            StringBuilder newIP = new StringBuilder();
            if (ip.contains("DH")) {
                ip = ip.replace("DH", "");
                String[] bytes = ip.split("\\.");
                for (int i = 0; i < 4; i++) {
                    if (bytes[i].length() > 3) {
                        bytes[i] = bytes[i].replace("255", "");
                    }
                    if (i == 3) {
                        newIP.append(bytes[i]);
                    } else {
                        newIP.append(bytes[i]).append(".");
                    }
                }
            }
            LogUtil.printLog("摄像头IP", newIP.toString());
            if (socket != null) {
                socket.close();
            }
            socket = null;
            IP = newIP.toString().trim() + ":81";
        } catch (UnknownHostException e) {
            Log.e(TAG, "没有找到WIFI网关");
        } catch (SocketException e) {
            Log.e(TAG, "与摄像头建立连接失败");
        } catch (IOException e) {
            Log.d(TAG, "与摄像头互动失败");
        }
    }


    /**
     * 更新图像
     *
     * @param callback 异步回调
     */
    public static void updateImage(Callback callback) {
        Request request = new Request.Builder()
                .url("http://" + IP + "/videostream.cgi?user=admin&pwd=888888")
                .build();
        client.newCall(request).enqueue(callback);
    }


    /**
     * 摄像头控制指令
     *
     * @param cmd 指令
     * @return 返回摄像头操作指令
     */
    public static int cameraAdjust(int cmd) {
        if (!"0.0.0.0".equals(IP)) {
            Request request = new Request.Builder()
                    .url("http://" + IP + "/decoder_control.cgi?loginuse=admin&loginpas=888888&" + "command=" + cmd + "&onestep=1")
                    .build();
            try (Response response = client.newCall(request).execute()) {
                return response.code();
            } catch (IOException e) {
                Log.e(TAG, "摄像头调整位置失败");
            }
        }
        return 443;
    }
}
