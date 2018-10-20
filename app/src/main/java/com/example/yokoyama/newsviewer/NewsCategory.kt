package com.example.yokoyama.newsviewer

sealed class NewsCategory {
    object Everything : NewsCategory()
    class TopHeadlines(val topHeadlinesCategory: TopHeadlinesCategory) : NewsCategory()
}

enum class TopHeadlinesCategory (val category: String) {
    BUSINESS("business"),
    ENTERTAINMENT("entertainment"),
    GENERAL("general"),
    HEALTH("health"),
    SCIENCE("science"),
    SPORTS("sports"),
    TECHNOLOGY("technology")
}