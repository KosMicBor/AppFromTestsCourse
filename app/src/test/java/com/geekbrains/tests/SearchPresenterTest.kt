package com.geekbrains.tests

import android.os.Build
import android.os.Looper.getMainLooper
import androidx.core.view.isEmpty
import androidx.core.view.isNotEmpty
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.geekbrains.tests.model.SearchResponse
import com.geekbrains.tests.model.SearchResult
import com.geekbrains.tests.presenter.search.SearchPresenter
import com.geekbrains.tests.repository.GitHubRepository
import com.geekbrains.tests.view.search.MainActivity
import com.geekbrains.tests.view.search.ViewSearchContract
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import retrofit2.Response

//Тестируем наш Презентер
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class SearchPresenterTest {

    private lateinit var presenter: SearchPresenter

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Mock
    private lateinit var repository: GitHubRepository

    @Mock
    private lateinit var viewContract: ViewSearchContract

    @Before
    fun setUp() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        //Обязательно для аннотаций "@Mock"
        //Раньше было @RunWith(MockitoJUnitRunner.class) в аннотации к самому классу (SearchPresenterTest)
        MockitoAnnotations.openMocks(this)
        //Создаем Презентер, используя моки Репозитория и Вью, проинициализированные строкой выше
        presenter = SearchPresenter(repository)
        presenter.onAttach(viewContract)
    }

    @After
    fun onClose() {
        presenter.onDetach()
    }

    @Test
    fun onAttach_Test() {
        scenario.onActivity {
            presenter.onAttach(it)

            val response = mock(Response::class.java) as Response<SearchResponse?>

            val searchResponse = mock(SearchResponse::class.java)

            val searchResults = listOf(mock(SearchResult::class.java))

            `when`(response.isSuccessful).thenReturn(true)
            `when`(response.body()).thenReturn(searchResponse)
            `when`(searchResponse.searchResults).thenReturn(searchResults)
            `when`(searchResponse.totalCount).thenReturn(777)

            presenter.handleGitHubResponse(response)

            shadowOf(getMainLooper()).idle()
            assertTrue(it.findViewById<RecyclerView>(R.id.recyclerView).isNotEmpty())
        }
    }

    @Test
    fun onDetach_Test() {

        scenario.onActivity {
            presenter.onDetach()

            val response = mock(Response::class.java) as Response<SearchResponse?>

            val searchResponse = mock(SearchResponse::class.java)

            val searchResults = listOf(mock(SearchResult::class.java))

            `when`(response.isSuccessful).thenReturn(true)
            `when`(response.body()).thenReturn(searchResponse)
            `when`(searchResponse.searchResults).thenReturn(searchResults)
            `when`(searchResponse.totalCount).thenReturn(777)

            presenter.handleGitHubResponse(response)

            shadowOf(getMainLooper()).idle()
            assertTrue(it.findViewById<RecyclerView>(R.id.recyclerView).isEmpty())
        }
    }

    @Test //Проверим вызов метода searchGitHub() у нашего Репозитория
    fun searchGitHub_Test() {
        val searchQuery = "some query"
        //Запускаем код, функционал которого хотим протестировать
        presenter.searchGitHub("some query")
        //Убеждаемся, что все работает как надо
        verify(repository, times(ONE_VAL)).searchGithub(searchQuery, presenter)
    }

    @Test //Проверяем работу метода handleGitHubError()
    fun handleGitHubError_Test() {
        //Вызываем у Презентера метод handleGitHubError()
        presenter.handleGitHubError()
        //Проверяем, что у viewContract вызывается метод displayError()
        verify(viewContract, times(ONE_VAL)).displayError()
    }

    //Проверяем работу метода handleGitHubResponse

    @Test //Для начала проверим, как приходит ответ сервера
    fun handleGitHubResponse_ResponseUnsuccessful() {
        //Создаем мок ответа сервера с типом Response<SearchResponse?>?
        val response = mock(Response::class.java) as Response<SearchResponse?>
        //Описываем правило, что при вызове метода isSuccessful должен возвращаться false
        `when`(response.isSuccessful).thenReturn(false)
        //Убеждаемся, что ответ действительно false
        assertFalse(response.isSuccessful)
    }

    @Test //Теперь проверим, как у нас обрабатываются ошибки
    fun handleGitHubResponse_Failure() {
        //Создаем мок ответа сервера с типом Response<SearchResponse?>?
        val response = mock(Response::class.java) as Response<SearchResponse?>
        //Описываем правило, что при вызове метода isSuccessful должен возвращаться false
        //В таком случае должен вызываться метод viewContract.displayError(...)
        `when`(response.isSuccessful).thenReturn(false)

        //Вызывваем у Презентера метод handleGitHubResponse()
        presenter.handleGitHubResponse(response)

        //Убеждаемся, что вызывается верный метод: viewContract.displayError("Response is null or unsuccessful"), и что он вызывается единожды
        verify(viewContract, times(ONE_VAL))
            .displayError("Response is null or unsuccessful")
    }

    @Test //Проверим порядок вызова методов viewContract
    fun handleGitHubResponse_ResponseFailure_ViewContractMethodOrder() {
        val response = mock(Response::class.java) as Response<SearchResponse?>
        `when`(response.isSuccessful).thenReturn(false)
        presenter.handleGitHubResponse(response)

        //Определяем порядок вызова методов какого класса мы хотим проверить
        val inOrder = inOrder(viewContract)
        //Прописываем порядок вызова методов
        inOrder.verify(viewContract).displayLoading(false)
        inOrder.verify(viewContract).displayError("Response is null or unsuccessful")
    }

    @Test //Проверим пустой ответ сервера
    fun handleGitHubResponse_ResponseIsEmpty() {
        val response = mock(Response::class.java) as Response<SearchResponse?>
        `when`(response.body()).thenReturn(null)
        //Вызываем handleGitHubResponse()
        presenter.handleGitHubResponse(response)
        //Убеждаемся, что body действительно null
        assertNull(response.body())
    }

    @Test //Теперь проверим непустой ответ сервера
    fun handleGitHubResponse_ResponseIsNotEmpty() {
        val response = mock(Response::class.java) as Response<SearchResponse?>
        `when`(response.body()).thenReturn(mock(SearchResponse::class.java))
        //Вызываем handleGitHubResponse()
        presenter.handleGitHubResponse(response)
        //Убеждаемся, что body действительно null
        assertNotNull(response.body())
    }

    @Test //Проверим как обрабатывается случай, если ответ от сервера пришел пустой
    fun handleGitHubResponse_EmptyResponse() {
        val response = mock(Response::class.java) as Response<SearchResponse?>
        //Устанавливаем правило, что ответ успешный
        `when`(response.isSuccessful).thenReturn(true)
        //При этом body ответа == null. В таком случае должен вызываться метод viewContract.displayError(...)
        `when`(response.body()).thenReturn(null)

        //Вызываем handleGitHubResponse()
        presenter.handleGitHubResponse(response)

        //Убеждаемся, что вызывается верный метод: viewContract.displayError("Search results or total count are null"), и что он вызывается единожды
        verify(viewContract, times(ONE_VAL))
            .displayError("Search results or total count are null")
    }

    @Test //Пришло время проверить успешный ответ, так как все остальные случаи мы уже покрыли тестами
    fun handleGitHubResponse_Success() {
        //Мокаем ответ
        val response = mock(Response::class.java) as Response<SearchResponse?>
        //Мокаем тело ответа
        val searchResponse = mock(SearchResponse::class.java)
        //Мокаем список
        val searchResults = listOf(mock(SearchResult::class.java))

        //Прописываем правила
        //Когда ответ приходит, возвращаем response.isSuccessful == true и остальные данные в процессе выполнения метода handleGitHubResponse
        `when`(response.isSuccessful).thenReturn(true)
        `when`(response.body()).thenReturn(searchResponse)
        `when`(searchResponse.searchResults).thenReturn(searchResults)
        `when`(searchResponse.totalCount).thenReturn(101)

        //Запускаем выполнение метода
        presenter.handleGitHubResponse(response)

        //Убеждаемся, что ответ от сервера обрабатывается корректно
        verify(viewContract, times(ONE_VAL)).displaySearchResults(searchResults, 101)
    }
}
