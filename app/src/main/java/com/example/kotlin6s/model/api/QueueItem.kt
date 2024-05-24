package com.example.kotlin6s.model.api

import com.google.gson.annotations.SerializedName

data class QueueItemResponse(
    @SerializedName("queueName")
    val queueName: String,
    @SerializedName("userLogin")
    val userLogin: String,
    @SerializedName("position")
    val position: Int
)
