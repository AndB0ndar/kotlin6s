package com.example.kotlin6s.model

import com.google.gson.annotations.SerializedName

data class GroupsResponse(
    @SerializedName("groupName") val groupName: String,
    @SerializedName("groupSuffix") val groupSuffix: String
)