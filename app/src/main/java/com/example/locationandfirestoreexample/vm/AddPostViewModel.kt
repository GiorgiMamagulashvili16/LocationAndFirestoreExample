package com.example.locationandfirestoreexample.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locationandfirestoreexample.Resource
import com.example.locationandfirestoreexample.repos.AuthRepoImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddPostViewModel : ViewModel() {
    private val repo: AuthRepoImpl by lazy { AuthRepoImpl() }
    private var _addPost = MutableLiveData<Resource<Any>>()
    val addPost: LiveData<Resource<Any>> = MutableLiveData()

    fun addPost(text: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
           val res =  repo.addPost(text)
            _addPost.postValue(res)
        }
    }
}