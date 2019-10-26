package com.cere.csplayer;

import android.app.Application;
import android.content.Context;

/**
 * Created by CheRevir on 2019/10/19
 */
public class App extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
