package com.example.yokoyama.newsviewer.newsapi

import ARTICLES_PER_PAGE_DEFAULT
import ARTICLES_PER_PAGE_KEY
import MAX_ARTICLES
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.content.Context
import android.os.Parcelable
import defaultSharedPreferences
import get
import kotlinx.android.parcel.Parcelize

data class NewsResult (val status: String = "default",
                       val totalResults: Int = 0,
                       val articles: List<NewsEntry> = emptyList()) {

    data class NewsEntry (val source: ArticleSource = ArticleSource(),
                          val title: String = "",
                          val author: String = "",
                          val description: String = "",
                          val url: String = "",
                          val urlToImage: String = "",
                          val publishedAt: String = "",
                          val content: String = "")

    data class ArticleSource (val id: String? = null,
                              val name: String? = null)

    fun pageCount(context : Context) : Int {
        val pageSize = context.defaultSharedPreferences[ARTICLES_PER_PAGE_KEY, ARTICLES_PER_PAGE_DEFAULT] ?: ARTICLES_PER_PAGE_DEFAULT
        return when {
            totalResults > MAX_ARTICLES -> MAX_ARTICLES / pageSize
            totalResults == 0 || totalResults % pageSize > 0 -> totalResults / pageSize + 1
            else -> totalResults / pageSize
        }
    }

}


data class NewsSourceResult (val status: String = "default",
                             val sources: List<NewsSource> = emptyList()) {

    @Entity
    @Parcelize
    data class NewsSource (@PrimaryKey val id: String = "",
                           val name: String = "",
                           val description: String = "",
                           val url: String = "",
                           val category: String = "",
                           val language: String = "",
                           val country: String = "") : Parcelable

}

data class NewsError (val status: String = "", val code: String = "", val message: String = "default")