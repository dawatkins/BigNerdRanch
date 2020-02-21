package com.example.geoquiz

import android.app.Application
import android.content.Context

class App  : Application() {

    private val TAG = App::class.java.simpleName

    override fun onCreate() {
        super.onCreate()
    }

    override fun onTerminate() {
        super.onTerminate()
    }

    override fun getApplicationContext(): Context {
        return super.getApplicationContext()
    }


}