package com.iflytek.aiui.demo.chat.di;


import com.iflytek.aiui.demo.chat.ui.chat.ChatFragment;

import com.iflytek.aiui.demo.chat.ui.settings.SettingsFragment;


import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Dagger2 Fragment Module
 */

@Module
public abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract ChatFragment contributesChatFragment( );

    @ContributesAndroidInjector
    abstract SettingsFragment contributeSettingFragment( );

}
