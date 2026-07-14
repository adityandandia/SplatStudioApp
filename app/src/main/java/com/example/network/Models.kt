package com.example.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class JobResponse(
    @Json(name = "id") val rawId: String? = null,
    @Json(name = "job_id") val jobIdSnake: String? = null,
    @Json(name = "jobId") val jobIdCamel: String? = null,
    @Json(name = "title") val rawTitle: String? = null,
    @Json(name = "status") val rawStatus: String? = null, // e.g. "colmap", "fastgs", "post_processing", "done", "failed"
    @Json(name = "progress") val rawProgress: Int? = null,
    @Json(name = "model_url") val modelUrlSnake: String? = null,
    @Json(name = "modelUrl") val modelUrlCamel: String? = null,
    @Json(name = "model") val modelUrlShort: String? = null,
    val timestamp: Long? = null
) {
    val id: String
        get() = rawId ?: jobIdSnake ?: jobIdCamel ?: ""

    val title: String
        get() = rawTitle ?: "Splat Video"

    val status: String
        get() = rawStatus ?: "COLMAP"

    val progress: Int
        get() = rawProgress ?: 0

    val modelUrl: String?
        get() = modelUrlSnake ?: modelUrlCamel ?: modelUrlShort
}

@JsonClass(generateAdapter = true)
data class UploadResponse(
    @Json(name = "success") val rawSuccess: Boolean? = null,
    @Json(name = "job_id") val jobIdSnake: String? = null,
    @Json(name = "jobId") val jobIdCamel: String? = null,
    @Json(name = "id") val idRaw: String? = null,
    val message: String? = null
) {
    val success: Boolean
        get() = rawSuccess ?: (jobIdSnake != null || jobIdCamel != null || idRaw != null)

    val jobId: String
        get() = jobIdSnake ?: jobIdCamel ?: idRaw ?: ""
}

@JsonClass(generateAdapter = true)
data class PingResponse(
    val status: String? = null,
    val message: String? = null,
    val version: String? = null,
    val gpu_available: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class RenderQuality(
    @Json(name = "psnr_mean") val psnrMean: Double,
    @Json(name = "ssim_mean") val ssimMean: Double,
    @Json(name = "lpips_mean") val lpipsMean: Double? = null,
    @Json(name = "num_held_out_views") val numHeldOutViews: Int
)

@JsonClass(generateAdapter = true)
data class MetricsData(
    @Json(name = "render_quality") val renderQuality: RenderQuality? = null
)

@JsonClass(generateAdapter = true)
data class MetricsStatusResponse(
    val status: String,
    val metrics: MetricsData? = null
)

@JsonClass(generateAdapter = true)
data class AuditEntry(
    val timestamp: String,
    val stage: String,
    val reason: String,
    @Json(name = "points_removed") val pointsRemoved: Int,
    val threshold: Double? = null
)

@JsonClass(generateAdapter = true)
data class AuditResponse(
    @Json(name = "run_started") val runStarted: String? = null,
    @Json(name = "input_file") val inputFile: String? = null,
    val entries: List<AuditEntry>? = null
)

