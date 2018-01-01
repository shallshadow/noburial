package com.zyydqpi.noburialdemo.sdk;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by zhaoyuyan on 2017/11/18.
 */

public class TopRunningActivity {
    private Activity activity;
    private final static String TAG = "TopRunningActivity";

    public void register(Activity activity) {
        this.activity = activity;
        this.activity.getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
               Log.i(TAG, "created");
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Log.i(TAG, "started");
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.i(TAG, "resumed");
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.i(TAG, "paused");
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Log.i(TAG, "stopped");
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                Log.i(TAG, "SaveInstanceState");
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.i(TAG, "Destroyed");
            }
        });
    }
}

