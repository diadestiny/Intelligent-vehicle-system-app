package com.test.xander.carplay;

import android.app.Activity;
import android.app.Application;
import android.content.Context;



import com.iflytek.aiui.demo.chat.di.AppComponent;
import com.iflytek.aiui.demo.chat.di.DaggerAppComponent;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;


public class ChatApp extends Application implements HasActivityInjector {
    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;
    AppComponent mAppComponent;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppComponent = DaggerAppComponent
                .builder()
                .application(this)
                .build();
        mAppComponent
                .inject(this);
        context = getApplicationContext();
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }

    public static Context getContext() {
        return context;
    }
}
