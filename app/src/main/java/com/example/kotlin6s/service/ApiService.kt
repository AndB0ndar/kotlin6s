package com.example.kotlin6s.service

import com.example.kotlin6s.model.api.LoginResponse
import com.example.kotlin6s.model.api.UserGroup
import com.example.kotlin6s.model.api.Profile
import com.example.kotlin6s.model.api.Queue
import com.example.kotlin6s.model.api.QueueItemResponse
import com.example.kotlin6s.model.api.QueueReceiveRemote
import com.example.kotlin6s.model.api.TokenResponse
import com.example.kotlin6s.model.api.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface ApiService {
    @POST("/login")
    fun login(@Body loginReceiveRemote: LoginResponse): Call<TokenResponse>

    @POST("/register")
    fun register(@Body registerReceiveRemote: RegisterResponse): Call<TokenResponse>

    @GET("/profile")
    fun getProfile(): Call<Profile>

    @GET("/group/my")
    fun getMyGroup(): Call<UserGroup>

    @GET("/queues/all")
    suspend fun getAllQueues(): List<Queue>

    @POST("queues/create")
    fun createQueue(@Body queue: QueueReceiveRemote): Call<Int>

    @POST("connect/{id}")
    fun connectQueue(@Path("id") queueId: Int): Call<Int>

    @DELETE("queues/delete/{id}")
    fun deleteQueue(@Path("id") queueId: Int): Call<Boolean>

    @GET("/queues/{id}/queue/all")
    suspend fun getQueueById(@Path("id") queueId: Int): List<QueueItemResponse>

    @POST("/queues/{id}/queue/add")
    fun addQueueItem(@Path("id") queueId: Int): Call<Int>

    @DELETE("/queues/{id}/queue/delete")
    fun deleteQueueItem(@Path("id") queueId: Int): Call<Boolean>
}
