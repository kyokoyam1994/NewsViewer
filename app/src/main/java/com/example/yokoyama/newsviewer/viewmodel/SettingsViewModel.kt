package com.example.yokoyama.newsviewer.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.example.yokoyama.newsviewer.database.AppDatabase
import com.example.yokoyama.newsviewer.newsapi.NewsSourceResult

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    val filteredSources: LiveData<List<NewsSourceResult.NewsSource>> = AppDatabase.getInstance(application)?.newsSourceDao()!!.getAllLiveData()

}