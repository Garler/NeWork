package ru.netology.nework.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.netology.nework.dto.Attachment
import ru.netology.nework.dto.Coords
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.EventType
import ru.netology.nework.dto.UserPreview
import java.time.OffsetDateTime

@Entity
@TypeConverters(
    MentionIdsConverter::class,
    UsersConverter::class
)
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val authorId: Int,
    val author: String,
    val authorJob: String? = null,
    val authorAvatar: String? = null,
    val content: String,
    val datetime: String,
    val published: String,
    @Embedded
    val coords: Coords? = null,
    @Embedded
    val type: EventType = EventType.OFFLINE,
    val likeOwnerIds: List<Int>,
    val likedByMe: Boolean,
    val speakerIds: List<Int>,
    val participantsIds: List<Int>,
    val participatedByMe: Boolean,
    @Embedded
    val attachment: Attachment? = null,
    val link: String? = null,
    val users: Map<String, UserPreview>,
    val ownedByMe: Boolean = false
) {

    fun toDto() = Event(
        id,
        authorId,
        author,
        authorJob,
        authorAvatar,
        content,
        OffsetDateTime.parse(datetime),
        OffsetDateTime.parse(published),
        coords,
        type,
        likeOwnerIds,
        likedByMe,
        speakerIds,
        participantsIds,
        participatedByMe,
        attachment,
        link,
        users,
        ownedByMe,
    )

    companion object {
        fun fromDto(event: Event) = EventEntity(
            event.id,
            event.authorId,
            event.author,
            event.authorJob,
            event.authorAvatar,
            event.content,
            event.datetime.toString(),
            event.published.toString(),
            event.coords,
            event.type,
            event.likeOwnerIds,
            event.likedByMe,
            event.speakerIds,
            event.participantsIds,
            event.participatedByMe,
            event.attachment,
            event.link,
            event.users,
            event.ownedByMe,
        )
    }
}

fun List<EventEntity>.toDto(): List<Event> = map(EventEntity::toDto)
fun List<Event>.toEntity(): List<EventEntity> = map(EventEntity::fromDto)
