package com.example.newsapp.data.repositories

import com.example.newsapp.data.api.APIService
import com.example.newsapp.data.db.SavedNewsDao
import com.example.newsapp.data.model.APIResponse
import com.example.newsapp.data.model.Article
import com.example.newsapp.domain.repositories.NewsRepository
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepositoryImpl @Inject constructor(
    private val apiService: APIService,
    private val savedNewsDao: SavedNewsDao
    ): NewsRepository {
    override suspend fun getNewsHeadlines(country: String, page: Int): Response<APIResponse> =
        apiService.getNewsHeadlines(country, page)

    override fun getSavedNews(): Flow<List<Article>> =
        savedNewsDao.getSavedNews()

    override suspend fun getSearchedNews(query: String, country: String, page: Int): Response<APIResponse> =
        apiService.getSearchedNews(query, country, page)

    override suspend fun saveNews(article: Article): Long =
        savedNewsDao.saveNews(article)

    override suspend fun deleteSavedNews(article: Article): Int =
        savedNewsDao.deleteSavedNews(article)
}