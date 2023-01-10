package com.example.newsapp.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.model.APIResponse
import com.example.newsapp.domain.usecases.GetNewsHeadlinesUseCase
import com.example.newsapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getNewsHeadlinesUseCase: GetNewsHeadlinesUseCase
    ): ViewModel() {

    var newsHeadlines: MutableLiveData<Resource<APIResponse>> = MutableLiveData()

    // todo check network connection
    fun getNewsHeadlines(country: String, page: Int) = viewModelScope.launch(Dispatchers.IO) {
        newsHeadlines.postValue(Resource.Loading())
        try {
            val apiResult = getNewsHeadlinesUseCase.execute(country, page)
            newsHeadlines.postValue(apiResult)
        } catch (e: Exception) {
            newsHeadlines.postValue(Resource.Error(e.message.toString()))
        }
    }
}