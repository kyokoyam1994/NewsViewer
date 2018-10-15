package com.example.yokoyama.newsviewer.newsapi

import com.example.yokoyama.newsviewer.BuildConfig
import com.fasterxml.jackson.databind.ObjectMapper
import java.net.HttpURLConnection
import java.net.URL

abstract class NewsQuery (open val country: String = "",
                          open val category: String = "",
                          open val q: String = "",
                          open val page: Int = 1) {

    abstract val endpoint : String

    fun query() : NewsResult? {
        val url = URL(endpoint)
        val connection = url.openConnection() as HttpURLConnection
        var queryResult : NewsResult? = null
        connection.setRequestProperty("X-Api-Key", BuildConfig.News_API_Key)

        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            val mapper = ObjectMapper()
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            queryResult = mapper.readValue<NewsResult>(response, NewsResult::class.java)

            println(queryResult.status)
            println(queryResult.totalResults)
            println(queryResult.articles.size)
            println("Success!\n$response")
        } else {
            val text = connection.errorStream.buffered().reader().use { reader -> println(reader.readText()) }
            println("${connection.responseCode} , ${connection.responseMessage}")
        }

        return queryResult
    }

}