package com.example.mvvmnewsapplication.ui.fragments

import android.content.ClipData
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmnewsapplication.R
import com.example.mvvmnewsapplication.adapter.NewsAdapter
import com.example.mvvmnewsapplication.databinding.FragmentSaveNewsBinding
import com.example.mvvmnewsapplication.ui.NewsActivity
import com.example.mvvmnewsapplication.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class SaveNewsFragment : Fragment(R.layout.fragment_save_news) {
    lateinit var viewModel: NewsViewModel
    lateinit var binding: FragmentSaveNewsBinding
    lateinit var newsAdpter: NewsAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSaveNewsBinding.bind(view)

        viewModel = (activity as NewsActivity).viewModel

        setUpRecycleNews()

        newsAdpter.setOnItemClickListener { article ->
            val bundel = Bundle()
            bundel.apply {
                putSerializable("article", article)
            }
            findNavController().navigate(
                R.id.action_saveNewsFragment_to_articleNewsFragment,
                bundel
            )
        }

        viewModel.getSaveNews().observe(viewLifecycleOwner, Observer {
            newsAdpter.differ.submitList(it)
        })


        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN ,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val  position = viewHolder.adapterPosition
                val article = newsAdpter.differ.currentList[position]

                viewModel.deleteArticle(article)

                Snackbar.make(view,"Sucessfuly Deleted",Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        viewModel.saveArticle(article)
                    }
                    show()
                }

            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvSavedNews)
        }


    }


    private fun setUpRecycleNews() {
        newsAdpter = NewsAdapter()
        binding.rvSavedNews.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = newsAdpter
        }
    }


}