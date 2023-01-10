package com.example.newsapp.domain.usecases

import com.example.newsapp.domain.repositories.NewsRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GetSearchedNewsUseCase @Inject constructor(private val repository: NewsRepository) {
    suspend fun execute(query: String) = repository.getSearchedNews(query)
}