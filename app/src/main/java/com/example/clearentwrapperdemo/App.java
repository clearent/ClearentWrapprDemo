package com.example.clearentwrapperdemo;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.clearent.idtech.android.wrapper.ClearentWrapper;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        initSdkWrapper();
    }

    private void initSdkWrapper() {
        ClearentWrapper.INSTANCE.initializeSDK(
                getApplicationContext(),
                Constants.BASE_URL_SANDBOX,
                Constants.PUBLIC_KEY_SANDBOX,
                Constants.API_KEY_SANDBOX,
                true // enable enhanced messages
        );
    }
}
