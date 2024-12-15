package com.clerami.intermediate.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.clerami.intermediate.data.remote.response.LoginRequest
import com.clerami.intermediate.data.remote.retrofit.ApiService

import com.clerami.intermediate.utils.Resource

import kotlinx.coroutines.Dispatchers
import org.json.JSONObject

class LoginViewModel(private val apiService: ApiService) : ViewModel() {

    fun login(email: String, password: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading())
        try {
            val response = apiService.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.success(response.body()))
            } else {
                val errorMessage = try {
                    val errorJson = JSONObject(response.errorBody()?.string())
                    errorJson.getString("message")
                } catch (e: Exception) {
                    "An unexpected error occurred. Please try again."
                }
                emit(Resource.error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Resource.error("Network error: ${e.message}"))
        } finally {

        }
    }
}



class LoginViewModelFactory(
    private val apiService: ApiService,

) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(apiService) as T // Pass both dependencies
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

