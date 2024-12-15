package com.clerami.intermediate.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.clerami.intermediate.data.remote.retrofit.ApiService

class MapsViewModelFactory(private val apiService: ApiService) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}