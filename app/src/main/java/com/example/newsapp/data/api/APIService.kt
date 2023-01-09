package com.example.newsapp.data.api

import com.example.newsapp.BuildConfig
import com.example.newsapp.data.model.APIResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {
    @GET("v2/top-headlines")
    suspend fun getNewsHeadlines(
        @Query("country") country: String,
        @Query("page") page: Int,
        @Query("apiKey") apiKey: String = BuildConfig.API_KEY
    ): Response<APIResponse>

    @GET("v2/top-headlines")
    suspend fun getSearchedNews(
        @Query("q") query: String,
        @Query("country") country: String,
        @Query("page") page: Int,
        @Query("apiKey") apiKey: String = BuildConfig.API_KEY
    ): Response<APIResponse>
}