package com.jaderalcantara.marvel.infra

import android.app.Application
import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MarvelApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this
        WifiService.instance.initializeWithApplicationContext(this)
        startKoin {
            androidContext(this@MarvelApplication)
            modules(KoinModules.appModule)
        }
    }

    companion object{
        lateinit var context: Context
    }
}