package com.example.yokoyama.newsviewer

import ARTICLES_PER_PAGE_DEFAULT
import ARTICLES_PER_PAGE_KEY
import COUNTRY_KEY
import LANGUAGE_KEY
import defaultSharedPreferences

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.yokoyama.newsviewer.dialog.CountryDialogFragment
import com.example.yokoyama.newsviewer.dialog.LanguageDialogFragment
import currCountry
import currLanguage
import kotlinx.android.synthetic.main.settings_main.*
import get
import set

class SettingsActivity : AppCompatActivity(), CountryDialogFragment.CountrySelectedListener, LanguageDialogFragment.LanguageSelectedListener{

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
            CountryDialogFragment.newInstance().show(supportFragmentManager, "TEST")
        }

        textViewSpinnerLanguage.text = currLanguage.displayName
        textViewSpinnerLanguage.setOnClickListener {
            LanguageDialogFragment.newInstance().show(supportFragmentManager, "TEST")
        }
    }

    override fun countrySelected(country: Country) {
        defaultSharedPreferences[COUNTRY_KEY] = country.name
        textViewSpinnerCountry.text = country.displayName
    }

    override fun languageSelected(language: Language) {
        defaultSharedPreferences[LANGUAGE_KEY] = language.name
        textViewSpinnerLanguage.text = language.displayName
    }

}