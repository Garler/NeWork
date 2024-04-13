package ru.netology.nework.model

import ru.netology.nework.dto.UserResponse

data class ListUsersModel(
    val likers: List<UserResponse> = emptyList(),
    val participants: List<UserResponse> = emptyList(),
    val mentioned: List<UserResponse> = emptyList(),
)

enum class ListUsersType {
    LIKERS,
    PARTICIPANT,
    MENTIONED
}
