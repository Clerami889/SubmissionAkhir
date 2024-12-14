package com.clerami.intermediate.utils

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


fun String.toMediaTypeOrNull(): MediaType {
    return this.toMediaType()
}



fun File.toRequestBody(mediaType: String): RequestBody {
    return this.asRequestBody(mediaType.toMediaTypeOrNull())
}

fun File.toMultipartBodyPart(paramName: String): MultipartBody.Part {
    return MultipartBody.Part.createFormData(paramName, this.name, this.toRequestBody("image/*"))
}
