package com.example.ensiplant.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface CloudinaryApi {
    @Multipart
    @POST("v1_1/dok87selt/image/upload") // Ganti sesuai cloud name kamu
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("upload_preset") uploadPreset: RequestBody
    ): Response<CloudinaryUploadResponse>
}
