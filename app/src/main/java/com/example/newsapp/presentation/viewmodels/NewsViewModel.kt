package com.example.newsapp.presentation.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.*
import com.example.newsapp.R
import com.example.newsapp.data.model.APIResponse
import com.example.newsapp.data.model.Article
import com.example.newsapp.domain.usecases.*
import com.example.newsapp.presentation.NewsApp
import com.example.newsapp.util.Constants.Companion.COUNTRY
import com.example.newsapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    app: Application,
    private val getNewsHeadlinesUseCase: GetNewsHeadlinesUseCase,
    private val getSearchedNewsUseCase: GetSearchedNewsUseCase,
    private val getSavedNewsUseCase: GetSavedNewsUseCase,
    private val saveNewsUseCase: SaveNewsUseCase,
    private val deleteSavedNewsUseCase: DeleteSavedNewsUseCase
    ): AndroidViewModel(app) {

    private val _newsHeadlines: MutableLiveData<Resource<APIResponse>> = MutableLiveData()
    val newsHeadlines: LiveData<Resource<APIResponse>>
        get() = _newsHeadlines

    private var _newsPage = 1
    val newsPage: Int
        get() = _newsPage

    private var _newsResponse: APIResponse? = null
    val newsResponse: APIResponse?
        get() = _newsResponse

    private val _searchedNewsHeadlines: MutableLiveData<Resource<APIResponse>> = MutableLiveData()
    val searchedNewsHeadlines: LiveData<Resource<APIResponse>>
        get() = _searchedNewsHeadlines

    private var _searchedNewsPage = 1
    val searchedNewsPage: Int
        get() = _searchedNewsPage

    private var _searchedNewsResponse: APIResponse? = null

    fun getNewsHeadlines(loadNextPage: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            _newsHeadlines.postValue(Resource.Loading())

            if (!hasInternetConnection()) {
                _newsHeadlines.postValue(
                    Resource.Error(getApplication<NewsApp>().getString(R.string.no_internet))
                )
                return@launch
            }

            try {
                if (loadNextPage) {
                    _newsPage++
                }

                val apiResult = getNewsHeadlinesUseCase.execute(COUNTRY, _newsPage)
                _newsHeadlines.postValue(handleNewsResponse(apiResult))
            } catch (e: Exception) {
                _newsHeadlines.postValue(Resource.Error(e.message.toString()))
            }
        }

    fun getNewsHeadlinesFromCache() {
        if (_newsResponse == null) {
            getNewsHeadlines(true)
            return
        }

        _newsHeadlines.value = Resource.Success(_newsResponse!!)
    }

    fun getSearchedNews(query: String, loadNextPage: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            _searchedNewsHeadlines.postValue(Resource.Loading())

            if (!hasInternetConnection()) {
                _newsHeadlines.postValue(
                    Resource.Error(getApplication<NewsApp>().getString(R.string.no_internet))
                )
                return@launch
            }

            try {
                if (loadNextPage) {
                    _searchedNewsPage++
                }
                val apiResult = getSearchedNewsUseCase.execute(query, COUNTRY, _searchedNewsPage)
                _searchedNewsHeadlines.postValue(handleSearchedNewsResponse(apiResult))
            } catch (e: Exception) {
                _searchedNewsHeadlines.postValue(Resource.Error(e.message.toString()))
            }
        }

    fun resetSearchedNews() {
        _searchedNewsPage = 1
        _searchedNewsResponse = null
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

    private fun handleNewsResponse(response: Response<APIResponse>): Resource<APIResponse> {
        if (response.isSuccessful) {
            response.body()?.let { result ->
                if (_newsResponse == null) {
                    _newsResponse = result
                } else {
                    val newArticles = result.articles
                    _newsResponse?.articles?.addAll(newArticles)
                }

                return Resource.Success(_newsResponse ?: result)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchedNewsResponse(response: Response<APIResponse>): Resource<APIResponse> {
        if (response.isSuccessful) {
            response.body()?.let { result ->
                if (_searchedNewsResponse == null) {
                    _searchedNewsResponse = result
                } else {
                    val newArticles = result.articles
                    _searchedNewsResponse?.articles?.addAll(newArticles)
                }
                return Resource.Success(_searchedNewsResponse ?: result)
            }
        }
        return Resource.Error(response.message())
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<NewsApp>()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }

        return false
    }
}