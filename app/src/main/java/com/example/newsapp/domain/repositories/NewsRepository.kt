package com.example.newsapp.domain.repositories

import com.example.newsapp.data.model.APIResponse
import com.example.newsapp.data.model.Article
import com.example.newsapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    suspend fun getNewsHeadlines(): Resource<APIResponse>
    fun getSavedNews(): Flow<List<Article>>
    suspend fun getSearchedNews(query: String): Resource<APIResponse>
    suspend fun saveNews(article: Article)
    suspend fun deleteSavedNews(article: Article)
}