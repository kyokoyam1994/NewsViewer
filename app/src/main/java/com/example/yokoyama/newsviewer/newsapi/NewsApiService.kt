package com.example.yokoyama.newsviewer.newsapi

import com.example.yokoyama.newsviewer.BuildConfig
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

const val BASE_URL: String = "https://newsapi.org"

interface NewsApiService {

    /* News API does not allow the use of country and category with sources. If using this request,
    make sure to only use one or the other. */
    @Headers("X-Api-Key: ${BuildConfig.News_API_Key}")
    @GET("/v2/top-headlines")
    fun topHeadlines(@Query("country") country : String? = null,
                     @Query("category") category : String? = null,
                     @Query("sources") sources : String? = null,
                     @Query("q") q : String? = null,
                     @Query("pageSize") pageSize : Int? = null,
                     @Query("page") page : Int? = null) : Single<NewsResult>

    @Headers("X-Api-Key: ${BuildConfig.News_API_Key}")
    @GET("/v2/everything")
    fun everything(@Query("q") q : String? = null,
                   @Query("sources") sources : String? = null,
                   @Query("domains") domains : String? = null,
                   @Query("excludeDomains") excludeDomains: String? = null,
                   @Query("from") from : String? = null,
                   @Query("to") to : String? = null,
                   @Query("language") language : String? = null,
                   @Query("sortBy") sortBy : String? = null,
                   @Query("pageSize") pageSize : Int? = null,
                   @Query("page") page : Int? = null) : Single<NewsResult>
}