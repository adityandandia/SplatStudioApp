package com.example.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface SplatApiService {
    @GET("api/ping")
    suspend fun ping(): PingResponse

    @Multipart
    @POST("api/upload")
    suspend fun uploadVideo(
        @Part video: MultipartBody.Part,
        @Part("title") title: RequestBody
    ): UploadResponse

    @GET("api/jobs/{id}")
    suspend fun getJobStatus(
        @Path("id") jobId: String
    ): JobResponse

    @GET("api/jobs")
    suspend fun getAllServerJobs(): List<JobResponse>

    @GET("api/status/{id}")
    suspend fun getStatusWithApi(
        @Path("id") jobId: String
    ): MetricsStatusResponse

    @GET("status/{id}")
    suspend fun getStatusNoApi(
        @Path("id") jobId: String
    ): MetricsStatusResponse

    @GET("api/audit/{id}")
    suspend fun getAuditWithApi(
        @Path("id") jobId: String
    ): AuditResponse

    @GET("audit/{id}")
    suspend fun getAuditNoApi(
        @Path("id") jobId: String
    ): AuditResponse

    @Streaming
    @GET
    suspend fun downloadPointCloud(
        @Url fileUrl: String
    ): ResponseBody
}

