package ru.netology.nework.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.netology.nework.api.ApiService
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.dao.UserDao
import ru.netology.nework.dto.FeedItem
import ru.netology.nework.dto.UserResponse
import ru.netology.nework.entity.UserEntity
import ru.netology.nework.error.ApiErrorAuth
import ru.netology.nework.error.NetworkError
import ru.netology.nework.error.UnknownError
import ru.netology.nework.model.AttachmentModel
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OptIn(ExperimentalPagingApi::class)
class RepositoryImpl @Inject constructor(
    private val appAuth: AppAuth,
    private val apiService: ApiService,
    private val userDao: UserDao,
    userRemoteMediator: UserRemoteMediator
) : Repository {

    override val dataUser: Flow<PagingData<FeedItem>> =
        Pager(
            config = PagingConfig(pageSize = 4, enablePlaceholders = false),
            pagingSourceFactory = { userDao.getPagingSource() },
            remoteMediator = userRemoteMediator
        ).flow.map {
            it.map(UserEntity::toDto)
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
        TODO("Not yet implemented")
    }

    override suspend fun getUser(id: Int): UserResponse {
        TODO("Not yet implemented")
    }

}