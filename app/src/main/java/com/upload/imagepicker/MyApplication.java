package com.upload.imagepicker;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }

    private void init() {
        context = this;
    }

    public static Context getContext() {
        return context;
    }
}
