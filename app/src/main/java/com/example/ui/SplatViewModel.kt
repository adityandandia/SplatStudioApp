package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ServerConfig
import com.example.data.SplatJob
import com.example.data.SplatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

class SplatViewModel(
    application: Application,
    private val repository: SplatRepository
) : AndroidViewModel(application) {

    // Active Server URL and States
    val activeServerConfig: StateFlow<ServerConfig?> = repository.activeConfig
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val serverConfigs: StateFlow<List<ServerConfig>> = repository.allConfigs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val splatJobs: StateFlow<List<SplatJob>> = repository.allJobs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active connection state for the selected server
    private val _isServerOnline = MutableStateFlow<Boolean?>(null)
    val isServerOnline: StateFlow<Boolean?> = _isServerOnline.asStateFlow()

    private val _isTestingConnection = MutableStateFlow(false)
    val isTestingConnection: StateFlow<Boolean> = _isTestingConnection.asStateFlow()

    // Map of running polling coroutines per job ID to prevent double polling
    private val activePollingJobs = mutableMapOf<Int, Job>()

    init {
        // Automatically check connection whenever active server config changes
        viewModelScope.launch {
            activeServerConfig.collect { config ->
                if (config != null) {
                    checkActiveServerConnection(config.url)
                } else {
                    _isServerOnline.value = null
                }
            }
        }

        // Start a coordinator that watches the jobs list and schedules/cancels polling
        viewModelScope.launch {
            splatJobs.collect { jobs ->
                val activeJobs = jobs.filter { job ->
                    job.status in listOf("Uploading", "Processing", "COLMAP", "FastGS", "Post-processing")
                }

                // Start polling for new active jobs
                for (job in activeJobs) {
                    if (!activePollingJobs.containsKey(job.id)) {
                        startPollingForJob(job.id)
                    }
                }

                // Stop polling for jobs that are no longer active (or deleted)
                val activeJobIds = activeJobs.map { it.id }.toSet()
                val runningJobIds = activePollingJobs.keys.toList()
                for (jobId in runningJobIds) {
                    if (!activeJobIds.contains(jobId)) {
                        activePollingJobs[jobId]?.cancel()
                        activePollingJobs.remove(jobId)
                    }
                }
            }
        }
    }

    /**
     * Test ping to the active or custom server URL.
     */
    fun checkActiveServerConnection(url: String) {
        viewModelScope.launch {
            _isTestingConnection.value = true
            val response = repository.testServerConnection(url)
            _isServerOnline.value = response != null
            _isTestingConnection.value = false
        }
    }

    /**
     * Add a new server configuration.
     */
    fun addServerConfig(name: String, url: String, makeActive: Boolean = false) {
        viewModelScope.launch {
            val formattedUrl = url.trim()
            val newConfig = ServerConfig(name = name, url = formattedUrl, isActive = makeActive)
            val insertedId = repository.insertServerConfig(newConfig)
            if (makeActive) {
                repository.setActiveServerConfig(insertedId.toInt())
            }
        }
    }

    /**
     * Select and activate a server configuration.
     */
    fun selectActiveServer(configId: Int) {
        viewModelScope.launch {
            repository.setActiveServerConfig(configId)
        }
    }

    /**
     * Delete a server configuration.
     */
    fun deleteServerConfig(configId: Int) {
        viewModelScope.launch {
            repository.deleteServerConfig(configId)
        }
    }

    /**
     * Create a new job locally and launch the asynchronous upload task.
     */
    fun startSplatConversion(title: String, videoFile: File) {
        viewModelScope.launch {
            // 1. Save local job entry in Room
            val initialJob = SplatJob(
                title = title,
                videoPath = videoFile.absolutePath,
                serverJobId = null,
                status = "Local",
                progress = 5,
                modelUrl = null
            )
            val insertedId = repository.insertJob(initialJob).toInt()

            // 2. Launch background upload
            viewModelScope.launch(Dispatchers.IO) {
                val uploadSuccess = repository.uploadVideo(insertedId, videoFile, title)
                if (uploadSuccess) {
                    Log.d("SplatViewModel", "Video upload successful. Server processing started.")
                } else {
                    Log.e("SplatViewModel", "Video upload failed.")
                }
            }
        }
    }

    /**
     * Delete a job from the list.
     */
    fun deleteJob(jobId: Int) {
        viewModelScope.launch {
            repository.deleteJob(jobId)
        }
    }

    /**
     * Spawns a dedicated polling loop for an active job.
     */
    private fun startPollingForJob(jobId: Int) {
        val job = viewModelScope.launch {
            while (true) {
                delay(3000) // Poll every 3 seconds
                val isTerminal = repository.pollJobStatus(jobId)
                if (isTerminal) {
                    break
                }
            }
        }
        activePollingJobs[jobId] = job
    }

    override fun onCleared() {
        super.onCleared()
        // Cancel all active polling tasks when ViewModel is cleared
        activePollingJobs.values.forEach { it.cancel() }
        activePollingJobs.clear()
    }
}

class SplatViewModelFactory(
    private val application: Application,
    private val repository: SplatRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SplatViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
