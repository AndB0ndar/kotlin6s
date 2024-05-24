package com.example.kotlin6s.model.api

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("login")
    val login: String,

    @SerializedName("firstName")
    val firstName: String,

    @SerializedName("lastName")
    val lastName: String,

    @SerializedName("group")
    val group: String,

    @SerializedName("password")
    val password: String
)
