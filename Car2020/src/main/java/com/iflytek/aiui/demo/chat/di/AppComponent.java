package com.iflytek.aiui.demo.chat.di;

import android.app.Application;

import com.test.xander.carplay.ChatApp;
import com.test.xander.carplay.MainActivity;
import com.test.xander.carplay.MainActivityModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

/**
 * Dagger2 Component入口
 */

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        AppModule.class,
        MainActivityModule.class
})

public interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);
        AppComponent build();
    }
    void inject(ChatApp application);
}
