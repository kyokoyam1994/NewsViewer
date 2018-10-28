package com.example.yokoyama.newsviewer.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.example.yokoyama.newsviewer.Language
import com.example.yokoyama.newsviewer.R
import com.example.yokoyama.newsviewer.adapter.LanguageAdapter
import currLanguage

class LanguageDialogFragment : AppCompatDialogFragment() {

    private lateinit var languageSelectedListener: LanguageSelectedListener

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
            val languageIndex = Language.values().indexOf(it.currLanguage)
            recyclerViewDialog?.adapter = LanguageAdapter(languageIndex)
            recyclerViewDialog?.layoutManager = LinearLayoutManager(activity)
        }

        return builder.setTitle("Languages")
                .setView(inflatedView)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK"){ _, _ ->
                    val adapter = recyclerViewDialog?.adapter as LanguageAdapter
                    languageSelectedListener.languageSelected(adapter.languages[adapter.checkedPosition])
                }.create()
    }

}