package com.example.newsapp.domain.usecases

import com.example.newsapp.domain.repositories.NewsRepository

class GetSearchedNewsUseCase(private val repository: NewsRepository) {
    suspend fun execute(query: String) = repository.getSearchedNews(query)
}