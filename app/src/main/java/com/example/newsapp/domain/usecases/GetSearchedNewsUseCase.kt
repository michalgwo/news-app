package com.example.newsapp.domain.usecases

import com.example.newsapp.data.model.APIResponse
import com.example.newsapp.domain.repositories.NewsRepository
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.Response
import javax.inject.Inject

@ViewModelScoped
class GetSearchedNewsUseCase @Inject constructor(private val repository: NewsRepository) {
    suspend fun execute(query: String, country: String, page: Int): Response<APIResponse> =
        repository.getSearchedNews(query, country, page)
}