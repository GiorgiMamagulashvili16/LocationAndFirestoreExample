package com.example.locationandfirestoreexample

import com.google.gson.annotations.SerializedName

data class UserModel(
    val uid:String = "",
    val userName:String = "",
    val status:String = "",
    val lat:Number = 0.0,
    val long:Number = 0.0
)
