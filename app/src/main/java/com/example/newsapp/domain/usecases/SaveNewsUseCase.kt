package com.example.newsapp.domain.usecases

import com.example.newsapp.data.model.Article
import com.example.newsapp.domain.repositories.NewsRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class SaveNewsUseCase @Inject constructor(private val repository: NewsRepository) {
    suspend fun execute(article: Article) = repository.saveNews(article)
}