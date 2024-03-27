package ru.netology.nework.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import ru.netology.nework.auth.AuthState
import ru.netology.nework.db.AppDb
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.FeedItem
import ru.netology.nework.dto.Job
import ru.netology.nework.dto.Post
import ru.netology.nework.dto.UserResponse
import ru.netology.nework.model.AttachmentModel

interface Repository {
    val appDb: AppDb
    val dataAuth: StateFlow<AuthState>
    val dataPost: Flow<PagingData<FeedItem>>
    val dataEvent: Flow<PagingData<FeedItem>>
    val dataUsers: Flow<PagingData<FeedItem>>
    val dataJob: Flow<List<Job>>

    suspend fun register(
        login: String,
        name: String,
        pass: String,
        attachmentModel: AttachmentModel?
    )
    suspend fun login(login: String, pass: String)
    fun logout()

    suspend fun savePost(post: Post)
    suspend fun savePostWithAttachment(post: Post, attachmentModel: AttachmentModel)
    suspend fun likePost(post: Post)
    suspend fun deletePost(id: Int)

    suspend fun saveEvent(event: Event)
    suspend fun saveEventWithAttachment(event: Event, attachmentModel: AttachmentModel)
    suspend fun likeEvent(event: Event)
    suspend fun deleteEvent(id: Int)

    suspend fun getUsers()
    suspend fun getUser(id: Int): UserResponse

    suspend fun getMyJobs()
    suspend fun getJobs(userId: Int)
    suspend fun saveJob(job: Job)
    suspend fun deleteJob(id: Int)
}