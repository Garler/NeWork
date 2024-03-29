package ru.netology.nework.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.netology.nework.BuildConfig.API_KEY
import ru.netology.nework.BuildConfig.BASE_URL
import ru.netology.nework.auth.AppAuth
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ApiModule {

    @Singleton
    @Provides
    fun provideOkHttp(
        appAuth: AppAuth
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            appAuth.authStateFlow.value.token?.let { token->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", token)
                    .addHeader("Api-Key", API_KEY)
                    .build()
                return@addInterceptor chain.proceed(request)
            }
            val request = chain.request().newBuilder()
                .addHeader("Api-Key", API_KEY)
                .build()
            chain.proceed(request)
        }
        .build()

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Singleton
    @Provides
    fun provideApiService(
        retrofit: Retrofit,
    ): ApiService = retrofit.create()
}
