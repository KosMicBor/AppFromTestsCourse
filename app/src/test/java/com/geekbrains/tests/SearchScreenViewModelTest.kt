package com.geekbrains.tests

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.geekbrains.tests.model.SearchResponse
import com.geekbrains.tests.repository.GitHubRepository
import com.geekbrains.tests.utils.MainSchedulersProviderStub
import com.geekbrains.tests.viewmodels.*
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class SearchScreenViewModelTest {

    companion object {
        private const val SEARCH_QUERY = "query"
        private const val ERROR_TEXT = "error"
        private const val ERROR_TEXT_NULL = "Search results or total count are null"
    }

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: SearchScreenViewModel

    @Mock
    private lateinit var repository: GitHubRepository

    @Before
    fun setUp() {

        MockitoAnnotations.initMocks(this)

        viewModel = SearchScreenViewModel(
            repository,
            MainSchedulersProviderStub()
        )
    }

    @Test
    fun searchScreenViewModel_SearchGitHub_Test() {
        Mockito.`when`(repository.searchGithub(SEARCH_QUERY)).thenReturn(
            Observable.just(
                SearchResponse(
                    1,
                    listOf()
                )
            )
        )
        viewModel.searchGitHub(SEARCH_QUERY)
        verify(repository, times(1)).searchGithub(SEARCH_QUERY)
    }

    @Test
    fun searchScreenViewModel_SearchGitHub_ResponseNotNull_Test() {
        val observer = Observer<AppState> {}
        val liveData = viewModel.subscribeLiveData()

        Mockito.`when`(repository.searchGithub(SEARCH_QUERY)).thenReturn(
            Observable.just(
                SearchResponse(
                    1,
                    listOf()
                )
            )
        )

        try {
            liveData.observeForever(observer)
            viewModel.searchGitHub(SEARCH_QUERY)
            Assert.assertNotNull(liveData.value)
        } finally {
            liveData.removeObserver(observer)
        }
    }


    @Test
    fun searchScreenViewModel_LiveData_ReturnValueIsError_Test() {
        val observer = Observer<AppState> {}
        val liveData = viewModel.subscribeLiveData()
        val error = Throwable(ERROR_TEXT)

        Mockito.`when`(repository.searchGithub(SEARCH_QUERY)).thenReturn(
            Observable.error(error)
        )

        try {
            liveData.observeForever(observer)
            viewModel.searchGitHub(SEARCH_QUERY)
            val value: ErrorState = liveData.value as ErrorState
            Assert.assertEquals(value.error.message, error.message)
        } finally {
            liveData.removeObserver(observer)
        }
    }

    @Test
    fun searchScreenViewModel_LiveData_ReturnValueIsNull_Test() {
        val observer = Observer<AppState> {}
        val liveData = viewModel.subscribeLiveData()
        val error = Throwable(ERROR_TEXT_NULL)

        Mockito.`when`(repository.searchGithub(SEARCH_QUERY)).thenReturn(
            Observable.just(
                SearchResponse(
                    null,
                    null
                )
            )
        )

        try {
            liveData.observeForever(observer)
            viewModel.searchGitHub(SEARCH_QUERY)
            val value: ErrorState = liveData.value as ErrorState
            Assert.assertEquals(value.error.message, error.message)
        } finally {
            liveData.removeObserver(observer)
        }
    }

    @Test
    fun searchScreenViewModel_LiveData_ReturnValueIsLoading_Test() {
        val observer = Observer<AppState> {}
        val liveData = viewModel.subscribeLiveData()
        val delay = PublishSubject.create<Boolean>()

        Mockito.`when`(repository.searchGithub(SEARCH_QUERY)).thenReturn(
           Observable.just(
               SearchResponse(
                   1,
                   listOf()
               )
           ).delaySubscription(delay)
        )

        try {
            liveData.observeForever(observer)
            viewModel.searchGitHub(SEARCH_QUERY)
            val loadingResponse = liveData.value as LoadingState
            Assert.assertNull(loadingResponse.percent)
        } finally {
            liveData.removeObserver(observer)
        }
    }

    @Test
    fun searchScreenViewModel_LiveData_ReturnValueIsSuccess_Test() {
        val observer = Observer<AppState> {}
        val liveData = viewModel.subscribeLiveData()

        Mockito.`when`(repository.searchGithub(SEARCH_QUERY)).thenReturn(
            Observable.just(
                SearchResponse(
                    10,
                    listOf()
                )
            )
        )

        try {
            liveData.observeForever(observer)
            viewModel.searchGitHub(SEARCH_QUERY)
            val response: SuccessState<SearchResponse> =
                liveData.value as SuccessState<SearchResponse>
            Assert.assertEquals(10, response.value.totalCount)
        } finally {
            liveData.removeObserver(observer)
        }
    }
}