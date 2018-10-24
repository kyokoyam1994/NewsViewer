package com.example.yokoyama.newsviewer

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.example.yokoyama.newsviewer.newsapi.NewsResult

class NewsViewModel : ViewModel() {

    lateinit var state: SearchState

    //private val changeNotifier = MutableLiveData<SearchState>()
    //val queryResult: NewsResult? = null

    /*fun updateState(state: SearchState) {
        changeNotifier.value = state
    }*/
}

data class SearchState (val currentPage : Int,
                        val pageSize : Int,
                        val currentCategory : NewsCategory,
                        val queryString : String? = null,
                        val queryResult: NewsResult? = null)