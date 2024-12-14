package com.clerami.intermediate.ui.story

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.clerami.intermediate.data.remote.retrofit.ApiService

class StoryViewModelFactory(
    private val repository: StoryRepository,
    private val context: Context,
    private val apiService: ApiService
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            return StoryViewModel(repository, context, apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}