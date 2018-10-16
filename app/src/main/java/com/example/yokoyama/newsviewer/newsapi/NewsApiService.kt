package com.example.yokoyama.newsviewer.newsapi

import com.example.yokoyama.newsviewer.BuildConfig
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query



val BASE_URL: String = "https://newsapi.org"

interface NewsApiService {

    @Headers("X-Api-Key: ${BuildConfig.News_API_Key}")
    @GET("/v2/top-headlines")
    fun topHeadlines(@Query("country") country : String?,
                     @Query("category") category : String?,
                     @Query("q") q : String?,
                     @Query("pageSize") pageSize : Int?,
                     @Query("page") page : Int?) : Call<NewsResult>

    @Headers("X-Api-Key: ${BuildConfig.News_API_Key}")
    @GET("/v2/top-headlines")
    fun topHeadlinesFromSources(@Query("sources") sources : String,
                                @Query("q") q : String,
                                @Query("pageSize") pageSize : Int,
                                @Query("page") page : Int) : Call<NewsResult>

    @Headers("X-Api-Key: ${BuildConfig.News_API_Key}")
    @GET("/v2/everything")
    fun everything(@Query("q") q : String,
                   @Query("sources") sources : String,
                   @Query("domains") domains : String,
                   @Query("excludeDomains") excludeDomains: String,
                   @Query("from") from : String,
                   @Query("to") to : String,
                   @Query("language") language : String,
                   @Query("sortBy") sortBy : String,
                   @Query("pageSize") pageSize : Int,
                   @Query("page") page : Int) : Call<NewsResult>
}