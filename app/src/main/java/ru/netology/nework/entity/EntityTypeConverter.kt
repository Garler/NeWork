package ru.netology.nework.entity

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nework.dto.UserPreview

class MentionIdsConverter {
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Int>>() {}.type

    @TypeConverter
    fun fromMentionIds(mentionIds: List<Int>?): String? {
        return if (mentionIds != null) gson.toJson(mentionIds) else null
    }

    @TypeConverter
    fun toMentionIds(mentionIds: String?): List<Int>? {
        return if (mentionIds != null) gson.fromJson(mentionIds, typeToken) else null
    }
}

class UsersConverter {
    private val gson = Gson()
    private val typeToken = object : TypeToken<Map<String, UserPreview>>() {}.type

    @TypeConverter
    fun fromAttachment(users: Map<String, UserPreview>?): String? {
        return if (users != null) gson.toJson(users) else null
    }

    @TypeConverter
    fun toAttachment(users: String?): Map<String, UserPreview>? {
        return if (users != null) gson.fromJson(users, typeToken) else null
    }
}