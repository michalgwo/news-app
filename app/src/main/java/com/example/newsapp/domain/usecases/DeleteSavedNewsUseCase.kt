package com.example.newsapp.domain.usecases

import com.example.newsapp.data.model.Article
import com.example.newsapp.domain.repositories.NewsRepository

class DeleteSavedNewsUseCase(private val repository: NewsRepository) {
    suspend fun execute(article: Article) = repository.deleteSavedNews(article)
}