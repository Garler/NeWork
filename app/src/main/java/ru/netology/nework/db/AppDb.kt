package ru.netology.nework.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.netology.nework.dao.UserDao
import ru.netology.nework.entity.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false
)

abstract class AppDb : RoomDatabase() {
    abstract fun userDao(): UserDao
}