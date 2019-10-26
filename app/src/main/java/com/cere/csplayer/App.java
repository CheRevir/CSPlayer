package com.cere.csplayer;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cere.csplayer.service.PlayService;

import java.util.ArrayList;

/**
 * Created by CheRevir on 2019/10/19
 */
public class App extends Application {
    private static Context context;
    private ArrayList<Activity> mActivities;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
        if ("com.cere.csplayer".equals(getCurrentProcessName())) {
            startService(new Intent(this, PlayService.class));
            mActivities = new ArrayList<>();
            registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks());
        }
    }

    public static Context getContext() {
        return context;
    }

    private class ActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            mActivities.add(activity);
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {

        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {

        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {

        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
            mActivities.remove(activity);
            if (mActivities.size() == 0) {
                App.this.stopService(new Intent(App.this, PlayService.class));
            }
        }
    }


    private String getCurrentProcessName() {
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
            if (process.pid == pid) {
                processName = process.processName;
            }
        }
        return processName;
    }
}
