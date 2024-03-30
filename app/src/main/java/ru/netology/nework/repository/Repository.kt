package ru.netology.nework.repository

import ru.netology.nework.dto.UserResponse
import ru.netology.nework.error.ApiErrorAuth
import ru.netology.nework.model.AttachmentModel

interface Repository {

    suspend fun registration(
        login: String,
        name: String,
        pass: String,
        attachmentModel: AttachmentModel?
    ): ApiErrorAuth

    suspend fun login(login: String, pass: String): ApiErrorAuth
    fun logout()

    suspend fun getUsers()
    suspend fun getUser(id: Int): UserResponse
}