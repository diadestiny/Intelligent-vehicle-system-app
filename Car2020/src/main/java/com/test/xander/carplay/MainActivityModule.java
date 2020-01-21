package com.test.xander.carplay;

import com.iflytek.aiui.demo.chat.di.FragmentBuildersModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainActivityModule {
    @ContributesAndroidInjector(modules = {FragmentBuildersModule.class})
    public abstract MainActivity contributesChatActivity();
}
