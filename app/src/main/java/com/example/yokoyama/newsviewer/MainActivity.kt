package com.example.yokoyama.newsviewer

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
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
import asyncIO
import com.example.yokoyama.newsviewer.newsapi.NewsApiClient
import com.example.yokoyama.newsviewer.newsapi.NewsError
import com.example.yokoyama.newsviewer.newsapi.NewsResult
import com.fasterxml.jackson.databind.ObjectMapper
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.news_viewer_scrollview.*
import plusAssign
import retrofit2.HttpException

import px

const val PAGE_SIZE = 20
const val LEFT_BIAS = 4
const val RIGHT_BIAS = 5
const val MIN_PAGE = 1
const val MAX_ARTICLES = 1000

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, NewsEntryAdapter.ArticleListener {

    private val compositeDisposable : CompositeDisposable = CompositeDisposable()
    private val newsViewModel : NewsViewModel by lazy { ViewModelProviders.of(this).get(NewsViewModel::class.java) }

    private fun reloadArticles(state : SearchState) {
        when (state.currentCategory) {
            is NewsCategory.Everything -> {
                spinnerSort.visibility = View.VISIBLE
                supportActionBar?.title = state.currentCategory.title
            }

            is NewsCategory.TopHeadlines -> {
                spinnerSort.visibility = View.GONE
                supportActionBar?.title = state.currentCategory.topHeadlinesCategory.category.capitalize()
            }
        }

        val response = when (state.currentCategory) {
            is NewsCategory.Everything -> NewsApiClient.instance.everything(q = state.queryString,
                    sortBy = when(spinnerSort.selectedItem.toString()) {
                        resources.getString(R.string.spinner_sort_relevancy_option) -> "relevancy"
                        resources.getString(R.string.spinner_sort_popularity_option) -> "popularity"
                        resources.getString(R.string.spinner_sort_published_date_option) -> "publishedAt"
                        else -> null
                    },
                    pageSize = state.pageSize,
                    page = state.currentPage)

            is NewsCategory.TopHeadlines -> NewsApiClient.instance.topHeadlines(country = "us",
                    category = state.currentCategory.topHeadlinesCategory.category,
                    q = state.queryString,
                    pageSize = state.pageSize,
                    page = state.currentPage)
        }

        compositeDisposable += response.asyncIO().subscribe(
            {result ->
                    Log.d("TAG", "SUCCESS")
                    textViewInfoMessage.visibility = View.GONE
                    recyclerViewNewsArticles.adapter = NewsEntryAdapter(this@MainActivity, this@MainActivity, result)
                    recyclerViewNewsArticles.layoutManager = LinearLayoutManager(this@MainActivity)
                    populatePages(result)},
            {e ->
                when (e) {
                    is HttpException  ->  {
                        val mapper = ObjectMapper()
                        val newsError = mapper.readValue<NewsError>(e.response().errorBody()?.string(), NewsError::class.java)
                        Log.d("TAG", newsError.toString())
                    }
                }
                recyclerViewNewsArticles.adapter = NewsEntryAdapter(this@MainActivity, this@MainActivity, NewsResult())
                textViewInfoMessage.visibility = View.VISIBLE}
        )
    }

    private fun populatePages(result : NewsResult) {
        val limit = LEFT_BIAS + RIGHT_BIAS
        var leftBound = when {
            newsViewModel.currentState.value!!.currentPage - LEFT_BIAS < MIN_PAGE -> MIN_PAGE
            else -> newsViewModel.currentState.value!!.currentPage - LEFT_BIAS
        }

        var rightBound = when {
            newsViewModel.currentState.value!!.currentPage + RIGHT_BIAS > result.pageCount -> result.pageCount
            else -> newsViewModel.currentState.value!!.currentPage + RIGHT_BIAS
        }

        val difference = rightBound - leftBound
        if (difference < limit) {
            leftBound = when {
                leftBound - (limit - difference) < MIN_PAGE -> MIN_PAGE
                else -> leftBound - (limit - difference)
            }

            rightBound = when {
                rightBound + (limit - difference) > result.pageCount -> result.pageCount
                else -> rightBound + (limit - difference)
            }
        }

        textViewCurrentPage.text = "Page ${newsViewModel.currentState.value!!.currentPage} of ${result.pageCount}"
        if (newsViewModel.currentState.value!!.currentPage - 1 < MIN_PAGE) {
            imageViewFirst.visibility = View.GONE
            imageViewPrev.visibility = View.GONE
            imageViewFirst.setOnClickListener(null)
            imageViewPrev.setOnClickListener(null)
        } else {
            imageViewFirst.visibility = View.VISIBLE
            imageViewPrev.visibility = View.VISIBLE
            imageViewFirst.setOnClickListener { newsViewModel.updateState(newsViewModel.currentState.value!!.copy(currentPage = MIN_PAGE)) }
            imageViewPrev.setOnClickListener { newsViewModel.updateState(newsViewModel.currentState.value!!.copy(currentPage = newsViewModel.currentState.value!!.currentPage - 1)) }
        }

        if (newsViewModel.currentState.value!!.currentPage + 1 > result.pageCount) {
            imageViewNext.visibility = View.GONE
            imageViewLast.visibility = View.GONE
            imageViewNext.setOnClickListener(null)
            imageViewLast.setOnClickListener(null)
        } else {
            imageViewNext.visibility = View.VISIBLE
            imageViewLast.visibility = View.VISIBLE
            imageViewNext.setOnClickListener { newsViewModel.updateState(newsViewModel.currentState.value!!.copy(currentPage = newsViewModel.currentState.value!!.currentPage + 1)) }
            imageViewLast.setOnClickListener { newsViewModel.updateState(newsViewModel.currentState.value!!.copy(currentPage = result.pageCount)) }
        }

        linearLayoutPageIndicator.removeAllViews()
        for (i in leftBound..rightBound) {
            val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutParams.marginStart = 4.px
            layoutParams.marginEnd = 4.px

            val textViewPageNumber = TextView(this@MainActivity)
            if (i == newsViewModel.currentState.value!!.currentPage) textViewPageNumber.paintFlags = (textViewPageNumber.paintFlags or Paint.UNDERLINE_TEXT_FLAG)
            textViewPageNumber.text = i.toString()
            textViewPageNumber.layoutParams = layoutParams

            textViewPageNumber.setOnClickListener { newsViewModel.updateState(newsViewModel.currentState.value!!.copy(currentPage = textViewPageNumber.text.toString().toInt())) }
            linearLayoutPageIndicator.addView(textViewPageNumber)
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
        buttonSearch.setOnClickListener {
            newsViewModel.updateState(SearchState(currentPage = MIN_PAGE, queryString = searchView.query.toString(),
                                        pageSize = PAGE_SIZE, currentCategory = newsViewModel.currentState.value!!.currentCategory))
        }

        val stateObserver : Observer<SearchState> = Observer {
            Log.d("OBSERVING", "CHANGE!!!")
            it?.let {reloadArticles(it)}
        }

        newsViewModel.currentState.observe(this, stateObserver)
        if (newsViewModel.currentState.value == null) {
            newsViewModel.updateState(SearchState(currentPage = MIN_PAGE, pageSize = PAGE_SIZE, currentCategory = NewsCategory.TopHeadlines(TopHeadlinesCategory.GENERAL)))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
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
        var state : NewsCategory? = null
        when (item.itemId) {
            R.id.nav_home -> state = NewsCategory.Everything
            R.id.nav_business -> state = NewsCategory.TopHeadlines(TopHeadlinesCategory.BUSINESS)
            R.id.nav_entertainment -> state = NewsCategory.TopHeadlines(TopHeadlinesCategory.ENTERTAINMENT)
            R.id.nav_general -> state = NewsCategory.TopHeadlines(TopHeadlinesCategory.GENERAL)
            R.id.nav_health -> state = NewsCategory.TopHeadlines(TopHeadlinesCategory.HEALTH)
            R.id.nav_science -> state = NewsCategory.TopHeadlines(TopHeadlinesCategory.SCIENCE)
            R.id.nav_sports -> state = NewsCategory.TopHeadlines(TopHeadlinesCategory.SPORTS)
            R.id.nav_technology -> state = NewsCategory.TopHeadlines(TopHeadlinesCategory.TECHNOLOGY)
            R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
        }

        if (state != null) {
            newsViewModel.updateState(SearchState(currentPage = MIN_PAGE, pageSize = PAGE_SIZE, currentCategory = state))
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun articleSelected(newsEntry: NewsResult.NewsEntry) {
        Log.d("TAG", newsEntry.url)
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(newsEntry.url)))
    }
}