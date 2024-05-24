package com.example.kotlin6s.model.api

import com.google.gson.annotations.SerializedName

data class QueueReceiveRemote(
    @SerializedName("queueName")
    val queueName: String,
    @SerializedName("group")
    val group: String,
    @SerializedName("description")
    val description: String,
)
