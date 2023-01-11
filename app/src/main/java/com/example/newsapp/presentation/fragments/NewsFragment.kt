package com.example.newsapp.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentNewsBinding
import com.example.newsapp.presentation.adapters.NewsAdapter
import com.example.newsapp.presentation.viewmodels.NewsViewModel
import com.example.newsapp.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NewsFragment : Fragment() {
    private lateinit var binding: FragmentNewsBinding
    @Inject lateinit var adapter: NewsAdapter
    private val viewModel: NewsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNewsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.rvNews.layoutManager = LinearLayoutManager(requireContext().applicationContext)
        binding.rvNews.adapter = adapter
        adapter.setOnItemClickListener {
            if (it.url.isEmpty()) {
                return@setOnItemClickListener
            }

            val bundle = Bundle().apply {
                putSerializable("selectedArticle", it)
            }

            findNavController().navigate(R.id.action_newsFragment_to_infoFragment, bundle)
        }

        populateRecyclerView()
    }

    private fun populateRecyclerView() {
        viewModel.getNewsHeadlines("us", 1)
        viewModel.newsHeadlines.observe(viewLifecycleOwner) { response ->
            when (response){
                is Resource.Success -> {
                    toggleProgressBar(false)
                    response.data?.let {
                        adapter.differ.submitList(it.articles)
                    }
                }
                is Resource.Error -> {
                    toggleProgressBar(false)
                    Toast.makeText(context?.applicationContext, response.message ?: "Unexpected error occurred", Toast.LENGTH_LONG).show()
                }
                is Resource.Loading -> {
                    toggleProgressBar(true)
                }
            }
        }
    }

    private fun toggleProgressBar(enable: Boolean) {
        binding.pbNews.visibility = if (enable) VISIBLE else GONE
    }
}