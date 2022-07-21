package com.geekbrains.tests.view.search

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.geekbrains.tests.R
import com.geekbrains.tests.model.SearchResponse
import com.geekbrains.tests.model.SearchResult
import com.geekbrains.tests.presenter.RepositoryContract
import com.geekbrains.tests.repository.GitHubApi
import com.geekbrains.tests.repository.GitHubRepository
import com.geekbrains.tests.utils.MainSchedulersProvider
import com.geekbrains.tests.view.details.DetailsActivity
import com.geekbrains.tests.viewmodels.*
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MainActivity : AppCompatActivity(), ViewSearchContract {

    private val adapter = SearchResultAdapter()
    private val factory = SearchScreenViewModelFactory(
        createRepository(),
        MainSchedulersProvider()
    )

    private lateinit var viewModel: SearchScreenViewModel
    private var totalCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUI()

        viewModel = ViewModelProvider(this@MainActivity, factory)[SearchScreenViewModel::class.java]
        subscribeToLiveData()
    }

    private fun subscribeToLiveData() {
        viewModel.subscribeLiveData().observe(this@MainActivity) {
            displaySearchResults(it)
        }
    }

    private fun displaySearchResults(response: AppState) {
        when (response) {

            is SuccessState<*> -> {
                displayLoading(false)

                val data = response as SuccessState<SearchResponse>
                val searchResults = data.value.searchResults!!
                val totalCount = data.value.totalCount!!

                displaySearchResults(searchResults, totalCount)
            }

            is LoadingState -> {
                displayLoading(true)
            }

            is ErrorState -> {
                displayLoading(false)
                displayError(response.error.message.toString())
            }
        }
    }

    private fun setUI() {
        toDetailsActivityButton.setOnClickListener {
            startActivity(DetailsActivity.getIntent(this, totalCount))
        }
        setEditTextQueryListener()
        setRecyclerView()

        searchButton.setOnClickListener {
            setSearchButtonQueryListener()
        }
    }

    private fun setRecyclerView() {
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
    }

    private fun setEditTextQueryListener() {
        searchEditText.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = searchEditText.text.toString()
                if (query.isNotBlank()) {
                    viewModel.searchGitHub(query)
                    return@OnEditorActionListener true
                } else {
                    displayError(getString(R.string.enter_search_word))
                    return@OnEditorActionListener false
                }
            }
            false
        })
    }

    private fun createRepository(): RepositoryContract {
        return GitHubRepository(createRetrofit().create(GitHubApi::class.java))
    }

    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    override fun displaySearchResults(
        searchResults: List<SearchResult>,
        totalCount: Int
    ) {
        with(totalCountTextView) {
            visibility = View.VISIBLE
            text =
                String.format(Locale.getDefault(), getString(R.string.results_count), totalCount)
        }

        this.totalCount = totalCount
        adapter.updateResults(searchResults)
    }

    override fun displayError() {

        with(totalCountTextView) {
            visibility = View.VISIBLE
            text = getString(R.string.undefined_error)

        }
    }

    override fun displayError(error: String) {

        with(totalCountTextView) {
            visibility = View.VISIBLE
            text = error

        }
    }

    override fun displayLoading(show: Boolean) {
        if (show) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }

    private fun setSearchButtonQueryListener() {
        val query = searchEditText.text.toString()
        if (query.isNotBlank()) {
            viewModel.searchGitHub(query)
        } else {
            displayError(getString(R.string.enter_search_word))
        }
    }

    companion object {
        const val BASE_URL = "https://api.github.com"
    }
}
