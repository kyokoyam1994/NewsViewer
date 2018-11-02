package com.example.yokoyama.newsviewer

import ARTICLES_PER_PAGE_DEFAULT
import ARTICLES_PER_PAGE_KEY
import COUNTRY_KEY
import LANGUAGE_KEY
import defaultSharedPreferences
import currCountry
import currLanguage

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import asyncIO
import com.example.yokoyama.newsviewer.database.AppDatabase
import com.example.yokoyama.newsviewer.dialog.CountryDialogFragment
import com.example.yokoyama.newsviewer.dialog.LanguageDialogFragment
import com.example.yokoyama.newsviewer.dialog.SourcesDialogFragment
import com.example.yokoyama.newsviewer.newsapi.NewsApiClient
import com.example.yokoyama.newsviewer.newsapi.NewsSourceResult
import kotlinx.android.synthetic.main.settings_main.*
import io.reactivex.disposables.CompositeDisposable
import get
import io.reactivex.Completable
import io.reactivex.Single
import plusAssign
import set
import java.util.ArrayList

class SettingsActivity : AppCompatActivity(), CountryDialogFragment.CountrySelectedListener,
        LanguageDialogFragment.LanguageSelectedListener, SourcesDialogFragment.SourcesSelectedListener{

    private val compositeDisposable : CompositeDisposable = CompositeDisposable()
    private val articlesPerPageValues = arrayOf(10, 20, 40, 50)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_main)

        val articlesPerPage = defaultSharedPreferences[ARTICLES_PER_PAGE_KEY, ARTICLES_PER_PAGE_DEFAULT]
        spinnerArticlesPerPage.adapter = ArrayAdapter<Int>(this, android.R.layout.simple_spinner_item, articlesPerPageValues)
        spinnerArticlesPerPage.setSelection(articlesPerPageValues.indexOf(articlesPerPage))

        spinnerArticlesPerPage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val selection = parent?.getItemAtPosition(pos)
                selection?.let { defaultSharedPreferences[ARTICLES_PER_PAGE_KEY] = selection }
            }
        }

        textViewSpinnerCountry.text = currCountry.displayName
        textViewSpinnerCountry.setOnClickListener {
            CountryDialogFragment.newInstance().show(supportFragmentManager, "Country")
        }

        textViewSpinnerLanguage.text = currLanguage.displayName
        textViewSpinnerLanguage.setOnClickListener {
            LanguageDialogFragment.newInstance().show(supportFragmentManager, "Language")
        }

        spinnerSources.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val selection = parent?.getItemAtPosition(pos)
                val visiblility = when (selection) {
                    resources.getString(R.string.spinner_sort_all_sources_option) -> View.GONE
                    else -> View.VISIBLE
                }
                imageViewEditSources.visibility = visiblility
                recyclerViewSources.visibility = visiblility
            }
        }

        imageViewEditSources.setOnClickListener {
            compositeDisposable += NewsApiClient.instance.sources().asyncIO().subscribe {
                it -> Log.d("TAG", it.toString())
                SourcesDialogFragment.newInstance(it.sources as ArrayList<NewsSourceResult.NewsSource>).show(supportFragmentManager, "Sources")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    override fun countrySelected(country: Country) {
        defaultSharedPreferences[COUNTRY_KEY] = country.name
        textViewSpinnerCountry.text = country.displayName
    }

    override fun languageSelected(language: Language) {
        defaultSharedPreferences[LANGUAGE_KEY] = language.name
        textViewSpinnerLanguage.text = language.displayName
    }

    override fun sourcesSelected(sources: List<NewsSourceResult.NewsSource>) {
        /*Completable.create {
            AppDatabase.getInstance(this)?.newsSourceDao()?.deleteAll()
            AppDatabase.getInstance(this)?.newsSourceDao()?.insertAll(sources)
        }*/
        //compositeDisposable +=
    }

}