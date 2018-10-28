package com.example.yokoyama.newsviewer.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.example.yokoyama.newsviewer.R
import com.example.yokoyama.newsviewer.Country
import com.example.yokoyama.newsviewer.adapter.CountryAdapter
import currCountry
import java.lang.Exception

class CountryDialogFragment : AppCompatDialogFragment() {

    private lateinit var countrySelectedListener: CountrySelectedListener

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
            Log.d("TAG", "Context must implement ${CountrySelectedListener::class.java.name} interface")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity?.layoutInflater
        val inflatedView = inflater?.inflate(R.layout.dialog_fragment, null)

        val recyclerViewDialog = inflatedView?.findViewById<RecyclerView>(R.id.recyclerViewDialog)

        activity?.let {
            val countryIndex = Country.values().indexOf(it.currCountry)
            recyclerViewDialog?.adapter = CountryAdapter(countryIndex)
            recyclerViewDialog?.layoutManager = LinearLayoutManager(activity)
        }

        return builder.setTitle("Countries")
                .setView(inflatedView)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK") { _, _ ->
                    val adapter = recyclerViewDialog?.adapter as CountryAdapter
                    countrySelectedListener.countrySelected(adapter.countries[adapter.checkedPosition])
                }.create()
    }

}