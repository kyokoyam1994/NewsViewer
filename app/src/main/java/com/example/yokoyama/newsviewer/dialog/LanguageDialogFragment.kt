package com.example.yokoyama.newsviewer.dialog

import currLanguage
import LANGUAGE_DIALOG_SELECTED_INDEX_KEY

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.example.yokoyama.newsviewer.R
import com.example.yokoyama.newsviewer.data.Language
import com.example.yokoyama.newsviewer.adapter.LanguageAdapter

class LanguageDialogFragment : AppCompatDialogFragment() {

    private lateinit var languageSelectedListener: LanguageSelectedListener
    private lateinit var languageAdapter: LanguageAdapter

    interface LanguageSelectedListener {
        fun languageSelected(language: Language)
    }

    companion object {
        fun newInstance() : LanguageDialogFragment {
            return LanguageDialogFragment()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            languageSelectedListener = context as LanguageSelectedListener
        } catch (e : Exception) {
            Log.d("TAG", "Context must implement ${LanguageSelectedListener::class.java.name} interface")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity?.layoutInflater
        val inflatedView = inflater?.inflate(R.layout.dialog_fragment, null)

        val recyclerViewDialog = inflatedView?.findViewById<RecyclerView>(R.id.recyclerViewDialog)

        activity?.let {
            val languageIndex = savedInstanceState?.getInt(LANGUAGE_DIALOG_SELECTED_INDEX_KEY) ?: Language.values().indexOf(it.currLanguage)
            languageAdapter = LanguageAdapter(languageIndex)
            recyclerViewDialog?.adapter = languageAdapter
            recyclerViewDialog?.layoutManager = LinearLayoutManager(activity)
        }

        return builder.setTitle(R.string.language_dialog_fragment_title_text)
                .setView(inflatedView)
                .setNegativeButton(R.string.dialog_fragment_negative_button_text, null)
                .setPositiveButton(R.string.dialog_fragment_positive_button_text){ _, _ ->
                    languageSelectedListener.languageSelected(languageAdapter.languages[languageAdapter.checkedPosition])
                }.create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(LANGUAGE_DIALOG_SELECTED_INDEX_KEY, languageAdapter.checkedPosition)
    }

}