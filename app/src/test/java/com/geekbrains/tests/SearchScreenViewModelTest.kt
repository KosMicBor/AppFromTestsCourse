package com.geekbrains.tests

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.geekbrains.tests.model.SearchResponse
import com.geekbrains.tests.repository.GitHubRepository
import com.geekbrains.tests.viewmodels.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
@ExperimentalCoroutinesApi
class SearchScreenViewModelTest {

    companion object {
        private const val SEARCH_QUERY = "query"
        private const val ERROR_TEXT_NULL = "Search results or total count are null"
        private const val EXCEPTION_TEXT = "Response is null or unsuccessful"

    }

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var testCoroutineRule = CoroutinesTestRule()

    private lateinit var viewModel: SearchScreenViewModel

    @Mock
    private lateinit var repository: GitHubRepository


    private val coroutineDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {

        MockitoAnnotations.initMocks(this)

        viewModel = SearchScreenViewModel(
            repository,
            coroutineDispatcher
        )
    }

    @Test
    fun searchScreenViewModel_SearchGitHub_Test() {

        testCoroutineRule.runBlockingTest {
            viewModel.searchGitHub(SEARCH_QUERY)
            verify(repository, times(1)).searchGithub(SEARCH_QUERY)
        }
    }

    @Test
    fun searchScreenViewModel_SearchGitHub_ResponseNotNull_Test() {

        testCoroutineRule.runBlockingTest {
            val observer = Observer<AppState> {}
            val liveData = viewModel.subscribeLiveData()

            try {
                liveData.observeForever(observer)
                viewModel.searchGitHub(SEARCH_QUERY)
                assertNotNull(liveData.value)
            } finally {
                liveData.removeObserver(observer)
            }
        }

    }

    @Test
    fun searchScreenViewModel_Exception_Test() {

        testCoroutineRule.runBlockingTest {
            val observer = Observer<AppState> {}
            val liveData = viewModel.subscribeLiveData()

            try {
                liveData.observeForever(observer)
                viewModel.searchGitHub(SEARCH_QUERY)
                val value: ErrorState = liveData.value as ErrorState
                assertEquals(value.error.message, EXCEPTION_TEXT)
            } finally {
                liveData.removeObserver(observer)
            }
        }
    }

    @Test
    fun searchScreenViewModel_LiveData_ReturnValueIsNull_Test() {

        testCoroutineRule.runBlockingTest {
            val observer = Observer<AppState> {}
            val liveData = viewModel.subscribeLiveData()

            `when`(repository.searchGithub(SEARCH_QUERY)).thenReturn(
                SearchResponse(null, listOf())
            )

            try {
                liveData.observeForever(observer)
                viewModel.searchGitHub(SEARCH_QUERY)
                val value: ErrorState = liveData.value as ErrorState
                assertEquals(value.error.message, ERROR_TEXT_NULL)
            } finally {
                liveData.removeObserver(observer)
            }
        }
    }

    @Test
    fun searchScreenViewModel_LiveData_ReturnValueIsLoading_Test() {

        testCoroutineRule.runBlockingTest {
            val observer = Observer<AppState> {}
            val liveData = viewModel.subscribeLiveData()



            `when`(repository.searchGithub(SEARCH_QUERY)).thenReturn(
                SearchResponse(10, listOf())
            )

            coroutineDispatcher.pauseDispatcher()

            try {
                liveData.observeForever(observer)
                viewModel.searchGitHub(SEARCH_QUERY)
                val loadingResponse = liveData.value as LoadingState
                assertNull(loadingResponse.percent)
            } finally {
                liveData.removeObserver(observer)
            }
        }
    }

    @Test
    fun searchScreenViewModel_LiveData_ReturnValueIsSuccess_Test() {

        testCoroutineRule.runBlockingTest {
            val observer = Observer<AppState> {}
            val liveData = viewModel.subscribeLiveData()

            `when`(repository.searchGithub(SEARCH_QUERY)).thenReturn(
                SearchResponse(10, listOf())
            )

            try {
                liveData.observeForever(observer)
                viewModel.searchGitHub(SEARCH_QUERY)
                val response: SuccessState<SearchResponse> =
                    liveData.value as SuccessState<SearchResponse>
                assertEquals(10, response.value.totalCount)
            } finally {
                liveData.removeObserver(observer)
            }
        }
    }
}