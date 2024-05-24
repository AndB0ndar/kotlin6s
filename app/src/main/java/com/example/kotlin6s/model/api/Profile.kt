package com.example.kotlin6s.model.api

import com.google.gson.annotations.SerializedName

data class Profile(
    @SerializedName("id")
    val id: Int,
    @SerializedName("login")
    val login: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("groupId")
    val groupId: Int,
    @SerializedName("token")
    val token: String
)
