package net.kuisec.r8c.ipc;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

public class PluginManager implements ServiceConnection {

    public static final String TAG = "ServiceManager";
    private final String connectionName;
    private final Context context;

    public Messenger messenger;

    public PluginManager(String connectionName, Context context) {
        this.connectionName = connectionName;
        this.context = context;
    }


    /**
     * 服务绑定
     * @param componentName 组件名称
     * @param iBinder IBinder
     */
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        messenger = new Messenger(iBinder);
        String log = "服务绑定成功";
        Log.d(TAG, connectionName + log);
        Toast.makeText(context, connectionName + log, Toast.LENGTH_SHORT).show();
    }


    /**
     * 服务断开绑定
     * @param componentName 组件名称
     */
    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        String log = "服务断开绑定";
        Log.d(TAG, connectionName + log);
        Toast.makeText(context, connectionName + log, Toast.LENGTH_SHORT).show();
    }
}
