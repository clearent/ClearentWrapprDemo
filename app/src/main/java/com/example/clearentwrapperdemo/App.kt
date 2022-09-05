package com.example.clearentwrapperdemo

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.clearent.idtech.android.wrapper.SDKWrapper

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // We initialize the sdk
        initSdk()
    }

    private fun initSdk() =
        SDKWrapper.initializeReader(
            applicationContext,
            Constants.BASE_URL_SANDBOX,
            Constants.PUBLIC_KEY_SANDBOX,
            Constants.API_KEY_SANDBOX
        )
}