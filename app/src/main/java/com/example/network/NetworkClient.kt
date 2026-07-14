package com.example.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object NetworkClient {
    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val baseOkHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val headerInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .header("ngrok-skip-browser-warning", "true")
                .build()
            chain.proceed(request)
        }
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .addInterceptor(headerInterceptor)
            .addInterceptor(logging)
            .build()
    }

    private var activeUrl: String? = null
    private var cachedService: SplatApiService? = null

    /**
     * Get or create a SplatApiService for the specified base URL.
     */
    fun getService(baseUrl: String): SplatApiService {
        val sanitizedUrl = sanitizeUrl(baseUrl)
        if (activeUrl == sanitizedUrl && cachedService != null) {
            return cachedService!!
        }

        synchronized(this) {
            if (activeUrl == sanitizedUrl && cachedService != null) {
                return cachedService!!
            }

            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl(sanitizedUrl)
                    .client(baseOkHttpClient)
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .build()

                val service = retrofit.create(SplatApiService::class.java)
                activeUrl = sanitizedUrl
                cachedService = service
                return service
            } catch (e: Exception) {
                // If invalid URL, throw an friendly exception
                throw IllegalArgumentException("Invalid URL: $sanitizedUrl", e)
            }
        }
    }

    /**
     * Helper to ensure the base URL starts with http:// or https:// and ends with a slash.
     */
    fun sanitizeUrl(url: String): String {
        var cleanUrl = url.trim()
        if (!cleanUrl.startsWith("http://") && !cleanUrl.startsWith("https://")) {
            cleanUrl = "http://$cleanUrl"
        }
        if (!cleanUrl.endsWith("/")) {
            cleanUrl = "$cleanUrl/"
        }
        return cleanUrl
    }
}
