package com.example.got2go

import android.app.Application
import timber.log.Timber


class G2GApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
