package com.geekbrains.tests.repository

import com.geekbrains.tests.model.SearchResponse
import com.geekbrains.tests.presenter.RepositoryContract
import io.reactivex.Observable

internal class GitHubRepository(private val gitHubApi: GitHubApi) : RepositoryContract {

    override fun searchGithub(query: String): Observable<SearchResponse> {
        return gitHubApi.searchGithub(query)
    }
}
