package com.example.newsapp.data.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.newsapp.data.api.FakeAPIService
import com.example.newsapp.data.db.FakeSavedNewsDao
import com.example.newsapp.data.model.Article
import com.example.newsapp.data.model.Source
import com.example.newsapp.domain.repositories.NewsRepository
import com.example.newsapp.util.Resource
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class NewsRepositoryImplTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val article1 = Article(1, null, "c1", "d1", "p1", Source("1", "n1"), "t1", "u1", "iu1")
    private val article2 = Article(2, "a2", null, "d2", "p2", Source("2", "n2"), "t2", "u2", "iu2")
    private val article3 = Article(3, "a3", "c3", null, "p3", Source("3", "n3"), "t3", "u3", "iu3")
    private val article4 = Article(4, "a4", "c4", "d4", null, Source("4", "n4"), "t4", "u4", "iu4")
    private var newsFromApiList = arrayListOf(article1, article2)
    private var savedNewsList = arrayListOf(article3)

    private lateinit var fakeApiService: FakeAPIService
    private lateinit var fakeSavedNewsDao: FakeSavedNewsDao
    private lateinit var newsRepository: NewsRepository

    @Before
    fun setUp() {
        fakeApiService = FakeAPIService(newsFromApiList)
        fakeSavedNewsDao = FakeSavedNewsDao(savedNewsList)
        newsRepository = NewsRepositoryImpl(fakeApiService, fakeSavedNewsDao)
   }

    @Test
    fun getNewsHeadlines_checkFilledList_returnsSuccess() = runTest {
        val result = newsRepository.getNewsHeadlines("us", 1)
        assertThat(result).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun getNewsHeadlines_checkFilledList_returnsCorrectList() = runTest {
        val result = newsRepository.getNewsHeadlines("us", 1)
        assertThat(result.data!!.articles).containsExactlyElementsIn(newsFromApiList)
    }

    @Test
    fun getNewsHeadlines_checkNullInsteadOfList_returnsError() = runTest {
        fakeApiService.setList(null)
        val result = newsRepository.getNewsHeadlines("us", 1)
        assertThat(result).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun getSearchedNews_checkFilledList_returnsSuccess() = runTest {
        val result = newsRepository.getSearchedNews("test", "us", 1)
        assertThat(result).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun getSearchedNews_checkFilledList_returnsCorrectList() = runTest {
        val result = newsRepository.getSearchedNews("test", "us", 1)
        assertThat(result.data!!.articles).containsExactlyElementsIn(newsFromApiList)
    }

    @Test
    fun getSearchedNews_checkNullInsteadOfList_returnsError() = runTest {
        fakeApiService.setList(null)
        val result = newsRepository.getSearchedNews("test", "us", 1)
        assertThat(result).isInstanceOf(Resource.Error::class.java)
    }


    @Test
    fun getSavedNews_checkFilledList_returnsCorrectList() = runTest {
        val firstResult = newsRepository.getSavedNews().first()
        assertThat(firstResult).containsExactlyElementsIn(savedNewsList)
    }

    @Test
    fun getSavedNews_checkNullInsteadOfList_returnsEmptyList() = runTest {
        fakeSavedNewsDao.setList(null)
        val firstResult = newsRepository.getSavedNews().first()
        assertThat(firstResult).isEmpty()
    }

    @Test
    fun saveNews_checkSaving_databaseContainsNewArticle() = runTest {
        val result = newsRepository.saveNews(article4)
        assertThat(result).isEqualTo(123L)
        val savedNews = newsRepository.getSavedNews().first()
        assertThat(savedNews).contains(article4)
    }

    @Test
    fun deleteSavedNews_checkDeleting_DatabaseDoesNotContainArticleAnymore() = runTest {
        val result = newsRepository.deleteSavedNews(article3)
        assertThat(result).isEqualTo(1)
        val savedNews = newsRepository.getSavedNews().first()
        assertThat(savedNews).doesNotContain(article3)
    }
}