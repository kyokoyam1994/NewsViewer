package com.example.yokoyama.newsviewer.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.example.yokoyama.newsviewer.newsapi.NewsSourceResult
import android.arch.persistence.room.OnConflictStrategy





@Dao
interface NewsSourceDao {

    @Query("SELECT * FROM NewsSource")
    fun getAll() : List<NewsSourceResult.NewsSource>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(blockedContacts: List<NewsSourceResult.NewsSource>)

    @Query("DELETE FROM NewsSource")
    fun deleteAll()

}