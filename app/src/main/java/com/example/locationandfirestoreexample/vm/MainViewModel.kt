package com.example.locationandfirestoreexample.vm

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locationandfirestoreexample.PostModel
import com.example.locationandfirestoreexample.Resource
import com.example.locationandfirestoreexample.UserModel
import com.example.locationandfirestoreexample.repos.AuthRepoImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    private val repo: AuthRepoImpl by lazy { AuthRepoImpl() }
    val posts: MutableLiveData<Resource<List<PostModel>>> = MutableLiveData()

    fun getPosts(location: Location) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val result = repo.getPosts(location)
            posts.postValue(result)
        }
    }
}