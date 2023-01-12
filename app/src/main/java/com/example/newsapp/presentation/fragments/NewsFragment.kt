package com.example.newsapp.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.R
import com.example.newsapp.data.model.APIResponse
import com.example.newsapp.databinding.FragmentNewsBinding
import com.example.newsapp.presentation.adapters.NewsAdapter
import com.example.newsapp.presentation.viewmodels.NewsViewModel
import com.example.newsapp.util.Constants.Companion.COUNTRY
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
    private lateinit var adapter: NewsAdapter
    private val viewModel: NewsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNewsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initSearch()
        viewModel.getNewsHeadlines(COUNTRY, 1)
        newsObserver()
        searchedNewsObserver()
    }

    private fun initSearch() {
        var job: Job? = null

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(text: String?): Boolean {
                job?.cancel()

                if (text == null)
                    return false

                if (text.isEmpty()) {
                    viewModel.getNewsHeadlines(COUNTRY, 1)
                    return false
                }

                job = MainScope().launch {
                    delay(SEARCH_TIME_DELAY)
                    viewModel.getSearchedNews(text, COUNTRY, 1)
                }

                return false
            }

            override fun onQueryTextSubmit(text: String?): Boolean {
                return false
            }
        })

        binding.searchView.setOnCloseListener {
            job?.cancel()
            viewModel.getNewsHeadlines(COUNTRY, 1)
            false
        }
    }

    private fun initRecyclerView() {
        adapter = NewsAdapter()
        binding.rvNews.layoutManager = LinearLayoutManager(requireContext().applicationContext)
        binding.rvNews.adapter = adapter
        adapter.setOnItemClickListener {
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
            when (response) {
                is Resource.Success -> responseSuccess(response.data)
                is Resource.Error -> responseError(response.message)
                is Resource.Loading -> toggleProgressBar(true)
            }
        }
    }

    private fun toggleProgressBar(enable: Boolean) {
        binding.pbNews.visibility = if (enable) VISIBLE else GONE
    }

    private fun responseError(message: String?) {
        toggleProgressBar(false)
        Toast.makeText(context?.applicationContext, message ?: getString(R.string.unexpected_error), Toast.LENGTH_LONG).show()
    }

    private fun responseSuccess(responseData: APIResponse?) {
        toggleProgressBar(false)
        responseData?.let {
            adapter.differ.submitList(it.articles)
        }
    }
}