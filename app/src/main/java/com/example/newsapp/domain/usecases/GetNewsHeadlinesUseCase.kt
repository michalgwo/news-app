package com.example.newsapp.domain.usecases

import com.example.newsapp.domain.repositories.NewsRepository

class GetNewsHeadlinesUseCase(private val repository: NewsRepository) {
    suspend fun execute() = repository.getNewsHeadlines()
}