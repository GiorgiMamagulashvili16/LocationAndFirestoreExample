package com.example.locationandfirestoreexample.repos

import com.example.locationandfirestoreexample.Resource
import com.example.locationandfirestoreexample.UserModel
import com.google.firebase.auth.AuthResult

interface AuthRepo {
    suspend fun register(
        email: String,
        password: String,
        lat:Number,
        long:Number,
        userName:String,
        status:String
    ): Resource<AuthResult>
    suspend fun getPosts():Resource<List<UserModel>>
}