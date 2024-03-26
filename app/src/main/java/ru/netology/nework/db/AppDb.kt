package ru.netology.nework.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.netology.nework.dao.EventDao
import ru.netology.nework.dao.JobDao
import ru.netology.nework.dao.PostDao
import ru.netology.nework.dao.RemoteKeyDao
import ru.netology.nework.dao.UserDao
import ru.netology.nework.entity.EventEntity
import ru.netology.nework.entity.JobEntity
import ru.netology.nework.entity.PostEntity
import ru.netology.nework.entity.RemoteKeyEntity
import ru.netology.nework.entity.UserEntity

@Database(
    entities = [EventEntity::class, JobEntity::class, PostEntity::class, UserEntity::class, RemoteKeyEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun eventDao(): EventDao
    abstract fun jobDao(): JobDao
    abstract fun userDao(): UserDao
    abstract fun remoteKeyDao(): RemoteKeyDao
}