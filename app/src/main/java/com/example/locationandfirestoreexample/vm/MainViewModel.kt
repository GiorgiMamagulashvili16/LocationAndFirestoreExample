package com.example.locationandfirestoreexample.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locationandfirestoreexample.Resource
import com.example.locationandfirestoreexample.UserModel
import com.example.locationandfirestoreexample.repos.AuthRepoImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    private val repo: AuthRepoImpl by lazy { AuthRepoImpl() }
    val users: MutableLiveData<Resource<List<UserModel>>> = MutableLiveData()

    fun getUsers() = viewModelScope.launch {
        users.postValue(Resource.Loading())
        withContext(Dispatchers.IO) {
            val result = repo.getPosts()
            users.postValue(result)
        }
    }
}