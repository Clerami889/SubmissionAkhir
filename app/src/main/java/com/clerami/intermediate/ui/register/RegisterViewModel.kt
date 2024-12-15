package com.clerami.intermediate.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.clerami.intermediate.data.remote.response.RegisterRequest
import com.clerami.intermediate.data.remote.retrofit.ApiService
import com.clerami.intermediate.utils.Resource


import kotlinx.coroutines.Dispatchers
import org.json.JSONObject


class RegisterViewModel(private val apiService: ApiService) : ViewModel() {

    fun register(email: String, password: String, username: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading())


        if (!isValidEmail(email)) {
            emit(Resource.error("Invalid email format"))
            return@liveData
        }

        try {

            val response =
                apiService.register(RegisterRequest(username, email, password)).execute()


            if (response.isSuccessful) {
                emit(Resource.success(response.body()))
            } else {

                val errorMessage = try {
                    val errorJson = JSONObject(response.errorBody()?.string())
                    errorJson.getString("message")
                } catch (e: Exception) {
                    "Registration failed: Unknown error"
                }
                emit(Resource.error("Registration failed: $errorMessage"))
            }
        } catch (e: Exception) {
            emit(Resource.error("Network error: ${e.message}"))
        }
    }


    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }


}

class RegisterViewModelFactory(private val apiService: ApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
