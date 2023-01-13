package com.example.newsapp.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.data.model.APIResponse
import com.example.newsapp.databinding.FragmentNewsBinding
import com.example.newsapp.presentation.adapters.NewsAdapter
import com.example.newsapp.presentation.viewmodels.NewsViewModel
import com.example.newsapp.util.Constants.Companion.PAGE_SIZE
import com.example.newsapp.util.Constants.Companion.SEARCH_TIME_DELAY
import com.example.newsapp.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewsFragment : Fragment() {
    private lateinit var binding: FragmentNewsBinding
    private lateinit var newsAdapter: NewsAdapter
    private val viewModel: NewsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    private var isScrolling = false
    private var isLoading = false
    private var isLastPageAll = false
    private var isLastPageSearch = false
    private var isSearching = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNewsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initSearch()
        if (viewModel.newsResponse == null) {
            viewModel.getNewsHeadlines(false)
        }
        searchedNewsObserver()
        newsObserver()
    }

    private fun initSearch() {
        var job: Job? = null

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(text: String?): Boolean {
                job?.cancel()

                if (text == null)
                    return false

                if (text.isEmpty()) {
                    setSearching(false)
                    viewModel.getNewsHeadlinesFromCache()
                    return false
                }

                job = MainScope().launch {
                    delay(SEARCH_TIME_DELAY)
                    setSearching(true)
                    viewModel.resetSearchedNews()
                    viewModel.getSearchedNews(text, false)
                }

                return false
            }

            override fun onQueryTextSubmit(text: String?): Boolean {
                return false
            }
        })

        binding.searchView.setOnCloseListener {
            job?.cancel()
            setSearching(false)
            viewModel.getNewsHeadlinesFromCache()
            false
        }
    }

    private fun setSearching(searching: Boolean) {
        isSearching = searching
        isScrolling = false
        isLastPageSearch = false
    }

    private fun initRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvNews.apply {
            layoutManager = LinearLayoutManager(requireContext().applicationContext)
            adapter = newsAdapter
            addOnScrollListener(rvScrollListener)
        }

        newsAdapter.setOnItemClickListener {
            if (it.url.isNullOrEmpty())
                return@setOnItemClickListener

            val bundle = Bundle().apply {
                putSerializable("selectedArticle", it)
            }

            findNavController().navigate(
                R.id.action_newsFragment_to_infoFragment,
                bundle
            )
        }
    }

    private fun newsObserver() {
        viewModel.newsHeadlines.observe(viewLifecycleOwner) { response ->
            when (response){
                is Resource.Success -> responseSuccess(response.data)
                is Resource.Error -> responseError(response.message)
                is Resource.Loading -> toggleProgressBar(true)
            }
        }
    }

    private fun searchedNewsObserver() {
        viewModel.searchedNewsHeadlines.observe(viewLifecycleOwner) { response ->
            if (!isSearching) {
                return@observe
            }

            when (response) {
                is Resource.Success -> responseSuccess(response.data)
                is Resource.Error -> responseError(response.message)
                is Resource.Loading -> toggleProgressBar(true)
            }
        }
    }

    private fun toggleProgressBar(enable: Boolean) {
        binding.pbNews.visibility = if (enable) VISIBLE else GONE
        isLoading = enable
    }

    private fun responseError(message: String?) {
        toggleProgressBar(false)
        Toast.makeText(context?.applicationContext, message ?: getString(R.string.unexpected_error), Toast.LENGTH_LONG).show()
    }

    private fun responseSuccess(responseData: APIResponse?) {
        toggleProgressBar(false)
        responseData?.let {
            newsAdapter.differ.submitList(it.articles.toList())
            var totalPages = it.totalResults / PAGE_SIZE

            if (it.totalResults % PAGE_SIZE != 0) {
                totalPages++
            }

            if (isSearching) {
                isLastPageSearch = viewModel.searchedNewsPage >= totalPages
            } else {
                isLastPageAll = viewModel.newsPage >= totalPages
            }
        }
    }

    private val rvScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition > 0
            val isTotalMoreThanVisible = totalItemCount >= PAGE_SIZE
            val isLastPage = if (isSearching) isLastPageSearch else isLastPageAll

            if (isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible
                && !isLoading && !isLastPage && isScrolling) {
                if (isSearching && !binding.searchView.query.isNullOrEmpty()) {
                    viewModel.getSearchedNews(binding.searchView.query.toString(), true)
                } else {
                    viewModel.getNewsHeadlines( true)
                }
                isScrolling = false
            }
        }
    }
}