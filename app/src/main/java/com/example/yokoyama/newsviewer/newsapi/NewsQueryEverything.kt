package com.example.yokoyama.newsviewer.newsapi

class NewsQueryEverything (override val country: String = "",
                           override val category: String = "",
                           override val q: String = "",
                           override val page: Int = 1,
                           val from: String = "",
                           val to: String = "",
                           val language: String = "",
                           val sortBy: String = "") : NewsQuery(country, category, q, page) {

    override val endpoint: String = "https://newsapi.org/v2/top-headlines?country=us"

}