package com.example.locationandfirestoreexample.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locationandfirestoreexample.Resource
import com.example.locationandfirestoreexample.repos.AuthRepoImpl
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddUserViewModel : ViewModel() {
    private val repo: AuthRepoImpl by lazy { AuthRepoImpl() }
    val registerResponse: MutableLiveData<Resource<AuthResult>> = MutableLiveData()
    fun register(
        email: String,
        password: String,
        lat: Number,
        long: Number,
        userName: String,
        status: String

    ) = viewModelScope.launch {
        registerResponse.postValue(Resource.Loading())
        withContext(Dispatchers.IO) {
            val response = repo.register(email, password, lat, long, userName, status)
            registerResponse.postValue(response)
        }
    }
}