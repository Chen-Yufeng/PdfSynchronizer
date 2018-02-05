package com.ifchan.pdftest3.Utils;


import android.app.Application;
import android.content.Context;

/**
 * Created by user on 2018/1/31.
 */

public class MyApplication extends Application {
    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
    public static Context getContext() {
        return context;
    }
}