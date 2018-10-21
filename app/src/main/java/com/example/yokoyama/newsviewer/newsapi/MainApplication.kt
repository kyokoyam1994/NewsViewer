package com.example.yokoyama.newsviewer.newsapi

import android.app.Application
import android.util.Log
import com.squareup.leakcanary.LeakCanary

class MainApplication : Application() {

    private val tag = MainApplication::class::java.name

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) return
        Log.d(tag, "Installing LeakCanary...")
        LeakCanary.install(this)
    }

}