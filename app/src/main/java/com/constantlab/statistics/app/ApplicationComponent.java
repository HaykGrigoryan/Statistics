package com.constantlab.statistics.app;

import com.constantlab.statistics.network.NetworkModule;
import com.constantlab.statistics.utils.PreferencesManager;

import javax.inject.Singleton;

import dagger.Component;
import retrofit2.Retrofit;

/**
 * Created by Sunny Kinger on 04-12-2017.
 */

@Singleton
@Component(modules = {ApplicationModule.class, NetworkModule.class})
public interface ApplicationComponent {

    PreferencesManager providePreferencesManager();

    Retrofit provideRetrofit();
}
