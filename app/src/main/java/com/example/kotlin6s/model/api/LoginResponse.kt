package com.example.kotlin6s.model.api


import com.google.gson.annotations.SerializedName

class LoginResponse (
    @SerializedName("login")
    val login: String,

    @SerializedName("password")
    val password: String
)
