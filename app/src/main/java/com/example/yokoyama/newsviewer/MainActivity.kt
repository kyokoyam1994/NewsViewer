package com.example.yokoyama.newsviewer

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.yokoyama.newsviewer.newsapi.BASE_URL
import com.example.yokoyama.newsviewer.newsapi.NewsApiService
import com.example.yokoyama.newsviewer.newsapi.NewsError
import com.example.yokoyama.newsviewer.newsapi.NewsResult
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.news_viewer_scrollview.*
import kotlinx.android.synthetic.main.page_indicator.*
import px
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.lang.Exception

const val PAGE_SIZE = 20
const val LEFT_BIAS = 4
const val RIGHT_BIAS = 5

const val MIN_PAGE = 1
const val MAX_ARTICLES = 1000

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, NewsEntryAdapter.ArticleListener {

    var currentPage = MIN_PAGE
    var totalPages = MIN_PAGE
    lateinit var currentCategory : NewsCategory

    private fun refreshArticles(pageNumber: Int) {
        currentPage = pageNumber
        NewsArticleTask(searchView.query.toString()).execute()
    }

    private fun loadCategory(category: NewsCategory) {
        currentCategory = category
        when (currentCategory) {
            is NewsCategory.Everything -> {
                spinnerSort.visibility = View.VISIBLE
            }
            is NewsCategory.TopHeadlines -> {
                spinnerSort.visibility = View.GONE
            }
        }
        refreshArticles(MIN_PAGE)
    }

    private fun populatePages(result : NewsResult) {
        totalPages = when {
            result.totalResults > MAX_ARTICLES -> MAX_ARTICLES / PAGE_SIZE
            result.totalResults == 0 || result.totalResults % PAGE_SIZE > 0 -> result.totalResults / PAGE_SIZE + 1
            else -> result.totalResults / PAGE_SIZE
        }

        textViewCurrentPage.text = "Page $currentPage of $totalPages"

        val limit = LEFT_BIAS + RIGHT_BIAS

        var leftBound = when {
            currentPage - LEFT_BIAS < MIN_PAGE -> MIN_PAGE
            else -> currentPage - LEFT_BIAS
        }

        var rightBound = when {
            currentPage + RIGHT_BIAS > totalPages -> totalPages
            else -> currentPage + RIGHT_BIAS
        }

        val difference = rightBound - leftBound
        if (difference < limit) {
            leftBound = when {
                leftBound - (limit - difference) < MIN_PAGE -> MIN_PAGE
                else -> leftBound - (limit - difference)
            }

            rightBound = when {
                rightBound + (limit - difference) > totalPages -> totalPages
                else -> rightBound + (limit - difference)
            }
        }

        linearLayoutPageIndicator.removeAllViews()
        for (i in leftBound..rightBound) {
            val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutParams.marginStart = 4.px
            layoutParams.marginEnd = 4.px

            val textViewPageNumber = TextView(this@MainActivity)
            if (i == currentPage) textViewPageNumber.paintFlags = (textViewPageNumber.paintFlags or Paint.UNDERLINE_TEXT_FLAG)
            textViewPageNumber.text = i.toString()
            textViewPageNumber.layoutParams = layoutParams
            textViewPageNumber.setOnClickListener { refreshArticles(textViewPageNumber.text.toString().toInt()) }
            linearLayoutPageIndicator.addView(textViewPageNumber)
            Log.d("TAG", "Adding page $i")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        buttonSearch.setOnClickListener { refreshArticles(MIN_PAGE) }

        imageViewFirst.setOnClickListener { refreshArticles(MIN_PAGE) }
        imageViewPrev.setOnClickListener { if (currentPage - 1 >= MIN_PAGE) refreshArticles(currentPage - 1) }
        imageViewNext.setOnClickListener { if (currentPage + 1 <= totalPages) refreshArticles(currentPage + 1) }
        imageViewLast.setOnClickListener { refreshArticles(totalPages) }

        loadCategory(NewsCategory.TopHeadlines(TopHeadlinesCategory.GENERAL))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> loadCategory(NewsCategory.Everything)
            R.id.nav_business -> loadCategory(NewsCategory.TopHeadlines(TopHeadlinesCategory.BUSINESS))
            R.id.nav_entertainment -> loadCategory(NewsCategory.TopHeadlines(TopHeadlinesCategory.ENTERTAINMENT))
            R.id.nav_general -> loadCategory(NewsCategory.TopHeadlines(TopHeadlinesCategory.GENERAL))
            R.id.nav_health -> loadCategory(NewsCategory.TopHeadlines(TopHeadlinesCategory.HEALTH))
            R.id.nav_science -> loadCategory(NewsCategory.TopHeadlines(TopHeadlinesCategory.SCIENCE))
            R.id.nav_sports -> loadCategory(NewsCategory.TopHeadlines(TopHeadlinesCategory.SPORTS))
            R.id.nav_technology -> loadCategory(NewsCategory.TopHeadlines(TopHeadlinesCategory.TECHNOLOGY))
            R.id.nav_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun articleSelected(newsEntry: NewsResult.NewsEntry) {
        Log.d("TAG", newsEntry.url)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newsEntry.url))
        startActivity(intent)
    }

    inner class NewsArticleTask(val searchQuery : String) : AsyncTask<Void, Void, NewsResult?>() {

        override fun doInBackground(vararg p0: Void?): NewsResult? {
            val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(JacksonConverterFactory.create()).build()
            val newsApiService = retrofit.create(NewsApiService::class.java)
            val response = when (currentCategory) {
                is NewsCategory.Everything -> newsApiService.everything(q = searchQuery,
                        pageSize = PAGE_SIZE,
                        page = currentPage).execute()

                is NewsCategory.TopHeadlines -> newsApiService.topHeadlines(country = "us",
                        category = (currentCategory as NewsCategory.TopHeadlines).topHeadlinesCategory.category,
                        q = searchQuery,
                        pageSize = PAGE_SIZE,
                        page = currentPage).execute()
            }

            return if (response.isSuccessful) {
                Log.d("SUCCESS", response.raw().request().url().toString())
                response.body()
            } else {
                try {
                    val mapper = ObjectMapper()
                    val newsError = mapper.readValue<NewsError>(response.errorBody()?.string(), NewsError::class.java)
                    Log.d("TAG", newsError.toString())
                } catch (e : Exception) {
                    Log.d("TAG", e.message)
                }
                null
            }
        }

        override fun onPostExecute(result: NewsResult?) {
            super.onPostExecute(result)
            if (result?.articles != null) {
                recyclerViewNewsArticles?.adapter = NewsEntryAdapter(this@MainActivity, this@MainActivity, result.articles)
                recyclerViewNewsArticles?.layoutManager = LinearLayoutManager(this@MainActivity)
                populatePages(result)
            }
        }
    }

}
