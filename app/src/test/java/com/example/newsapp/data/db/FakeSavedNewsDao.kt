package com.example.newsapp.data.db

import com.example.newsapp.data.model.Article
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeSavedNewsDao(private var savedNewsList: ArrayList<Article>?): SavedNewsDao {
    override fun getSavedNews(): Flow<List<Article>> {
        val tmpList = arrayListOf<Article>()
        savedNewsList?.let {
            tmpList.addAll(savedNewsList!!)
        }

        return flow {
            emit(tmpList)
        }
    }

    override suspend fun saveNews(article: Article): Long {
        savedNewsList?.let { list ->
            if (list.add(article))
                return 123L
        }
        return 0L
    }

    override suspend fun deleteSavedNews(article: Article): Int {
        savedNewsList?.let { list ->
            if (list.remove(article))
                return 1
        }
        return 0
    }

    fun setList(list: ArrayList<Article>?) {
        savedNewsList = list
    }
}