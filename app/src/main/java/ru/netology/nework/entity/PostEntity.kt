package ru.netology.nework.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nework.dto.Attachment
import ru.netology.nework.dto.Coords
import ru.netology.nework.dto.Post
import ru.netology.nework.dto.UserPreview
import java.time.OffsetDateTime

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val authorId: Int,
    val author: String,
    val authorJob: String? = null,
    val authorAvatar: String? = null,
    val content: String,
    val published: String,
    @Embedded
    val coords: Coords? = null,
    val link: String? = null,
    val mentionIds: List<Int>,
    val mentionedMe: Boolean,
    val likeOwnerIds: List<Int>,
    val likedByMe: Boolean,
    @Embedded
    val attachment: Attachment? = null,
    val users: Map<String, UserPreview>
) {
    fun toDto() = Post(
        id,
        authorId,
        author,
        authorJob,
        authorAvatar,
        content,
        OffsetDateTime.parse(published),
        coords,
        link,
        mentionIds,
        mentionedMe,
        likeOwnerIds,
        likedByMe,
        attachment,
        users
    )

    companion object {
        fun fromDto(post: Post) = PostEntity(
            post.id,
            post.authorId,
            post.author,
            post.authorJob,
            post.authorAvatar,
            post.content,
            post.published.toString(),
            post.coords,
            post.link,
            post.mentionIds,
            post.mentionedMe,
            post.likeOwnerIds,
            post.likedByMe,
            post.attachment,
            post.users
        )
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)