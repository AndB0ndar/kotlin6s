package com.example.kotlin6s.service

import com.example.kotlin6s.model.GroupResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface SubjectService {
    @GET("groups/certain")
    fun getGroupData(@Query("name") groupName: String?): Call<List<GroupResponse?>>?
}
