package com.example.newsapp.data.repositories

import com.example.newsapp.data.api.APIService
import com.example.newsapp.data.db.SavedNewsDao
import com.example.newsapp.data.model.APIResponse
import com.example.newsapp.data.model.Article
import com.example.newsapp.domain.repositories.NewsRepository
import com.example.newsapp.util.Resource
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepositoryImpl @Inject constructor(
    private val apiService: APIService,
    private val savedNewsDao: SavedNewsDao
    ): NewsRepository {
    override suspend fun getNewsHeadlines(country: String, page: Int): Resource<APIResponse> {
        val response = apiService.getNewsHeadlines(country, page)

        if (response.isSuccessful) {
            response.body()?.let { result ->
                return Resource.Success(result)
            }
        } else {
            response.errorBody()?.let { result ->
                try {
                    val jsonErrorObject = JSONObject(result.string())
                    return Resource.Error(jsonErrorObject.getString("message"))
                } catch (e: Exception) {
                    return Resource.Error(e.message.toString())
                }
            }
        }
        return Resource.Error(response.message())
    }

    override fun getSavedNews(): Flow<List<Article>> =
        savedNewsDao.getSavedNews()

    override suspend fun getSearchedNews(query: String, country: String, page: Int): Resource<APIResponse> {
        val response = apiService.getSearchedNews(query, country, page)

        if (response.isSuccessful) {
            response.body()?.let { result ->
                return Resource.Success(result)
            }
        } else {
            response.errorBody()?.let {
                try {
                    val jsonErrorObject = JSONObject(it.string())
                    return Resource.Error(jsonErrorObject.getString("message"))
                } catch (e: Exception) {
                    return Resource.Error(e.message.toString())
                }
            }
        }
        return Resource.Error(response.message())
    }

    override suspend fun saveNews(article: Article): Long =
        savedNewsDao.saveNews(article)

    override suspend fun deleteSavedNews(article: Article): Int =
        savedNewsDao.deleteSavedNews(article)
}