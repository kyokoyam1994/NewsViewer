package com.example.yokoyama.newsviewer.dialog

import SOURCES_KEY
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.example.yokoyama.newsviewer.R
import com.example.yokoyama.newsviewer.adapter.SourcesAdapter
import com.example.yokoyama.newsviewer.newsapi.NewsSourceResult
import java.util.ArrayList

class SourcesDialogFragment : DialogFragment() {

    private lateinit var sourcesSelectedListener: SourcesSelectedListener

    interface SourcesSelectedListener {
        fun sourcesSelected(sources: List<NewsSourceResult.NewsSource>)
    }

    companion object {
        fun newInstance(sources: ArrayList<NewsSourceResult.NewsSource>) : SourcesDialogFragment {
            val dialogFragment = SourcesDialogFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList(SOURCES_KEY, sources)
            dialogFragment.arguments = bundle
            return dialogFragment
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            sourcesSelectedListener = context as SourcesSelectedListener
        } catch (e : Exception) {
            Log.d("TAG", "Context must implement ${SourcesSelectedListener::class.java.name} interface")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity?.layoutInflater
        val inflatedView = inflater?.inflate(R.layout.dialog_fragment, null)

        val recyclerViewDialog = inflatedView?.findViewById<RecyclerView>(R.id.recyclerViewDialog)
        val sources : List<NewsSourceResult.NewsSource>? = arguments?.get(SOURCES_KEY) as List<NewsSourceResult.NewsSource>?

        activity?.let {
            val checkedIndices = mutableListOf<Int>()
            recyclerViewDialog?.adapter = SourcesAdapter(it, sources ?: emptyList(), checkedIndices)
            recyclerViewDialog?.layoutManager = LinearLayoutManager(activity)
        }

        return builder.setTitle("Sources")
                .setView(inflatedView)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK"){ _, _ ->
                    val adapter = recyclerViewDialog?.adapter as SourcesAdapter
                    sourcesSelectedListener.sourcesSelected(adapter.sources)
                }.create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val recyclerViewDialog = view?.findViewById<RecyclerView>(R.id.recyclerViewDialog)
        val adapter = recyclerViewDialog?.adapter as? SourcesAdapter
        outState.putParcelableArrayList(SOURCES_KEY, adapter?.sources as? ArrayList<out Parcelable>)
    }

}