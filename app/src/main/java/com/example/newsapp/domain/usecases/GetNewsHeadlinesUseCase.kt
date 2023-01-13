package com.example.newsapp.domain.usecases

import com.example.newsapp.data.model.APIResponse
import com.example.newsapp.domain.repositories.NewsRepository
import com.example.newsapp.util.Resource
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GetNewsHeadlinesUseCase @Inject constructor(private val repository: NewsRepository) {
    suspend fun execute(country: String, page: Int): Resource<APIResponse> =
        repository.getNewsHeadlines(country, page)
}