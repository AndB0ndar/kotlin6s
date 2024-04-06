package com.example.kotlin6s.model

import com.google.gson.annotations.SerializedName


data class GroupResponse(
    @SerializedName("groupName") val groupName: String,
    @SerializedName("groupSuffix") val groupSuffix: String,
    @SerializedName("remoteFile") val remoteFile: String,
    @SerializedName("unitName") val unitName: String,
    @SerializedName("unitCourse") val unitCourse: String,
    @SerializedName("lessonsTimes") val lessonsTimes: List<List<String>>,
    @SerializedName("updatedDate") val updatedDate: String,
    @SerializedName("schedule") val schedule: List<DaySchedule>
)

data class DaySchedule(
    @SerializedName("day") val day: String,
    @SerializedName("odd") val odd: List<List<ScheduleItem>>,
    @SerializedName("even") val even: List<List<ScheduleItem>>
)

data class ScheduleItem(
    @SerializedName("weeks") val weeks: String?,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String,
    @SerializedName("tutor") val tutor: String,
    @SerializedName("place") val place: String,
    @SerializedName("link") val link: String?
)
