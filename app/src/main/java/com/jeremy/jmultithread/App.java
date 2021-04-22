package com.jeremy.jmultithread;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

public class App extends Application implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();
//        Thread.setDefaultUncaughtExceptionHandler(this);


    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        Log.d(TAG, "Got an exception.", e);
    }
}
