package ru.netology.nework.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class RemoteKeyEntity(
    @PrimaryKey
    val type: KeyType,
    val id: Int,
) {
    enum class KeyType {
        AFTER, BEFORE
    }
}