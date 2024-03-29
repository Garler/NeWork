package ru.netology.nework.auth

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val _authStateFlow = MutableStateFlow(
        AuthState(
            prefs.getInt("id", 0),
            prefs.getString("token", null)
        )
    )
    val authStateFlow: StateFlow<AuthState> = _authStateFlow.asStateFlow()

    @Synchronized
    fun setAuth(id: Int, token: String) {
        _authStateFlow.value = AuthState(id, token)
        with(prefs.edit()) {
            putInt("id", id)
            putString("token", token)
            commit()
        }
    }

    @Synchronized
    fun removeAuth() {
        _authStateFlow.value = AuthState(0, null)
        with(prefs.edit()) {
            clear()
            commit()
        }
    }
}

data class AuthState(val id: Int = 0, val token: String? = null)