package ru.netology.nework.repository

import android.content.Context
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nework.R
import ru.netology.nework.api.ApiService
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.auth.AuthState
import ru.netology.nework.dto.UserResponse
import ru.netology.nework.error.ApiError
import ru.netology.nework.error.NetworkError
import ru.netology.nework.error.UnknownError
import ru.netology.nework.model.AttachmentModel
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val appAuth: AppAuth,
    private val apiService: ApiService
) : Repository {

    override val dataAuth: StateFlow<AuthState>
        get() {
            TODO()
        }

    override suspend fun registration(
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
                        toastMsg(R.string.user_registered)
                    }

                    415 -> {
                        toastMsg(R.string.incorrect_photo_format)
                    }

                    400 -> {
                        toastMsg(R.string.user_already_register)
                    }

                    200 -> {
                        response.body()?.let {
                            appAuth.setAuth(id = it.id, token = it.token)
                        }
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
                throw UnknownError
            }
        }

        override suspend fun login(login: String, pass: String) {
            try {
                val response = apiService.usersAuthentication(login, pass)
                if (!response.isSuccessful) {
                    when (response.code()) {
                        404 -> toastMsg(R.string.user_not_found)

                        400 -> toastMsg(R.string.incorrect_password)

                        200 -> {
                            response.body()?.let {
                                appAuth.setAuth(id = it.id, token = it.token)
                            }
                        }

                        else -> toastMsg(R.string.unknown_error)
                    }
                    return
                }
                val body = response.body() ?: throw ApiError(response.code(), response.message())
                appAuth.setAuth(body.id, body.token)

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

        private fun toastMsg(msg: Int) {
            Toast.makeText(
                context,
                msg,
                Toast.LENGTH_LONG
            ).show()
        }

    }