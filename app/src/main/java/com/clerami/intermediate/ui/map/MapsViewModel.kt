package com.clerami.intermediate.ui.map


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.clerami.intermediate.data.remote.response.GetAllStories
import com.clerami.intermediate.data.remote.response.ListStoryItem
import com.clerami.intermediate.data.remote.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(private val apiService: ApiService) : ViewModel() {

    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> get() = _stories

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchStories(token: String, location: Int) {
        apiService.getStories("Bearer $token", location).enqueue(object : Callback<GetAllStories> {
            override fun onResponse(call: Call<GetAllStories>, response: Response<GetAllStories>) {
                if (response.isSuccessful) {

                    response.body()?.listStory?.filterNotNull()?.let {
                        _stories.value = it
                    } ?: run {
                        _errorMessage.value = "No valid stories found"
                    }
                } else {
                    _errorMessage.value = "Error: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<GetAllStories>, t: Throwable) {
                _errorMessage.value = "Network error: ${t.localizedMessage}"
            }
        })
    }
}


