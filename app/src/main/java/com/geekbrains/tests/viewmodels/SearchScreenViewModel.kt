package com.geekbrains.tests.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.geekbrains.tests.model.SchedulersProvider
import com.geekbrains.tests.model.SearchResponse
import com.geekbrains.tests.presenter.RepositoryContract
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver

class SearchScreenViewModel internal constructor(
    private val repository: RepositoryContract,
    private val schedulersProvider: SchedulersProvider
) : ViewModel() {

    val compositeDisposable = CompositeDisposable()
    private val _liveDataToObserve: MutableLiveData<AppState> = MutableLiveData()

    fun subscribeLiveData(): LiveData<AppState> = _liveDataToObserve

    fun searchGitHub(query: String) {
        compositeDisposable.add(
            repository.searchGithub(query)
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .doOnSubscribe { _liveDataToObserve.postValue(LoadingState(null)) }
                .subscribeWith(object : DisposableObserver<SearchResponse>() {
                    override fun onNext(searchResponse: SearchResponse) {
                        val searchResults = searchResponse.searchResults
                        val totalCount = searchResponse.totalCount
                        if (searchResults != null && totalCount != null) {
                            _liveDataToObserve.postValue(SuccessState(searchResponse))
                        } else {
                            _liveDataToObserve.postValue(
                                ErrorState(Throwable("Search results or total count are null"))
                            )
                        }
                    }

                    override fun onError(e: Throwable) {
                        _liveDataToObserve.postValue(
                            ErrorState(
                                Throwable(
                                    e.message ?: "Response is null or unsuccessful"
                                )
                            )
                        )
                    }

                    override fun onComplete() {
                        //NOTHING TO DO
                    }
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}

sealed class AppState
data class LoadingState(val percent: Int?) : AppState()
data class SuccessState<T>(val value: T) : AppState()
data class ErrorState(val error: Throwable) : AppState()
