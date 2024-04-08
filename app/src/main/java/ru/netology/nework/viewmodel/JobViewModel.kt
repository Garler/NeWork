package ru.netology.nework.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.dto.Job
import ru.netology.nework.model.FeedModelState
import ru.netology.nework.repository.Repository
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class JobViewModel @Inject constructor(
    private val repository: Repository,
    appAuth: AppAuth,
) : ViewModel() {
    private val userId = MutableLiveData<Int>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val dataJob: Flow<List<Job>> = appAuth.authStateFlow.flatMapLatest { (MyId) ->
        repository.dataJob.map {
            it.map { job ->
                job.copy(
                    ownedByMe = userId.value == MyId
                )
            }
        }
    }

    private val _dataState = MutableLiveData(FeedModelState())

    fun getJobs(userId: Int) = viewModelScope.launch {
        _dataState.postValue(FeedModelState(loading = true))
        try {
            repository.getJobs(userId)
            _dataState.postValue(FeedModelState())
        } catch (e: Exception) {
            e.printStackTrace()
            _dataState.postValue(FeedModelState(error = true))
        }
    }

    fun removeJob(id: Int) {
        viewModelScope.launch {
            try {
                repository.deleteJob(id)
                _dataState.value = FeedModelState()

            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)

            }
        }
    }

    fun setId(id: Int) {
        userId.value = id
    }

    fun saveJob(
        name: String,
        position: String,
        link: String?,
        startWork: OffsetDateTime,
        finishWork: OffsetDateTime,
    ) = viewModelScope.launch {
        repository.saveJob(
            Job(
                id = 0,
                name = name,
                position = position,
                link = link,
                start = startWork,
                finish = finishWork,
            )
        )
    }
}