package ru.netology.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.auth.AuthState
import ru.netology.nework.repository.Repository
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val appAuth: AppAuth, private val repository: Repository) : ViewModel() {

    val dataAuth: LiveData<AuthState> = appAuth.authStateFlow
        .asLiveData(Dispatchers.Default)

    val authenticated: Boolean
        get() = appAuth.authStateFlow.value.id != 0

    fun registration(login: String, name: String, pass: String) {
        viewModelScope.launch {
            val photo = null
            repository.registration(login, name, pass, photo)
        }
    }

    fun login(login: String, pass: String) {
        viewModelScope.launch {
            repository.login(login, pass)
        }
    }

    fun logout() {
        repository.logout()
    }
}