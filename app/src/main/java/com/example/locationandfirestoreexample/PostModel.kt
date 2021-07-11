package com.example.locationandfirestoreexample

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class PostModel(
    val postId: String = "",
    val authorId: String = "",
    val text: String = "",
    @get:Exclude var authorUserName: String = ""
)