package ru.netology.nework.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import ru.netology.nework.api.ApiService
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.dao.PostDao
import ru.netology.nework.dao.UserDao
import ru.netology.nework.dto.Attachment
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.FeedItem
import ru.netology.nework.dto.Job
import ru.netology.nework.dto.Media
import ru.netology.nework.dto.Post
import ru.netology.nework.dto.UserResponse
import ru.netology.nework.entity.PostEntity
import ru.netology.nework.entity.UserEntity
import ru.netology.nework.entity.toEntity
import ru.netology.nework.error.ApiError
import ru.netology.nework.error.ApiErrorAuth
import ru.netology.nework.error.NetworkError
import ru.netology.nework.error.UnknownError
import ru.netology.nework.model.AttachmentModel
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OptIn(ExperimentalPagingApi::class)
class RepositoryImpl @Inject constructor(
    private val appAuth: AppAuth,
    private val apiService: ApiService,
    private val userDao: UserDao,
    userRemoteMediator: UserRemoteMediator,
    private val postDao: PostDao,
    postRemoteMediator: PostRemoteMediator,
) : Repository {

    override val dataUser: Flow<PagingData<FeedItem>> =
        Pager(
            config = PagingConfig(pageSize = 4, enablePlaceholders = false),
            pagingSourceFactory = { userDao.getPagingSource() },
            remoteMediator = userRemoteMediator
        ).flow.map {
            it.map(UserEntity::toDto)
        }

    override val dataPost: Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(pageSize = 4, enablePlaceholders = false),
        pagingSourceFactory = { postDao.getPagingSource() },
        remoteMediator = postRemoteMediator
    ).flow
        .map {
            it.map(PostEntity::toDto)
        }



    override suspend fun registration(
        login: String,
        name: String,
        pass: String,
        attachmentModel: AttachmentModel?
    ): ApiErrorAuth {
        try {
            val response = apiService.usersRegistration(login, pass, name)
            return when (response.code()) {
                403 -> ApiErrorAuth.UserRegistered
                415 -> ApiErrorAuth.IncorrectPhotoFormat
                200 -> {
                    response.body()?.let {
                        appAuth.setAuth(id = it.id, token = it.token)
                    }
                    ApiErrorAuth.Success
                }
                else -> ApiErrorAuth.UnknownError
            }
        } catch (e: IOException) {
            throw NetworkError

        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun login(login: String, pass: String): ApiErrorAuth {
          try {
            val response = apiService.usersAuthentication(login, pass)
            return when (response.code()) {
                404 -> ApiErrorAuth.UserNotFound
                400 -> ApiErrorAuth.IncorrectPassword
                200 -> {
                    response.body()?.let {
                        appAuth.setAuth(id = it.id, token = it.token)
                    }
                    ApiErrorAuth.Success
                }
                else -> ApiErrorAuth.UnknownError
            }
        } catch (e: IOException) {
            throw NetworkError

        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override fun logout() {
        appAuth.removeAuth()
    }

    override suspend fun getUsers() {
        try {
            userDao.getAll()
            val response = apiService.usersGetAllUser()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            userDao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError

        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun getUser(id: Int): UserResponse {
        try {
            val response = apiService.usersGetUser(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError

        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likePost(post: Post) {
        try {
            val response = when (post.likedByMe) {
                true -> {
                    apiService.postsUnLikePost(post.id)
                }

                else -> {
                    apiService.postsLikePost(post.id)
                }
            }

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())

            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun savePost(post: Post) {
        try {
            val response = apiService.postsSavePost(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun savePostWithAttachment(post: Post, attachmentModel: AttachmentModel) {
        try {
            val mediaResponse = saveMedia(attachmentModel.file)
            if (!mediaResponse.isSuccessful) {
                throw ApiError(mediaResponse.code(), mediaResponse.message())
            }
            val media = mediaResponse.body() ?: throw ApiError(
                mediaResponse.code(),
                mediaResponse.message()
            )

            val response = apiService.postsSavePost(
                post.copy(
                    attachment = Attachment(
                        media.url,
                        attachmentModel.attachmentType
                    )
                )
            )
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    private suspend fun saveMedia(file: File): Response<Media> {
        val part = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody())
        return apiService.mediaSaveMedia(part)
    }

    override suspend fun deletePost(id: Int) {
        try {
            val response = apiService.postsDeletePost(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            postDao.removeById(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveEvent(event: Event) {
        TODO("Not yet implemented")
    }

    override suspend fun saveEventWithAttachment(event: Event, attachmentModel: AttachmentModel) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteEvent(id: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun likeEvent(event: Event) {
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