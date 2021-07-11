package com.example.locationandfirestoreexample.vm

import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locationandfirestoreexample.Resource
import com.example.locationandfirestoreexample.repos.AuthRepoImpl
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : ViewModel() {
    private var _login = MutableLiveData<Resource<AuthResult>>()
    val login: LiveData<Resource<AuthResult>> = _login

    private val repo: AuthRepoImpl by lazy { AuthRepoImpl() }

    fun logIn(email: String, password: String, editText: AppCompatEditText) {
        if (email.isNotBlank() && password.isNotBlank()) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    val result = repo.logIn(email, password)
                    _login.postValue(result)
                }
            }
        } else {
            Snackbar.make(editText, "Please Fill", Snackbar.LENGTH_SHORT).show()
        }
    }
}