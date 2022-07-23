package com.geekbrains.tests.repository

import com.geekbrains.tests.model.SearchResponse
import com.geekbrains.tests.presenter.RepositoryContract

internal class GitHubRepository(private val gitHubApi: GitHubApi) : RepositoryContract {

    override suspend fun searchGithub(query: String): SearchResponse {
        return gitHubApi.searchGithubAsync(query)
    }
}
