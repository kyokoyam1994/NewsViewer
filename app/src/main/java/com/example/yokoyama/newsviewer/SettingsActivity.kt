package com.example.yokoyama.newsviewer

import ARTICLES_PER_PAGE_DEFAULT
import ARTICLES_PER_PAGE_KEY
import COUNTRY_KEY
import LANGUAGE_KEY
import defaultSharedPreferences
import currCountry
import currLanguage

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import asyncIO
import com.example.yokoyama.newsviewer.adapter.SourcesAdapter
import com.example.yokoyama.newsviewer.data.Country
import com.example.yokoyama.newsviewer.data.Language
import com.example.yokoyama.newsviewer.database.AppDatabase
import com.example.yokoyama.newsviewer.dialog.CountryDialogFragment
import com.example.yokoyama.newsviewer.dialog.LanguageDialogFragment
import com.example.yokoyama.newsviewer.dialog.SourcesDialogFragment
import com.example.yokoyama.newsviewer.newsapi.NewsApiClient
import com.example.yokoyama.newsviewer.newsapi.NewsSourceResult
import com.example.yokoyama.newsviewer.viewmodel.SettingsViewModel
import kotlinx.android.synthetic.main.settings_main.*
import io.reactivex.disposables.CompositeDisposable
import get
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import plusAssign
import set
import java.lang.Exception

class SettingsActivity : AppCompatActivity(), CountryDialogFragment.CountrySelectedListener,
        LanguageDialogFragment.LanguageSelectedListener, SourcesDialogFragment.SourcesSelectedListener{

    private val compositeDisposable : CompositeDisposable = CompositeDisposable()
    private val settingsViewModel : SettingsViewModel by lazy { ViewModelProviders.of(this).get(SettingsViewModel::class.java) }
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

        imageViewEditSources.setOnClickListener {
            val databaseObservable = Single.create<List<NewsSourceResult.NewsSource>> {
                val filteredSources =  settingsViewModel.filteredSources.value
                when (filteredSources) {
                    null -> it.onError(Exception())
                    else -> it.onSuccess(filteredSources)
                }
            }

            compositeDisposable += Single.zip(
                    NewsApiClient.instance.sources(), databaseObservable,
                    BiFunction { t1: NewsSourceResult, t2: List<NewsSourceResult.NewsSource> -> EditSourcesParameters(t1.sources, t2)})
                    .asyncIO().subscribe { parameters ->
                        SourcesDialogFragment.newInstance(parameters.retrievedSources as ArrayList<NewsSourceResult.NewsSource>,
                                parameters.filteredSources as ArrayList<NewsSourceResult.NewsSource>).show(supportFragmentManager, "Sources")
                    }
        }

        imageViewClearSources.setOnClickListener {
            viewFlipper.displayedChild = viewFlipper.indexOfChild(indeterminateBar)
            compositeDisposable += Completable.create {
                AppDatabase.getInstance(this)?.newsSourceDao()?.deleteAll()
                it.onComplete()
            }.asyncIO().subscribe()
        }

        val stateObserver : Observer<List<NewsSourceResult.NewsSource>> = Observer {
            it?.let {
                recyclerViewSources.adapter = SourcesAdapter(this, false, it, mutableListOf())
                recyclerViewSources.layoutManager = LinearLayoutManager(this)
                when {
                    it.isEmpty() -> viewFlipper.displayedChild = viewFlipper.indexOfChild(textViewNoSources)
                    else -> viewFlipper.displayedChild = viewFlipper.indexOfChild(recyclerViewSources)
                }
            }
        }
        settingsViewModel.filteredSources.observe(this, stateObserver)
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
        viewFlipper.displayedChild = viewFlipper.indexOfChild(indeterminateBar)
        compositeDisposable += Completable.create {
            AppDatabase.getInstance(this)?.newsSourceDao()?.deleteAll()
            AppDatabase.getInstance(this)?.newsSourceDao()?.insertAll(sources)
            it.onComplete()
        }.asyncIO().subscribe()
    }
}

data class EditSourcesParameters(val retrievedSources: List<NewsSourceResult.NewsSource>, val filteredSources: List<NewsSourceResult.NewsSource>)