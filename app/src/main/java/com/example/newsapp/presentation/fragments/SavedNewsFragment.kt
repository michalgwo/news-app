package com.example.newsapp.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentSavedNewsBinding
import com.example.newsapp.presentation.adapters.NewsAdapter
import com.example.newsapp.presentation.viewmodels.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedNewsFragment : Fragment() {
    private lateinit var binding: FragmentSavedNewsBinding
    private lateinit var savedNewsAdaper: NewsAdapter
    private val viewModel: NewsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSavedNewsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        savedNewsObserver()
        swipeToDeleteArticle()
    }

    private fun initRecyclerView() {
        savedNewsAdaper = NewsAdapter()
        binding.rvSavedNews.layoutManager = LinearLayoutManager(requireContext().applicationContext)
        binding.rvSavedNews.adapter = savedNewsAdaper
        savedNewsAdaper.setOnItemClickListener {
            if (it.url.isNullOrEmpty())
                return@setOnItemClickListener

            val bundle = Bundle().apply {
                putSerializable("selectedArticle", it)
            }

            findNavController().navigate(
                R.id.action_savedNewsFragment_to_infoFragment,
                bundle
            )
        }
    }

    private fun savedNewsObserver() {
        viewModel.getSavedNews().observe(viewLifecycleOwner) {
            if (it == null)
                return@observe

            savedNewsAdaper.differ.submitList(it)
        }
    }

    private fun swipeToDeleteArticle() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = savedNewsAdaper.differ.currentList[position]
                viewModel.deleteSavedArticle(article)
                Snackbar.make(requireView(), getString(R.string.article_deleted), Snackbar.LENGTH_LONG).apply {
                    setAction(getString(R.string.undo)) {
                        viewModel.saveArticle(article)
                    }
                }.show()
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.rvSavedNews)
    }
}