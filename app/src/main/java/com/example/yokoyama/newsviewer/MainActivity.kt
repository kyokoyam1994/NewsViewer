package com.example.yokoyama.newsviewer

import android.content.Intent
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
import android.view.ViewGroup
import android.widget.TextView
import com.example.yokoyama.newsviewer.newsapi.BASE_URL
import com.example.yokoyama.newsviewer.newsapi.NewsApiService
import com.example.yokoyama.newsviewer.newsapi.NewsResult
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.news_viewer_scrollview.*
import kotlinx.android.synthetic.main.page_indicator.*
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, NewsEntryAdapter.ArticleListener {

    val PAGE_SIZE = 20
    val PAGES_SHOWN = 5
    val MIN_PAGE = 1
    var currentPage = MIN_PAGE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)


        imageViewFirst.setOnClickListener {
            currentPage = 1
            NewsArticleTask().execute()
        }

        //NewsArticleTask().execute()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

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
        // Handle navigation view item clicks here.
        when (item.itemId) {
            /*R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }*/
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun articleSelected(newsEntry: NewsResult.NewsEntry) {
        Log.d("TAG", newsEntry.url)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newsEntry.url))
        startActivity(intent)
    }

    inner class NewsArticleTask : AsyncTask<Void, Void, NewsResult?>() {

        override fun doInBackground(vararg p0: Void?): NewsResult? {
            return requestNewsApi()
        }

        override fun onPostExecute(result: NewsResult?) {
            super.onPostExecute(result)

            if (result?.articles != null) {
                recyclerViewNewsArticles?.adapter = NewsEntryAdapter(this@MainActivity, this@MainActivity, result.articles)
                recyclerViewNewsArticles?.layoutManager = LinearLayoutManager(this@MainActivity)

                val pageNumbers = when {
                    result.totalResults == 0 || result.totalResults % PAGE_SIZE > 0 -> result.totalResults / PAGE_SIZE + 1
                    else -> result.totalResults / PAGE_SIZE
                }

                val maxPage = when {
                    currentPage + PAGES_SHOWN > pageNumbers -> pageNumbers
                    else -> currentPage + PAGES_SHOWN
                }

                Log.d("TAG", "current page $currentPage, max page $maxPage")
                linearLayoutPageIndicator.removeAllViews()
                for (i in currentPage..maxPage) {
                    val textViewPageNumber = TextView(this@MainActivity)
                    textViewPageNumber.text = i.toString()
                    textViewPageNumber.setOnClickListener {
                        currentPage = textViewPageNumber.text.toString().toInt()
                        NewsArticleTask().execute()
                    }
                    textViewPageNumber.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    linearLayoutPageIndicator.addView(textViewPageNumber)
                    Log.d("TAG", "Adding page $i")
                }
            }
        }

        private fun requestNewsApi() : NewsResult? {
            //return NewsQueryTopHeadlines().query()
            val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(JacksonConverterFactory.create()).build()
            val newsApiService = retrofit.create(NewsApiService::class.java)
            val response = newsApiService.topHeadlines("us", "", "", PAGE_SIZE, 1).execute()

            if (response.isSuccessful) {
                Log.d("TAG", "SUCCESS RETRO")
            } else {
                try {
                    val jObjError = JSONObject(response.errorBody()?.string())
                    Log.d("TAG", jObjError.getString("message"))
                } catch (e: Exception) {
                    Log.d("TAG", e.message)
                }
            }
            return null
        }
    }

}
