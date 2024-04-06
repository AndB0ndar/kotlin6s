package com.example.kotlin6s.viewmodule
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlin6s.model.GroupResponse
import com.example.kotlin6s.service.RetrofitClient
import com.example.kotlin6s.service.SubjectService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeViewModel: ViewModel() {
    private val apiService: SubjectService = RetrofitClient.getRetrofitInstance().create(SubjectService::class.java)
    private val groupLiveData: MutableLiveData<List<GroupResponse?>> = MutableLiveData()
    private val searchText: MutableLiveData<String> = MutableLiveData()
    private val errorLiveData: MutableLiveData<String> = MutableLiveData()

    fun getError(): LiveData<String> {
        return errorLiveData
    }
    fun getGroupData(groupName: String?): LiveData<List<GroupResponse?>> {
        apiService.getGroupData(groupName)?.enqueue(object : Callback<List<GroupResponse?>> {
            override fun onResponse(
                call: Call<List<GroupResponse?>>,
                response: Response<List<GroupResponse?>>
            ) {
                if (response.isSuccessful) {
                    groupLiveData.postValue(response.body())
                } else {
                    Log.d("HomeViewModel", "Response ERROR: ${response.code()} - ${response.message()}")
                    errorLiveData.postValue(response.message())
                }
            }
            override fun onFailure(call: Call<List<GroupResponse?>>, t: Throwable) {
                Log.d("HomeViewModel", "onFailure: ${t.message}")
                errorLiveData.postValue(t.message)
            }
        })
        return groupLiveData
    }

    fun saveEditTextState(text: String) {
        searchText.value = text
    }

    fun getSavedEditTextState(): LiveData<String> {
        return searchText
    }
}

