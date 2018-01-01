
package com.zyydqpi.noburialdemo.sdk;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import com.zyydqpi.noburialdemo.TestService;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhaoyuyan on 2018/1/1.
 */

public class TopRunningApp {
    private Activity activity = null;
    private final static String TAG = "TopRunningApp";
    private final Timer timer = new Timer();
    private TimerTask timerTask;

    // 在Activity中，我们通过ServiceConnection接口来取得建立连接与连接意外丢失的回调
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 建立连接
            // 获取服务的操作对象
            TestService.TestBinder binder = (TestService.TestBinder) service;
            ActivityManager activityManager = (ActivityManager) binder.getService().getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
            ComponentName cn = activityManager.getRunningTasks(1).get(0).topActivity;
            Log.i(TAG, "Connected.");
            Log.i(TAG, cn.getClassName());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // 连接断开
            Log.i(TAG, "Connection close.");
        }
    };

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            getTopApp(activity);
            super.handleMessage(msg);
        }
    };

    /**
     * 注册需要监听的activity
     * @param activity
     */
    public void register(Activity activity) {
        this.activity = activity;

        // check permission
        if (!hasPermission(this.activity)) {
            Log.i(TAG, "Request permission");
            //若用户未开启权限，则引导用户开启“Apps with usage access”权限
            this.activity.startActivityForResult(
                    new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                    MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
        }

        // bind service
        Intent intent = new Intent(activity, TestService.class);
        this.activity.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        Log.i(TAG, "Bind Service.");

        timerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                handler.handleMessage(message);
            }
        };
        timer.schedule(timerTask, 2000, 3000);
    }

    private void getTopApp(Context context) {
        Log.i(TAG, "TopRunningActivity VERSION : " + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager m = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            if (m != null) {
                long now = System.currentTimeMillis();
                //获取60秒之内的应用数据
                List<UsageStats> stats = m.queryUsageStats(UsageStatsManager.INTERVAL_BEST, now - 60 * 1000, now);
                Log.i(TAG, "Running app number in last 60 seconds : " + stats.size());
                String topActivity = "";
                //取得最近运行的一个app，即当前运行的app
                if ((stats != null) && (!stats.isEmpty())) {
                    int j = 0;
                    for (int i = 0; i < stats.size(); i++) {
                        if (stats.get(i).getLastTimeUsed() > stats.get(j).getLastTimeUsed()) {
                            j = i;
                        }
                    }
                    topActivity = stats.get(j).getPackageName();
                }
                Log.i(TAG, "top running app is : " + topActivity);
            }
        }
    }

    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 1101;

    private boolean hasPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager)
                context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), context.getPackageName());
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    public void close() {
        Log.i(TAG, "Close connection.");
        timer.cancel();
        this.activity.unbindService(serviceConnection);
    }
}
