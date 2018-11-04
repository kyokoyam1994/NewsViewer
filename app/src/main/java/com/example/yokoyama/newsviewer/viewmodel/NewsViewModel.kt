package com.example.yokoyama.newsviewer.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.example.yokoyama.newsviewer.data.NewsType

class NewsViewModel : ViewModel() {
    val currentState: MutableLiveData<SearchState> by lazy { MutableLiveData<SearchState>() }

    fun updateState(state: SearchState, mainThread : Boolean = true) =
        when (mainThread) {
            true -> currentState.value = state
            false -> currentState.postValue(state)
        }
}

data class SearchState (val currentPage : Int,
                        val pageSize : Int,
                        val currentType : NewsType,
                        val queryString : String? = null)