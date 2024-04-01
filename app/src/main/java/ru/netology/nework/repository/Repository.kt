package ru.netology.nework.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nework.dto.FeedItem
import ru.netology.nework.dto.UserResponse
import ru.netology.nework.error.ApiErrorAuth
import ru.netology.nework.model.AttachmentModel

interface Repository {

    val dataUser: Flow<PagingData<FeedItem>>

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