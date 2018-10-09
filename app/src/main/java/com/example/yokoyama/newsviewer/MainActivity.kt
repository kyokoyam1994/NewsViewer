package com.example.yokoyama.newsviewer

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.android.synthetic.main.activity_main.*
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //thread(start = true) { requestNewsApi() }
        NewsArticleTask().execute()
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


    inner class NewsArticleTask() : AsyncTask<Void, Void, NewsResult.QueryResult?>() {

        override fun doInBackground(vararg p0: Void?): NewsResult.QueryResult? {
            return requestNewsApi()
        }

        override fun onPostExecute(result: NewsResult.QueryResult?) {
            super.onPostExecute(result)
            if (result?.articles != null && this@MainActivity != null) {
                recyclerViewNewsArticles?.adapter = NewsEntryAdapter(result?.articles)
                recyclerViewNewsArticles?.layoutManager = LinearLayoutManager(this@MainActivity)
            }
        }

        private fun requestNewsApi() : NewsResult.QueryResult? {
            val url = URL("https://newsapi.org/v2/top-headlines?country=us")
            val connection = url.openConnection() as HttpURLConnection
            var queryResult : NewsResult.QueryResult? = null
            connection.setRequestProperty("X-Api-Key", "INSERT_YOUR_API_KEY_HERE")

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val mapper = ObjectMapper()
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                queryResult = mapper.readValue<NewsResult.QueryResult>(response, NewsResult.QueryResult::class.java)

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

}
