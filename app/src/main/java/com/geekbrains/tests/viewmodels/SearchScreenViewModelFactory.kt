package com.geekbrains.tests.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.geekbrains.tests.model.SchedulersProvider
import com.geekbrains.tests.presenter.RepositoryContract
import kotlinx.coroutines.CoroutineDispatcher

class SearchScreenViewModelFactory internal constructor(
    private val repository: RepositoryContract,
    private val dispatcher: CoroutineDispatcher
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        SearchScreenViewModel(repository, dispatcher) as T
}