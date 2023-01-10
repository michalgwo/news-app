package com.example.newsapp.domain.usecases

import com.example.newsapp.domain.repositories.NewsRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GetNewsHeadlinesUseCase @Inject constructor(private val repository: NewsRepository) {
    suspend fun execute(country: String, page: Int) = repository.getNewsHeadlines(country, page)
}