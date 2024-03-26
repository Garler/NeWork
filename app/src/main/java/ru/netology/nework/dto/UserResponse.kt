package ru.netology.nework.dto

data class UserResponse(
    val id: Int,
    val login: String,
    val name: String,
    val avatar: String? = null
)
