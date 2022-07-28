package com.geekbrains.tests.repository

import com.geekbrains.tests.model.SearchResponse
import io.reactivex.Observable
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

/**
 * Документация https://developer.github.com/v3/search/
 */

internal interface GitHubApi {

    @Headers("Accept: application/vnd.github.mercy-preview+json")
    @GET("search/repositories")
    suspend fun searchGithubAsync(@Query("q") term: String?): SearchResponse
}
