package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SplatDao {
    // Server Config Queries
    @Query("SELECT * FROM server_configs ORDER BY id DESC")
    fun getAllServerConfigs(): Flow<List<ServerConfig>>

    @Query("SELECT * FROM server_configs WHERE isActive = 1 LIMIT 1")
    fun getActiveServerConfigFlow(): Flow<ServerConfig?>

    @Query("SELECT * FROM server_configs WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveServerConfig(): ServerConfig?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServerConfig(config: ServerConfig): Long

    @Update
    suspend fun updateServerConfig(config: ServerConfig)

    @Query("UPDATE server_configs SET isActive = 0")
    suspend fun deactivateAllConfigs()

    @Transaction
    suspend fun setActiveServerConfig(configId: Int) {
        deactivateAllConfigs()
        val config = getConfigById(configId)
        if (config != null) {
            updateServerConfig(config.copy(isActive = true))
        }
    }

    @Query("SELECT * FROM server_configs WHERE id = :id LIMIT 1")
    suspend fun getConfigById(id: Int): ServerConfig?

    @Query("DELETE FROM server_configs WHERE id = :id")
    suspend fun deleteServerConfig(id: Int)


    // Splat Job Queries
    @Query("SELECT * FROM splat_jobs ORDER BY timestamp DESC")
    fun getAllJobs(): Flow<List<SplatJob>>

    @Query("SELECT * FROM splat_jobs WHERE id = :id")
    suspend fun getJobById(id: Int): SplatJob?

    @Query("SELECT * FROM splat_jobs WHERE serverJobId = :serverJobId")
    suspend fun getJobByServerId(serverJobId: String): SplatJob?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: SplatJob): Long

    @Update
    suspend fun updateJob(job: SplatJob)

    @Query("DELETE FROM splat_jobs WHERE id = :id")
    suspend fun deleteJob(id: Int)
}
