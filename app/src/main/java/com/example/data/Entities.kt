package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "server_configs")
data class ServerConfig(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val url: String,
    val isActive: Boolean = false
)

@Entity(tableName = "splat_jobs")
data class SplatJob(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val videoPath: String?,
    val serverJobId: String?,
    val status: String, // "Local", "Uploading", "COLMAP", "FastGS", "Post-processing", "Done", "Failed"
    val progress: Int, // 0 - 100
    val modelUrl: String?, // URL of generated 3JS html or splat ply
    val timestamp: Long = System.currentTimeMillis()
)
