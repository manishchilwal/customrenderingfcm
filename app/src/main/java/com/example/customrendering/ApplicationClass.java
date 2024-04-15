package com.example.customrendering;

import android.app.Application;

import com.clevertap.android.sdk.ActivityLifecycleCallback;

public class ApplicationClass extends Application {
    @Override
    public void onCreate(){
        ActivityLifecycleCallback.register(this);
        super.onCreate();
    }
}
