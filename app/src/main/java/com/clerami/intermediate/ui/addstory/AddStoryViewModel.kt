package com.clerami.intermediate.ui.addstory

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.clerami.intermediate.data.remote.response.AddNewStory
import com.clerami.intermediate.data.remote.retrofit.ApiConfig
import com.clerami.intermediate.utils.ImageUtils
import com.clerami.intermediate.utils.SessionManager
import com.clerami.intermediate.utils.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class AddStoryViewModel : ViewModel() {

    private val _uploadResult = MutableLiveData<String>()
    val uploadResult: LiveData<String> get() = _uploadResult

    private val apiService = ApiConfig.getApiService()

    fun uploadStory(
        context: Context,
        description: String,
        photoUri: Uri?,
        lat: Float?,
        lon: Float?
    ) {
        if (description.isEmpty() || photoUri == null) {
            _uploadResult.value = "Please provide description and photo"
            return
        }

        var file = getFileFromUri(context, photoUri)

        if (file == null || !file.exists() || !file.isFile) {
            _uploadResult.value = "Invalid file"
            return
        }


        if (file.length() > 1 * 1024 * 1024) {
            val compressedFile = ImageUtils.compressImage(context, photoUri, file)
            if (compressedFile != null) {
                file = compressedFile
            } else {
                _uploadResult.value = "Failed to compress image"
                return
            }
        }


        val descriptionBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
        val photoPart = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            file.asRequestBody("image/*".toMediaTypeOrNull())
        )


        val latBody = lat?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
        val lonBody = lon?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())


        val token = getTokenFromSession(context) ?: return


        if (latBody != null) {
            if (lonBody != null) {
                apiService.addStory("Bearer $token", descriptionBody, photoPart, latBody, lonBody)
                    .enqueue(object : Callback<AddNewStory> {
                        override fun onResponse(
                            call: Call<AddNewStory>,
                            response: Response<AddNewStory>
                        ) {
                            if (response.isSuccessful && response.body() != null) {
                                val message = response.body()?.message
                                _uploadResult.value = message ?: "Success"
                                Log.d("AddStoryViewModel", "Lat: $lat, Lon: $lon")

                            } else {
                                _uploadResult.value =
                                    "Failed to upload story: ${response.message()}"
                            }
                        }

                        override fun onFailure(call: Call<AddNewStory>, t: Throwable) {
                            _uploadResult.value = "Error: ${t.message}"
                        }
                    })
            }
        }
    }


    fun getFileFromUri(context: Context, uri: Uri): File? {
        try {

            if (uri.scheme.equals("content", ignoreCase = true)) {
                return copyContentUriToTempFile(context, uri)
            } else if (uri.scheme.equals("file", ignoreCase = true)) {
                return File(uri.path)
            }
        } catch (e: Exception) {
            Log.e("AddStoryViewModel", "Error getting file from URI: ", e)
        }
        return null
    }

    fun copyContentUriToTempFile(context: Context, uri: Uri): File? {
        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null
        try {
            inputStream = context.contentResolver.openInputStream(uri)
            val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            outputStream = FileOutputStream(tempFile)
            inputStream?.copyTo(outputStream)
            Log.d("AddStoryViewModel", "Copied content URI to temp file: ${tempFile.absolutePath}")
            return tempFile
        } catch (e: Exception) {

            Toast.makeText(context, "Error copying file, please try again.", Toast.LENGTH_SHORT)
                .show()
            Log.e("AddStoryViewModel", "Error copying content URI to temp file: ", e)
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
        return null
    }


    private fun getTokenFromSession(context: Context): String? {
        val loginResult = SessionManager.getLoginResult(context)
        return loginResult?.token
    }


}

