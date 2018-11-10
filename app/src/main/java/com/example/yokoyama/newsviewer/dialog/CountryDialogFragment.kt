package com.example.yokoyama.newsviewer.dialog

import COUNTRY_DIALOG_SELECTED_INDEX_KEY
import currCountry

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.example.yokoyama.newsviewer.R
import com.example.yokoyama.newsviewer.data.Country
import com.example.yokoyama.newsviewer.adapter.CountryAdapter
import kotlinx.android.synthetic.main.dialog_fragment.view.*

class CountryDialogFragment : AppCompatDialogFragment() {

    private val dialogTag = CountryDialogFragment::class::java.name
    private lateinit var countrySelectedListener: CountrySelectedListener
    private lateinit var countryAdapter: CountryAdapter

    interface CountrySelectedListener {
        fun countrySelected(country: Country)
    }

    companion object {
        fun newInstance() : CountryDialogFragment {
            return CountryDialogFragment()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            countrySelectedListener = context as CountrySelectedListener
        } catch (e : Exception) {
            Log.e(dialogTag, "Context must implement ${CountrySelectedListener::class.java.name} interface", e)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity?.layoutInflater
        val inflatedView = inflater?.inflate(R.layout.dialog_fragment, null)

        activity?.let {
            val countryIndex = savedInstanceState?.getInt(COUNTRY_DIALOG_SELECTED_INDEX_KEY) ?: Country.values().indexOf(it.currCountry)
            countryAdapter = CountryAdapter(countryIndex)
            inflatedView?.recyclerViewDialog?.adapter = countryAdapter
            inflatedView?.recyclerViewDialog?.layoutManager = LinearLayoutManager(activity)
        }

        return builder.setTitle(R.string.country_dialog_fragment_title_text)
                .setView(inflatedView)
                .setNegativeButton(R.string.dialog_fragment_negative_button_text, null)
                .setPositiveButton(R.string.dialog_fragment_positive_button_text) { _, _ ->
                    countrySelectedListener.countrySelected(countryAdapter.countries[countryAdapter.checkedPosition])
                }.create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(COUNTRY_DIALOG_SELECTED_INDEX_KEY, countryAdapter.checkedPosition)
    }

}