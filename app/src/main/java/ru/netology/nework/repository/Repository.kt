package ru.netology.nework.repository

import kotlinx.coroutines.flow.StateFlow
import ru.netology.nework.auth.AuthState
import ru.netology.nework.dto.UserResponse
import ru.netology.nework.model.AttachmentModel

interface Repository {
    val dataAuth: StateFlow<AuthState>

    suspend fun registration(
        login: String,
        name: String,
        pass: String,
        attachmentModel: AttachmentModel?
    )

    suspend fun login(login: String, pass: String)
    fun logout()

    suspend fun getUsers()
    suspend fun getUser(id: Int): UserResponse
}