package com.example.newsapp.data.api

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.newsapp.BuildConfig
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@OptIn(ExperimentalCoroutinesApi::class)
class APIServiceTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var server: MockWebServer
    private lateinit var service: APIService

    @Before
    fun setUp() {
        server = MockWebServer()
        service = Retrofit.Builder()
            .baseUrl(server.url(""))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIService::class.java)

        val inputStream = javaClass.classLoader!!.getResourceAsStream("newsresponse.json")
        val source = inputStream.source().buffer()
        val mockResponse = MockResponse()
        mockResponse.setBody(source.readString(Charsets.UTF_8))
        server.enqueue(mockResponse)
    }

    @Test
    fun getNewsHeadlines_sendRequest_correctRequest() = runTest {
        service.getNewsHeadlines("us", 1)
        val request = server.takeRequest()
        assertThat(request.path).isEqualTo("/v2/top-headlines?country=us&page=1&apiKey=${BuildConfig.API_KEY}")
    }

    @Test
    fun getNewsHeadlines_sendRequest_receivedResponse() = runTest {
        val responseBody = service.getNewsHeadlines("us", 1).body()
        assertThat(responseBody).isNotNull()
    }

    @Test
    fun getNewsHeadlines_sendRequest_correctResultListSize() = runTest {
        val responseBody = service.getNewsHeadlines("us", 1).body()
        val size = responseBody!!.articles.size
        assertThat(size).isEqualTo(20)
    }

    @Test
    fun getNewsHeadlines_sendRequest_resultsNotEmpty() = runTest {
        val responseBody = service.getNewsHeadlines("us", 1).body()
        val article = responseBody!!.articles[0]
        assertThat(article.author).isNotEmpty()
        assertThat(article.title).isNotEmpty()
        assertThat(article.description).isNotEmpty()
        assertThat(article.url).isNotEmpty()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }
}