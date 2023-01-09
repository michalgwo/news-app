package com.example.newsapp.domain.usecases

import com.example.newsapp.domain.repositories.NewsRepository

class GetSavedNewsUseCase(private val repository: NewsRepository) {
    fun execute() = repository.getSavedNews()
}