package com.example.yokoyama.newsviewer

class NewsResult {

    data class QueryResult(val status: String = "default",
                           val totalResults: Int = 0,
                           val articles: List<NewsEntry> = emptyList())

    data class NewsEntry (val source: ArticleSource = ArticleSource(null, null),
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