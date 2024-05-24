package com.example.kotlin6s.model.api

import com.google.gson.annotations.SerializedName

data class UserGroup (
    @SerializedName("id")
    val id: Int,
    @SerializedName("groupName")
    val groupName: String,
    @SerializedName("groupSuffix")
    val groupSuffix: String,
    @SerializedName("unitName")
    val unitName: String,
    @SerializedName("unitCourse")
    val unitCourse: String
)
