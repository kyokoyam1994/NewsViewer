package com.example.yokoyama.newsviewer.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.example.yokoyama.newsviewer.newsapi.NewsSourceResult


@Database(entities = [NewsSourceResult.NewsSource::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun newsSourceDao() : NewsSourceDao

    companion object {
        private var instance : AppDatabase? = null
        fun getInstance(context: Context) : AppDatabase? {
            if (instance == null) {
                instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "news_viewer_db").build()
            }
            return instance
        }
    }

}