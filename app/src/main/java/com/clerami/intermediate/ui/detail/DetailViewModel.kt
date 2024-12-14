package com.clerami.intermediate.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.clerami.intermediate.data.remote.retrofit.ApiConfig
import com.clerami.intermediate.data.remote.response.DetailStory
import com.clerami.intermediate.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel(application: Application) : AndroidViewModel(application) {

    val storyDetailLiveData = MutableLiveData<DetailStory>()
    val errorLiveData = MutableLiveData<String>()

    private val apiService = ApiConfig.getApiService()

    fun fetchStoryDetail(storyId: String) {
        val token = SessionManager.getLoginResult(getApplication())?.token
        if (token == null) {
            errorLiveData.value = "Authorization token is missing"
            return
        }

        apiService.getStoryDetail(storyId, "Bearer $token").enqueue(object : Callback<DetailStory> {
            override fun onResponse(call: Call<DetailStory>, response: Response<DetailStory>) {
                if (response.isSuccessful && response.body() != null) {
                    storyDetailLiveData.value = response.body()
                } else {
                    errorLiveData.value = "Failed to fetch story details"
                }
            }

            override fun onFailure(call: Call<DetailStory>, t: Throwable) {
                errorLiveData.value = "Error: ${t.message}"
            }
        })
    }
}
