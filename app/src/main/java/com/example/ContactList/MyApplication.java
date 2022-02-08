package com.example.ContactList;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyApplication extends Application
{
    private static Context context;
    public static ExecutorService executorService = Executors.newFixedThreadPool(1);
    public static Handler mainHandler = HandlerCompat.createAsync(Looper.getMainLooper());
    public void onCreate()
    {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }
    public static Context getAppContext() {
        return MyApplication.context;
    }

}
