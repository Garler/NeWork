package ru.netology.nework.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nework.dto.Job
import java.time.OffsetDateTime

@Entity
data class JobEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val position: String,
    val start: String,
    val finish: String,
    val link: String? = null
) {
    fun toDto() = Job(
        id,
        name,
        position,
        OffsetDateTime.parse(start),
        OffsetDateTime.parse(finish),
        link
    )

    companion object {
        fun fromDto(job: Job) = JobEntity(
            job.id,
            job.name,
            job.position,
            job.start.toString(),
            job.finish.toString(),
            job.link
        )
    }
}

fun List<JobEntity>.toDto(): List<Job> = map(JobEntity::toDto)
fun List<Job>.toEntity(): List<JobEntity> = map(JobEntity.Companion::fromDto)