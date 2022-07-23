package com.geekbrains.tests.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.geekbrains.tests.model.SchedulersProvider
import com.geekbrains.tests.presenter.RepositoryContract
import kotlinx.coroutines.*

class SearchScreenViewModel internal constructor(
    private val repository: RepositoryContract,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val viewModelCoroutineScope = CoroutineScope(
        dispatcher
                + SupervisorJob()
                + CoroutineExceptionHandler { _, throwable ->
            handleError(throwable)
        })

    private val _liveDataToObserve: MutableLiveData<AppState> = MutableLiveData()

    fun subscribeLiveData(): LiveData<AppState> = _liveDataToObserve

    fun searchGitHub(query: String) {
        _liveDataToObserve.postValue(LoadingState(null))

        viewModelCoroutineScope.launch {
            val response = repository.searchGithub(query)
            val result = response.searchResults
            val totalCount = response.totalCount

            if (result != null && totalCount != null) {
                _liveDataToObserve.postValue(SuccessState(response))
            } else {
                _liveDataToObserve.postValue(
                    ErrorState(Throwable("Search results or total count are null"))
                )
            }
        }
    }


    private fun handleError(error: Throwable) {
        _liveDataToObserve.postValue(
            ErrorState(
                Throwable(
                    error.message ?: "Response is null or unsuccessful"
                )
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        viewModelCoroutineScope.cancel()
    }
}

sealed class AppState
data class LoadingState(val percent: Int?) : AppState()
data class SuccessState<T>(val value: T) : AppState()
data class ErrorState(val error: Throwable) : AppState()
