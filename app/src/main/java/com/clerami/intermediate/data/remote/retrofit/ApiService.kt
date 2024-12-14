package com.clerami.intermediate.data.remote.retrofit


import com.clerami.intermediate.data.remote.response.AddNewStory
import com.clerami.intermediate.data.remote.response.AddNewStoryGuest
import com.clerami.intermediate.data.remote.response.DetailStory
import com.clerami.intermediate.data.remote.response.GetAllStories
import com.clerami.intermediate.data.remote.response.LoginRequest
import com.clerami.intermediate.data.remote.response.LoginResponse
import com.clerami.intermediate.data.remote.response.RegisterRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("register")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterRequest>


    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>


    @Multipart
    @POST("stories")
    fun addStory(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody
    ): Call<AddNewStory>


    @POST("addStoryGuest")
    fun addNewStoryGuest(@Body addNewStoryGuest: AddNewStoryGuest): Call<AddNewStoryGuest>

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): GetAllStories

    @GET("stories")
    fun getStories(
        @Header("Authorization") token: String,
        @Query("location") location : Int = 1,
    ): Call<GetAllStories>

    @GET("stories/{id}")
    fun getStoryDetail(
        @Path("id") storyId: String,
        @Header("Authorization") token: String
    ): Call<DetailStory>
}
