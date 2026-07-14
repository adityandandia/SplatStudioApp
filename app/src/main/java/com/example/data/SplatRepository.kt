package com.example.data

import android.util.Log
import com.example.network.NetworkClient
import com.example.network.PingResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class SplatRepository(private val splatDao: SplatDao) {

    val allJobs: Flow<List<SplatJob>> = splatDao.getAllJobs()
    val allConfigs: Flow<List<ServerConfig>> = splatDao.getAllServerConfigs()
    val activeConfig: Flow<ServerConfig?> = splatDao.getActiveServerConfigFlow()

    suspend fun getActiveConfigSnapshot(): ServerConfig? = withContext(Dispatchers.IO) {
        splatDao.getActiveServerConfig()
    }

    suspend fun insertServerConfig(config: ServerConfig): Long = withContext(Dispatchers.IO) {
        splatDao.insertServerConfig(config)
    }

    suspend fun deleteServerConfig(configId: Int) = withContext(Dispatchers.IO) {
        splatDao.deleteServerConfig(configId)
    }

    suspend fun setActiveServerConfig(configId: Int) = withContext(Dispatchers.IO) {
        splatDao.setActiveServerConfig(configId)
    }

    suspend fun insertJob(job: SplatJob): Long = withContext(Dispatchers.IO) {
        splatDao.insertJob(job)
    }

    suspend fun updateJob(job: SplatJob) = withContext(Dispatchers.IO) {
        splatDao.updateJob(job)
    }

    suspend fun deleteJob(jobId: Int) = withContext(Dispatchers.IO) {
        splatDao.deleteJob(jobId)
    }

    suspend fun getJobById(jobId: Int): SplatJob? = withContext(Dispatchers.IO) {
        splatDao.getJobById(jobId)
    }

    /**
     * Test if a server is online by sending a ping request to it.
     */
    suspend fun testServerConnection(url: String): PingResponse? = withContext(Dispatchers.IO) {
        try {
            val service = NetworkClient.getService(url)
            service.ping()
        } catch (e: Exception) {
            Log.e("SplatRepository", "Ping failed for URL: $url", e)
            null
        }
    }

    /**
     * Uploads the recorded video to the currently active server.
     */
    suspend fun uploadVideo(jobId: Int, videoFile: File, title: String): Boolean = withContext(Dispatchers.IO) {
        val config = getActiveConfigSnapshot()
        if (config == null) {
            Log.e("SplatRepository", "No active server configured.")
            updateJobStatus(jobId, "Failed", 0, "No active server configured.")
            return@withContext false
        }

        try {
            updateJobStatus(jobId, "Uploading", 10, null)
            val service = NetworkClient.getService(config.url)

            val videoBody = videoFile.asRequestBody("video/mp4".toMediaTypeOrNull())
            val videoPart = MultipartBody.Part.createFormData("video", videoFile.name, videoBody)
            val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = service.uploadVideo(videoPart, titleBody)
            if (response.success) {
                val dbJob = splatDao.getJobById(jobId)
                if (dbJob != null) {
                    splatDao.updateJob(
                        dbJob.copy(
                            serverJobId = response.jobId,
                            status = "COLMAP", // Initial server state
                            progress = 25
                        )
                    )
                }
                return@withContext true
            } else {
                updateJobStatus(jobId, "Failed", 0, response.message ?: "Upload rejected by server.")
                return@withContext false
            }
        } catch (e: Exception) {
            Log.e("SplatRepository", "Video upload failed for job $jobId", e)
            updateJobStatus(jobId, "Failed", 0, e.message ?: "Network upload error.")
            false
        }
    }

    /**
     * Pulls the latest job status from the server and updates our local database.
     */
    suspend fun pollJobStatus(jobId: Int): Boolean = withContext(Dispatchers.IO) {
        val job = splatDao.getJobById(jobId) ?: return@withContext false
        val serverJobId = job.serverJobId ?: return@withContext false
        val config = getActiveConfigSnapshot() ?: return@withContext false

        try {
            val service = NetworkClient.getService(config.url)
            val response = service.getJobStatus(serverJobId)

            // Map server status string to our display statuses
            // "colmap" -> "COLMAP" (progress 30)
            // "fastgs" -> "FastGS" (progress 60)
            // "post_processing" -> "Post-processing" (progress 80)
            // "done" -> "Done" (progress 100)
            // "failed" -> "Failed" (progress 0)
            val localStatus = when (response.status.lowercase()) {
                "colmap" -> "COLMAP"
                "fastgs" -> "FastGS"
                "post_processing", "anisotropy" -> "Post-processing"
                "processing" -> "Processing"
                "done", "completed" -> "Done"
                "failed" -> "Failed"
                else -> response.status
            }

            val finalModelUrl = if (localStatus == "Done") {
                // If the returned modelUrl is relative, prepend server base url
                val rawUrl = response.modelUrl
                if (rawUrl != null && !rawUrl.startsWith("http://") && !rawUrl.startsWith("https://")) {
                    NetworkClient.sanitizeUrl(config.url) + rawUrl.removePrefix("/")
                } else {
                    rawUrl
                }
            } else {
                job.modelUrl
            }

            splatDao.updateJob(
                job.copy(
                    status = localStatus,
                    progress = response.progress,
                    modelUrl = finalModelUrl
                )
            )

            return@withContext localStatus == "Done" || localStatus == "Failed"
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 404) {
                Log.e("SplatRepository", "Job $jobId not found on server (404) — marking failed.")
                splatDao.updateJob(job.copy(status = "Failed", modelUrl = "Job not found on server (server likely restarted)."))
                return@withContext true
            }
            Log.e("SplatRepository", "Failed to poll job status for $jobId", e)
            return@withContext false
        } catch (e: Exception) {
            Log.e("SplatRepository", "Failed to poll job status for $jobId", e)
            // Do not fail immediately on transient networking errors while polling, just return false so we retry
            return@withContext false
        }
    }

    private suspend fun updateJobStatus(jobId: Int, status: String, progress: Int, errorMsg: String?) {
        val job = splatDao.getJobById(jobId)
        if (job != null) {
            splatDao.updateJob(
                job.copy(
                    status = status,
                    progress = progress,
                    modelUrl = errorMsg // Reuse modelUrl field to show error messages if failed
                )
            )
        }
    }
}
