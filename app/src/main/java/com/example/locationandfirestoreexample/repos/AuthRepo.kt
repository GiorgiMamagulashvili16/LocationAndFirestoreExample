package com.example.locationandfirestoreexample.repos

import android.location.Location
import com.example.locationandfirestoreexample.PostModel
import com.example.locationandfirestoreexample.Resource
import com.example.locationandfirestoreexample.UserModel
import com.google.firebase.auth.AuthResult

interface AuthRepo {
    suspend fun register(
        email: String,
        password: String,
        lat: Number,
        long: Number,
        userName: String,
    ): Resource<AuthResult>

    suspend fun getPosts(location: Location): Resource<List<PostModel>>
    suspend fun getUser(uid:String):Resource<UserModel>
    suspend fun addPost(text: String): Resource<Any>
    suspend fun logIn(email: String, password: String): Resource<AuthResult>
}