package com.clerami.intermediate.ui.story

import android.content.Context
import android.widget.Toast

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.clerami.intermediate.data.remote.response.ListStoryItem
import com.clerami.intermediate.data.remote.retrofit.ApiService
import kotlinx.coroutines.launch


class StoryViewModel(
    private val storyRepository: StoryRepository,
    private val context: Context,
    private val apiService: ApiService
) : ViewModel() {

    private val _stories = MutableLiveData<PagingData<ListStoryItem>>()
    val stories: LiveData<PagingData<ListStoryItem>> = _stories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun getStories(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            Toast.makeText(context, "Loading stories...", Toast.LENGTH_SHORT).show()
            try {
                storyRepository.getStories("Bearer $token").collect { pagingData ->
                    _stories.value = pagingData
                    Toast.makeText(context, "Stories loaded successfully", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (exception: Exception) {
                _isLoading.value = false
                Toast.makeText(
                    context,
                    "Error loading stories: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                _isLoading.value = false
            }
        }
    }
}