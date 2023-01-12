package com.example.newsapp.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.model.APIResponse
import com.example.newsapp.data.model.Article
import com.example.newsapp.domain.usecases.*
import com.example.newsapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getNewsHeadlinesUseCase: GetNewsHeadlinesUseCase,
    private val getSearchedNewsUseCase: GetSearchedNewsUseCase,
    private val getSavedNewsUseCase: GetSavedNewsUseCase,
    private val saveNewsUseCase: SaveNewsUseCase,
    private val deleteSavedNewsUseCase: DeleteSavedNewsUseCase
    ): ViewModel() {

    var newsHeadlines: MutableLiveData<Resource<APIResponse>> = MutableLiveData()
    var searchedNewsHeadlines: MutableLiveData<Resource<APIResponse>> = MutableLiveData()

    // todo check network connection
    fun getNewsHeadlines(country: String, page: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            newsHeadlines.postValue(Resource.Loading())
            val apiResult = getNewsHeadlinesUseCase.execute(country, page)
            newsHeadlines.postValue(apiResult)
        }

    fun getSearchedNews(query: String, country: String, page: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            searchedNewsHeadlines.postValue(Resource.Loading())
            val apiResult = getSearchedNewsUseCase.execute(query, country, page)
            searchedNewsHeadlines.postValue(apiResult)
        }

    fun saveArticle(article: Article) = viewModelScope.launch(Dispatchers.IO) {
        saveNewsUseCase.execute(article)
    }

    fun deleteSavedArticle(article: Article) = viewModelScope.launch(Dispatchers.IO) {
        deleteSavedNewsUseCase.execute(article)
    }

    fun getSavedNews() = liveData {
        getSavedNewsUseCase.execute().collect {
            emit(it)
        }
    }
}