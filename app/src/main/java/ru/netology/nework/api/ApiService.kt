package ru.netology.nework.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import ru.netology.nework.dto.Token
import ru.netology.nework.dto.UserResponse

interface ApiService {

    //Users

    @FormUrlEncoded
    @POST("api/users/registration")
    suspend fun usersRegistration(
        @Field("login") login: String,
        @Field("pass") pass: String,
        @Field("name") name: String
    ): Response<Token>

    @Multipart
    @POST("api/users/registration")
    suspend fun usersRegistrationWithPhoto(
        @Query("login") login: String,
        @Query("pass") pass: String,
        @Query("name") name: String,
        @Part file: MultipartBody.Part
    ): Response<Token>

    @FormUrlEncoded
    @POST("api/users/authentication")
    suspend fun usersAuthentication(
        @Field("login") login: String,
        @Field("pass") pass: String
    ): Response<Token>

    @GET("api/users")
    suspend fun usersGetAllUser(): Response<List<UserResponse>>

    @GET("api/users/{id}")
    suspend fun usersGetUser(
        @Path("id") id: Int,
    ): Response<UserResponse>

}