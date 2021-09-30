package com.example.mvvmnewsapplication.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmnewsapplication.R
import com.example.mvvmnewsapplication.adapter.NewsAdapter
import com.example.mvvmnewsapplication.ui.NewsActivity
import com.example.mvvmnewsapplication.ui.NewsViewModel
import com.example.mvvmnewsapplication.util.Constants.Companion.QUERY_PAGE_SIZE
import com.example.mvvmnewsapplication.util.Resource
import kotlinx.coroutines.delay

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var breakingNewsAdapter: NewsAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var progressBar: ProgressBar
    val TAG = "BreakingNewsFragment"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rvBreakingNews)
        progressBar = view.findViewById(R.id.paginationProgressBar)

        viewModel = (activity as NewsActivity).viewModel

        breakingNewsAdapter = NewsAdapter()
        setUpRecyclerView()


        breakingNewsAdapter.setOnItemClickListener {
            var bundel: Bundle = Bundle().apply {
                putSerializable("articl", it)
            }

            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleNewsFragment,
                bundel
            )


        }


        viewModel.breakingNewsMLData.observe(viewLifecycleOwner, Observer { response ->

            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponce ->
                        breakingNewsAdapter.differ.submitList(newsResponce.articles.toList())
                        val totalPage = newsResponce.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.pagination == totalPage

                        if(isLastPage){
                            recyclerView.setPadding(0,0,0,0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { msg ->
                        Log.d(TAG, "Error an occur $msg")
                        Toast.makeText(activity,"Error Internet Connection",Toast.LENGTH_LONG).show()
                    }

                }
                is Resource.Loding -> {
                    showProgressBar()

                }
            }

        })


    }

    private fun hideProgressBar() {
        progressBar.visibility = View.INVISIBLE
        isLoading = false

    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
        isLoading = true
    }


    var isLoading = false
    var isScrolling = false
    var isLastPage = false

    val scrollListener = object : RecyclerView.OnScrollListener() {

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

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotABeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val ShouldPagenate = isNotLoadingAndNotLastPage && isAtLastItem && isNotABeginning
                    && isTotalMoreThanVisible && isScrolling

            if (ShouldPagenate) {
                viewModel.getBreakingNews("us")
                isScrolling = false
            }


        }
    }


    private fun setUpRecyclerView() {
        recyclerView.apply {
            adapter = breakingNewsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)

        }
    }
}