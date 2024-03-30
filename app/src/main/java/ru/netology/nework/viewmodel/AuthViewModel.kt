package ru.netology.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.auth.AuthState
import ru.netology.nework.error.ApiErrorAuth
import ru.netology.nework.error.NetworkError
import ru.netology.nework.error.UnknownError
import ru.netology.nework.repository.Repository
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val appAuth: AppAuth,
    private val repository: Repository
) : ViewModel() {

    val dataAuth: LiveData<AuthState> = appAuth.authStateFlow
        .asLiveData(Dispatchers.Default)

    val authenticated: Boolean
        get() = appAuth.authStateFlow.value.id != 0

    suspend fun registration(login: String, name: String, pass: String): ApiErrorAuth {
        val photo = null
        try {
           return repository.registration(login, name, pass, photo)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    suspend fun login(login: String, pass: String): ApiErrorAuth {
        try {
            return repository.login(login, pass)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    fun logout() {
        repository.logout()
    }
}