package ru.netology.nework.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ru.netology.nework.api.ApiService
import ru.netology.nework.dao.EventDao
import ru.netology.nework.dao.RemoteKeyDao
import ru.netology.nework.db.AppDb
import ru.netology.nework.entity.EventEntity
import ru.netology.nework.entity.RemoteKeyEntity
import ru.netology.nework.error.ApiError

@OptIn(ExperimentalPagingApi::class)
class EventRemoteMediator(
    private val apiService: ApiService,
    private val eventDao: EventDao,
    private val eventRemoteKeyDao: RemoteKeyDao,
    private val appDb: AppDb
) : RemoteMediator<Int, EventEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EventEntity>
    ): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    val id = eventRemoteKeyDao.max()
                    if (id != null) {
                        apiService.eventsGetAfterEvent(id, state.config.pageSize)
                    } else {
                        apiService.eventsGetLatestPageEvent(state.config.pageSize)
                    }
                }
                LoadType.PREPEND -> {
                    val id = eventRemoteKeyDao.max() ?: return MediatorResult.Success(false)
                    apiService.eventsGetAfterEvent(id, state.config.pageSize)
                }
                LoadType.APPEND -> {
                    val id = eventRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                    apiService.eventsGetBeforeEvent(id, state.config.pageSize)
                }
            }

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(
                response.code(),
                response.message(),
            )

            appDb.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {

                        if (eventDao.isEmpty()) {
                            eventRemoteKeyDao.insert(
                                listOf(
                                    RemoteKeyEntity(
                                        type = RemoteKeyEntity.KeyType.AFTER,
                                        id = body.first().id
                                    ),
                                    RemoteKeyEntity(
                                        type = RemoteKeyEntity.KeyType.BEFORE,
                                        id = body.last().id
                                    )
                                )
                            )
                        } else {
                            eventRemoteKeyDao.insert(
                                RemoteKeyEntity(
                                    type = RemoteKeyEntity.KeyType.AFTER,
                                    id = body.first().id
                                )
                            )
                        }
                    }

                    LoadType.PREPEND -> {
                        eventRemoteKeyDao.insert(
                            RemoteKeyEntity(
                                type = RemoteKeyEntity.KeyType.AFTER,
                                id = body.first().id
                            )
                        )
                    }

                    LoadType.APPEND -> {
                        eventRemoteKeyDao.insert(
                            RemoteKeyEntity(
                                type = RemoteKeyEntity.KeyType.BEFORE,
                                id = body.last().id
                            )
                        )
                    }
                }

                eventDao.insert(body.map(EventEntity::fromDto))
            }
            return MediatorResult.Success(body.isEmpty())
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}