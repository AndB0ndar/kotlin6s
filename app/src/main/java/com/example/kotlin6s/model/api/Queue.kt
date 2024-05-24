package com.example.kotlin6s.model.api

import com.google.gson.annotations.SerializedName

data class Queue (
    @SerializedName("id")
    val id: Int,

    @SerializedName("queueName")
    val queueName: String,

    @SerializedName("creatorToken")
    val creatorToken: String,

    @SerializedName("group")
    val group: Int,

    @SerializedName("description")
    val description: String
)