package ru.netology.nework.repository

import android.content.Context
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nework.R
import ru.netology.nework.api.ApiService
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.auth.AuthState
import ru.netology.nework.dao.EventDao
import ru.netology.nework.dao.JobDao
import ru.netology.nework.dao.PostDao
import ru.netology.nework.dao.RemoteKeyDao
import ru.netology.nework.dao.UserDao
import ru.netology.nework.db.AppDb
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.FeedItem
import ru.netology.nework.dto.Job
import ru.netology.nework.dto.Post
import ru.netology.nework.dto.UserResponse
import ru.netology.nework.entity.EventEntity
import ru.netology.nework.entity.PostEntity
import ru.netology.nework.entity.UserEntity
import ru.netology.nework.entity.toDto
import ru.netology.nework.error.ApiError
import ru.netology.nework.error.NetworkError
import ru.netology.nework.model.AttachmentModel
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val appAuth: AppAuth,
    private val postDao: PostDao,
    private val eventDao: EventDao,
    private val userDao: UserDao,
    private val jobDao: JobDao,
    postRemoteMediator: PostRemoteMediator,
    eventRemoteMediator: EventRemoteMediator,
    userRemoteMediator: UserRemoteMediator,
    postRemoteKeyDao: RemoteKeyDao,
    eventRemoteKeyDao: RemoteKeyDao,
    private val apiService: ApiService,
    override val appDb: AppDb
) : Repository {

    override val dataAuth: StateFlow<AuthState> = appAuth.authStateFlow

    @OptIn(ExperimentalPagingApi::class)
    override val dataPost: Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(pageSize = 4, enablePlaceholders = false),
        pagingSourceFactory = { postDao.getPagingSource() },
        remoteMediator = postRemoteMediator
    ).flow
        .map {
            it.map(PostEntity::toDto)
        }

    @OptIn(ExperimentalPagingApi::class)
    override val dataEvent: Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(pageSize = 4, enablePlaceholders = false),
        pagingSourceFactory = { eventDao.getPagingSource() },
        remoteMediator = eventRemoteMediator
    ).flow
        .map {
            it.map(EventEntity::toDto)
        }

    @OptIn(ExperimentalPagingApi::class)
    override val dataUsers: Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(pageSize = 4, enablePlaceholders = false),
        pagingSourceFactory = { userDao.getPagingSource() },
        remoteMediator = userRemoteMediator
    ).flow.map {
        it.map(UserEntity::toDto)
    }

    override val dataJob: Flow<List<Job>> = jobDao.getAll()
        .map { it.toDto() }
        .flowOn(Dispatchers.Default)

    private val _data = MutableLiveData<List<Job>>()

    override suspend fun register(
        login: String,
        name: String,
        pass: String,
        attachmentModel: AttachmentModel?
    ) {
        try {
            val response = if (attachmentModel != null) {
                val part = MultipartBody.Part.createFormData(
                    "file",
                    attachmentModel.file.name,
                    attachmentModel.file.asRequestBody()
                )
                apiService.usersRegistrationWithPhoto(login, pass, name, part)
            } else {
                apiService.usersRegistration(login, pass, name)
            }
            if (!response.isSuccessful) {
                when (response.code()) {
                    403 -> {
                        Toast.makeText(context, R.string.user_registered, LENGTH_SHORT).show()
                    }
                    415 -> {
                        Toast.makeText(context, R.string.incorrect_photo_format, LENGTH_SHORT).show()
                    }
                    else -> throw ApiError(response.code(), response.message())
                }
                return
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            appAuth.setAuth(body.id, body.token)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw ru.netology.nework.error.UnknownError
        }
    }

    override suspend fun login(login: String, pass: String) {
        TODO("Not yet implemented")
    }

    override fun logout() {
        TODO("Not yet implemented")
    }

    override suspend fun savePost(post: Post) {
        TODO("Not yet implemented")
    }

    override suspend fun savePostWithAttachment(post: Post, attachmentModel: AttachmentModel) {
        TODO("Not yet implemented")
    }

    override suspend fun likePost(post: Post) {
        TODO("Not yet implemented")
    }

    override suspend fun deletePost(id: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun saveEvent(event: Event) {
        TODO("Not yet implemented")
    }

    override suspend fun saveEventWithAttachment(event: Event, attachmentModel: AttachmentModel) {
        TODO("Not yet implemented")
    }

    override suspend fun likeEvent(event: Event) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteEvent(id: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun getUsers() {
        TODO("Not yet implemented")
    }

    override suspend fun getUser(id: Int): UserResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getMyJobs() {
        TODO("Not yet implemented")
    }

    override suspend fun getJobs(userId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun saveJob(job: Job) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteJob(id: Int) {
        TODO("Not yet implemented")
    }

}