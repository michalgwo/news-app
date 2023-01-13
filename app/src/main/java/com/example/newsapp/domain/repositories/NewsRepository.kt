package com.example.newsapp.domain.repositories

import com.example.newsapp.data.model.APIResponse
import com.example.newsapp.data.model.Article
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface NewsRepository {
    suspend fun getNewsHeadlines(country: String, page: Int): Response<APIResponse>
    fun getSavedNews(): Flow<List<Article>>
    suspend fun getSearchedNews(query: String, country: String, page: Int): Response<APIResponse>
    suspend fun saveNews(article: Article): Long
    suspend fun deleteSavedNews(article: Article): Int
}