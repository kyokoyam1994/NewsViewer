package com.example.yokoyama.newsviewer

import ARTICLES_PER_PAGE_DEFAULT
import ARTICLES_PER_PAGE_KEY
import LEFT_BIAS
import MIN_PAGE
import NESTED_SCROLL_POSITION_KEY
import RIGHT_BIAS

import defaultSharedPreferences
import px
import currCountry
import currLanguage

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewCompat
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
import com.example.yokoyama.newsviewer.adapter.NewsEntryAdapter
import com.example.yokoyama.newsviewer.data.Category
import com.example.yokoyama.newsviewer.data.NewsType
import com.example.yokoyama.newsviewer.database.AppDatabase
import com.example.yokoyama.newsviewer.newsapi.NewsApiClient
import com.example.yokoyama.newsviewer.newsapi.NewsError
import com.example.yokoyama.newsviewer.newsapi.NewsResult
import com.example.yokoyama.newsviewer.newsapi.NewsSourceResult
import com.example.yokoyama.newsviewer.viewmodel.NewsViewModel
import com.example.yokoyama.newsviewer.viewmodel.SearchState
import com.fasterxml.jackson.databind.ObjectMapper
import get
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.news_viewer_scrollview.*
import kotlinx.android.synthetic.main.sliding_toolbar_panel.*
import plusAssign
import retrofit2.HttpException
import toHeader

enum class ResponseState {
    SUCCESS,
    NO_RESULTS,
    SEARCH_TOO_BROAD,
    OTHER_ERROR
}

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, NewsEntryAdapter.ArticleListener {

    private val tag = MainActivity::class::java.name
    private val compositeDisposable : CompositeDisposable = CompositeDisposable()
    private val newsViewModel : NewsViewModel by lazy { ViewModelProviders.of(this).get(NewsViewModel::class.java) }
    private var nestedScrollPosition : IntArray? = null

    private fun reloadArticles(state : SearchState) {
        when (state.currentType) {
            is NewsType.Everything -> {
                spinnerSort.visibility = View.VISIBLE
                supportActionBar?.title = state.currentType.title
            }

            is NewsType.TopHeadlines -> {
                spinnerSort.visibility = View.GONE
                supportActionBar?.title = state.currentType.category.displayName
            }
        }

        val response = when (state.currentType) {
            is NewsType.Everything -> NewsApiClient.instance.everything(
                    q = state.queryString,
                    sources = state.currentType.sources.toHeader(),
                    sortBy = when(spinnerSort.selectedItem.toString()) {
                        resources.getString(R.string.spinner_sort_relevancy_option) -> "relevancy"
                        resources.getString(R.string.spinner_sort_popularity_option) -> "popularity"
                        resources.getString(R.string.spinner_sort_published_date_option) -> "publishedAt"
                        else -> null
                    },
                    language = state.currentType.language.abbr,
                    pageSize = state.pageSize,
                    page = state.currentPage)

            is NewsType.TopHeadlines -> NewsApiClient.instance.topHeadlines(
                    q = state.queryString,
                    country = state.currentType.country.abbr,
                    category = state.currentType.category.abbr,
                    pageSize = state.pageSize,
                    page = state.currentPage)
        }

        viewFlipper.displayedChild = viewFlipper.indexOfChild(indeterminateBar)
        compositeDisposable += response.asyncIO().subscribe({onResponseSuccess(it)}, {onResponseError(it)})
    }

    private fun onResponseSuccess(result : NewsResult) {
        val state = when {
            result.totalResults > 0 -> ResponseState.SUCCESS
            else -> ResponseState.NO_RESULTS
        }
        updateResponseState(state, result)
    }

    private fun onResponseError(e : Throwable) {
        var broadSearch = false
        when (e) {
            is HttpException  ->  {
                val mapper = ObjectMapper()
                val newsError = mapper.readValue<NewsError>(e.response().errorBody()?.string(), NewsError::class.java)
                if (newsError.code == "parametersMissing") broadSearch = true
                Log.d(tag, newsError.toString())
            }
        }

        val state = when (broadSearch) {
            true -> ResponseState.SEARCH_TOO_BROAD
            else -> ResponseState.OTHER_ERROR
        }
        updateResponseState(state, NewsResult())
    }

    private fun populatePages(result : NewsResult) {
        val limit = LEFT_BIAS + RIGHT_BIAS
        var leftBound = when {
            newsViewModel.currentState.value!!.currentPage - LEFT_BIAS < MIN_PAGE -> MIN_PAGE
            else -> newsViewModel.currentState.value!!.currentPage - LEFT_BIAS
        }

        var rightBound = when {
            newsViewModel.currentState.value!!.currentPage + RIGHT_BIAS > result.pageCount(this) -> result.pageCount(this)
            else -> newsViewModel.currentState.value!!.currentPage + RIGHT_BIAS
        }

        val difference = rightBound - leftBound
        if (difference < limit) {
            leftBound = when {
                leftBound - (limit - difference) < MIN_PAGE -> MIN_PAGE
                else -> leftBound - (limit - difference)
            }

            rightBound = when {
                rightBound + (limit - difference) > result.pageCount(this) -> result.pageCount(this)
                else -> rightBound + (limit - difference)
            }
        }

        textViewCurrentPage.text = "Page ${newsViewModel.currentState.value!!.currentPage} of ${result.pageCount(this)}"
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

        if (newsViewModel.currentState.value!!.currentPage + 1 > result.pageCount(this)) {
            imageViewNext.visibility = View.GONE
            imageViewLast.visibility = View.GONE
            imageViewNext.setOnClickListener(null)
            imageViewLast.setOnClickListener(null)
        } else {
            imageViewNext.visibility = View.VISIBLE
            imageViewLast.visibility = View.VISIBLE
            imageViewNext.setOnClickListener { newsViewModel.updateState(newsViewModel.currentState.value!!.copy(currentPage = newsViewModel.currentState.value!!.currentPage + 1)) }
            imageViewLast.setOnClickListener { newsViewModel.updateState(newsViewModel.currentState.value!!.copy(currentPage = result.pageCount(this))) }
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

    private fun clearPages() {
        textViewCurrentPage.text = ""
        imageViewFirst.visibility = View.GONE
        imageViewPrev.visibility = View.GONE
        imageViewNext.visibility = View.GONE
        imageViewLast.visibility = View.GONE
        imageViewFirst.setOnClickListener(null)
        imageViewPrev.setOnClickListener(null)
        imageViewNext.setOnClickListener(null)
        imageViewLast.setOnClickListener(null)
        linearLayoutPageIndicator.removeAllViews()
    }

    private fun updateResponseState(state: ResponseState, result: NewsResult) {
        when (state) {
            ResponseState.SUCCESS -> {
                viewFlipper.displayedChild = viewFlipper.indexOfChild(nestedScrollViewNewsArticles)
                populatePages(result)
            }
            ResponseState.NO_RESULTS -> {
                textViewErrorIcon.text = resources.getString(R.string.text_view_info_no_results)
                imageViewErrorIcon.setImageResource(R.drawable.error_image_results)
                viewFlipper.displayedChild = viewFlipper.indexOfChild(layoutErrorMessage)
                clearPages()
            }
            ResponseState.SEARCH_TOO_BROAD -> {
                textViewErrorIcon.text = resources.getString(R.string.text_view_info_search_too_broad)
                imageViewErrorIcon.setImageResource(R.drawable.error_image_results)
                viewFlipper.displayedChild = viewFlipper.indexOfChild(layoutErrorMessage)
                clearPages()
            }
            ResponseState.OTHER_ERROR -> {
                textViewErrorIcon.text = resources.getString(R.string.text_view_info_error)
                imageViewErrorIcon.setImageResource(R.drawable.error_image_other)
                viewFlipper.displayedChild = viewFlipper.indexOfChild(layoutErrorMessage)
                clearPages()
            }
        }

        recyclerViewNewsArticles.adapter = NewsEntryAdapter(this, this, result.articles)
        recyclerViewNewsArticles.layoutManager = LinearLayoutManager(this)
        ViewCompat.setNestedScrollingEnabled(recyclerViewNewsArticles, false)

        val position = nestedScrollPosition
        if (position != null) {
            nestedScrollViewNewsArticles.scrollTo(position[0], position[1])
            nestedScrollPosition = null
        } else {
            nestedScrollViewNewsArticles.scrollTo(0, 0)
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
            compositeDisposable += Single.create<List<NewsSourceResult.NewsSource>> {
                it.onSuccess(AppDatabase.getInstance(this)!!.newsSourceDao().getAll())
            }.asyncIO().subscribe {
                sources -> val type = newsViewModel.currentState.value!!.currentType
                newsViewModel.updateState(
                    SearchState(currentPage = MIN_PAGE,
                                queryString = searchView.query.toString(),
                                pageSize = defaultSharedPreferences[ARTICLES_PER_PAGE_KEY, ARTICLES_PER_PAGE_DEFAULT] ?: ARTICLES_PER_PAGE_DEFAULT,
                                currentType = when (type) {
                                    is NewsType.Everything -> NewsType.Everything(currLanguage, sources)
                                    is NewsType.TopHeadlines -> NewsType.TopHeadlines(type.category, currCountry)
                                })
                )
            }
        }

        nestedScrollPosition = savedInstanceState?.getIntArray(NESTED_SCROLL_POSITION_KEY)

        val stateObserver : Observer<SearchState> = Observer { it?.let { reloadArticles(it) } }
        newsViewModel.currentState.observe(this, stateObserver)

        if (newsViewModel.currentState.value == null) {
            newsViewModel.updateState(
                SearchState(currentPage = MIN_PAGE,
                            pageSize = defaultSharedPreferences[ARTICLES_PER_PAGE_KEY, ARTICLES_PER_PAGE_DEFAULT] ?: ARTICLES_PER_PAGE_DEFAULT,
                            currentType = NewsType.TopHeadlines(Category.GENERAL, currCountry))
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putIntArray(NESTED_SCROLL_POSITION_KEY, intArrayOf(nestedScrollViewNewsArticles.scrollX, nestedScrollViewNewsArticles.scrollY))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
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
        compositeDisposable += Single.create<List<NewsSourceResult.NewsSource>> {
            it.onSuccess(AppDatabase.getInstance(this)!!.newsSourceDao().getAll())
        }.asyncIO().subscribe { sources ->
            var state: NewsType? = null
            when (item.itemId) {
                R.id.nav_home -> state = NewsType.Everything(currLanguage, sources)
                R.id.nav_business -> state = NewsType.TopHeadlines(Category.BUSINESS, currCountry)
                R.id.nav_entertainment -> state = NewsType.TopHeadlines(Category.ENTERTAINMENT, currCountry)
                R.id.nav_general -> state = NewsType.TopHeadlines(Category.GENERAL, currCountry)
                R.id.nav_health -> state = NewsType.TopHeadlines(Category.HEALTH, currCountry)
                R.id.nav_science -> state = NewsType.TopHeadlines(Category.SCIENCE, currCountry)
                R.id.nav_sports -> state = NewsType.TopHeadlines(Category.SPORTS, currCountry)
                R.id.nav_technology -> state = NewsType.TopHeadlines(Category.TECHNOLOGY, currCountry)
                R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            }

            if (state != null) {
                newsViewModel.updateState(
                    SearchState(currentPage = MIN_PAGE,
                                pageSize = defaultSharedPreferences[ARTICLES_PER_PAGE_KEY, ARTICLES_PER_PAGE_DEFAULT] ?: ARTICLES_PER_PAGE_DEFAULT,
                                currentType = state)
                )
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun articleSelected(newsEntry: NewsResult.NewsEntry) {
        Log.d(tag, newsEntry.url)
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(newsEntry.url)))
    }
}