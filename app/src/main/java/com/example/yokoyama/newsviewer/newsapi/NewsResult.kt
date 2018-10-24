package com.example.yokoyama.newsviewer.newsapi

import com.example.yokoyama.newsviewer.MAX_ARTICLES
import com.example.yokoyama.newsviewer.PAGE_SIZE

data class NewsResult (val status: String = "default",
                       val totalResults: Int = 0,
                       val articles: List<NewsEntry> = emptyList()) {

    val pageCount
        get() = when {
            totalResults > MAX_ARTICLES -> MAX_ARTICLES / PAGE_SIZE
            totalResults == 0 || totalResults % PAGE_SIZE > 0 -> totalResults / PAGE_SIZE + 1
            else -> totalResults / PAGE_SIZE
        }

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

}

data class NewsError (val status: String = "", val code: String = "", val message: String = "default")