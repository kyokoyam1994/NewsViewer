package com.example.yokoyama.newsviewer.dialog

import SOURCES_DIALOG_SELECTED_SOURCES_KEY
import SOURCES_KEY

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
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
    private lateinit var sourcesAdapter: SourcesAdapter

    interface SourcesSelectedListener {
        fun sourcesSelected(sources: List<NewsSourceResult.NewsSource>)
    }

    companion object {
        fun newInstance(sources: ArrayList<NewsSourceResult.NewsSource>, selectedSources: ArrayList<NewsSourceResult.NewsSource>) : SourcesDialogFragment {
            val dialogFragment = SourcesDialogFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList(SOURCES_KEY, sources)
            bundle.putParcelableArrayList(SOURCES_DIALOG_SELECTED_SOURCES_KEY, selectedSources)
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
        val sources : List<NewsSourceResult.NewsSource> = arguments?.get(SOURCES_KEY) as? ArrayList<NewsSourceResult.NewsSource> ?: emptyList()
        val selectedSources : MutableList<NewsSourceResult.NewsSource> = arguments?.get(SOURCES_DIALOG_SELECTED_SOURCES_KEY) as? ArrayList<NewsSourceResult.NewsSource> ?: mutableListOf()


        activity?.let {
            sourcesAdapter = SourcesAdapter(it, true, sources, selectedSources)
            recyclerViewDialog?.adapter = sourcesAdapter
            recyclerViewDialog?.layoutManager = LinearLayoutManager(activity)
        }

        return builder.setTitle(R.string.sources_dialog_fragment_title_text)
                .setView(inflatedView)
                .setNegativeButton(R.string.dialog_fragment_negative_button_text, null)
                .setPositiveButton(R.string.dialog_fragment_positive_button_text){ _, _ ->
                    sourcesSelectedListener.sourcesSelected(sourcesAdapter.selectedSources)
                }.create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(SOURCES_KEY, sourcesAdapter.sources as ArrayList<NewsSourceResult.NewsSource>)
        outState.putParcelableArrayList(SOURCES_DIALOG_SELECTED_SOURCES_KEY, sourcesAdapter.selectedSources as ArrayList<NewsSourceResult.NewsSource>)
    }

}