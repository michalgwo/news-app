package com.example.newsapp.domain.usecases

import com.example.newsapp.data.model.Article
import com.example.newsapp.domain.repositories.NewsRepository

class SaveNewsUseCase(private val repository: NewsRepository) {
    suspend fun execute(article: Article) = repository.saveNews(article)
}