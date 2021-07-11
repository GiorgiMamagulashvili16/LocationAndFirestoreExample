package com.example.locationandfirestoreexample.repos

import android.location.Location
import android.util.Log.d
import com.example.locationandfirestoreexample.PostModel
import com.example.locationandfirestoreexample.Resource
import com.example.locationandfirestoreexample.UserModel
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class AuthRepoImpl : AuthRepo {
    val auth = FirebaseAuth.getInstance()
    val users = FirebaseFirestore.getInstance().collection("users")
    private val posts = FirebaseFirestore.getInstance().collection("posts")


    override suspend fun register(
        email: String,
        password: String,
        lat: Number,
        long: Number,
        userName: String,
    ): Resource<AuthResult> {
        return withContext(Dispatchers.IO) {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val uid = auth.currentUser?.uid!!
                val user = UserModel(uid, userName, lat, long)
                users.document(uid).set(user).await()
                Resource.Success(result)
            } catch (e: Exception) {
                Resource.Error(e.toString())
            }
        }
    }

    override suspend fun getPosts(location: Location): Resource<List<PostModel>> =
        withContext(Dispatchers.IO) {
            try {
                val uid = FirebaseAuth.getInstance().uid!!
                val nearbyPosts = mutableListOf<PostModel>()

                posts
                    .whereNotEqualTo("authorId", uid)
                    .get()
                    .await()
                    .toObjects(PostModel::class.java)
                    .onEach {
                        val user = getUser(it.authorId).data
                        it.authorUserName = user?.userName!!
                        val currentLoc = Location("currentUserLoc").apply {
                            longitude = location.longitude
                            latitude = location.latitude
                        }
                        d("POSTPOST","$currentLoc")
                        val userLoc = Location("userLoc").apply {
                            longitude = user.long.toDouble()
                            latitude = user.lat.toDouble()
                        }
                        d("POSTPOST","$userLoc")
                        if (currentLoc.distanceTo(userLoc) < 5000.0) {
                            nearbyPosts.add(it)
                        }
                    }
                d("POSTPOST","$nearbyPosts")
                return@withContext Resource.Success(nearbyPosts)
            } catch (e: Exception) {
                return@withContext Resource.Error(e.toString())
            }
        }

    override suspend fun getUser(uid: String): Resource<UserModel> = withContext(Dispatchers.IO) {
        try {
            val user = users.document(uid).get().await()
            val userr = UserModel(
                user["uid"] as String,
                user["userName"] as String,
                user["lat"] as Double,
                user["long"] as Double
            )
            return@withContext Resource.Success(userr)
        } catch (e: Exception) {
            return@withContext Resource.Error(e.toString())
        }
    }!!

    override suspend fun addPost(text: String): Resource<Any> {
        return try {
            val uid = auth.currentUser?.uid!!
            val postId = UUID.randomUUID().toString()
            val post = PostModel(postId, uid, text)
            posts.document(postId).set(post).await()
            Resource.Success(Any())
        } catch (e: Exception) {
            Resource.Error(e.toString())
        }
    }

    override suspend fun logIn(email: String, password: String): Resource<AuthResult> {
        return try {
            val loginResult = auth.signInWithEmailAndPassword(email, password).await()
            Resource.Success(loginResult)
        } catch (e: Exception) {
            Resource.Error(e.toString())
        }
    }
}