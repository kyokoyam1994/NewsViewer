package com.example.yokoyama.newsviewer

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

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