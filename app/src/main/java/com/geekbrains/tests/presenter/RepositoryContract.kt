package com.geekbrains.tests.presenter

import com.geekbrains.tests.model.SearchResponse
import io.reactivex.Observable


internal interface RepositoryContract {
    fun searchGithub(
        query: String
    ): Observable<SearchResponse>
}
