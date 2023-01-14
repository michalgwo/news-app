package com.example.newsapp.data.api

import com.example.newsapp.data.model.APIResponse
import com.example.newsapp.data.model.Article
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class FakeAPIService(private var newsFromApiList: ArrayList<Article>?): APIService {
    override suspend fun getNewsHeadlines(country: String, page: Int, apiKey: String): Response<APIResponse> {
        newsFromApiList?.let { list ->
            return Response.success(APIResponse(list, "ok", list.size))
        }

        return Response.error(
            404,
            "{\"message\":[\"List not found\"]}"
                .toResponseBody("application/json".toMediaTypeOrNull())
        )
    }

    override suspend fun getSearchedNews(query: String, country: String, page: Int, apiKey: String): Response<APIResponse> {
        newsFromApiList?.let { list ->
            return Response.success(APIResponse(list, "ok", list.size))
        }

        return Response.error(
            404,
            "{\"message\":[\"List not found\"]}"
                .toResponseBody("application/json".toMediaTypeOrNull())
        )
    }

    fun setList(list: ArrayList<Article>?) {
        newsFromApiList = list
    }
}