package com.example.kotlin6s.service

import com.example.kotlin6s.AuthManager
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {
    private const val BASE_URL = "http://172.20.10.2:8080/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        val token = AuthManager.getToken()

        val newUrl = token?.let {
            val baseUrl = BASE_URL.toHttpUrlOrNull()!!
            baseUrl.newBuilder()
                .addPathSegment(it)  // Add the token
                .addPathSegments(originalUrl.encodedPath.substring(1))
                .encodedQuery(originalUrl.encodedQuery)
                .build()
        } ?: originalUrl

        val modifiedRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        chain.proceed(modifiedRequest)
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    private fun addAuthorizationHeaderIfNeeded(request: Request): Boolean {
        val path = request.url.encodedPath
        return !excludedPaths.contains(path)
    }

    private val excludedPaths = listOf("/login", "/register")
}
