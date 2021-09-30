package com.example.mvvmnewsapplication.ui.fragments

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmnewsapplication.R
import com.example.mvvmnewsapplication.adapter.NewsAdapter
import com.example.mvvmnewsapplication.databinding.FragmentSearchNewsBinding
import com.example.mvvmnewsapplication.ui.NewsActivity
import com.example.mvvmnewsapplication.ui.NewsViewModel
import com.example.mvvmnewsapplication.util.Constants

import com.example.mvvmnewsapplication.util.Constants.Companion.SEARCH_NEWS_DELAY_TIME
import com.example.mvvmnewsapplication.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {

    lateinit var binding: FragmentSearchNewsBinding
    lateinit var searchAdpter:NewsAdapter
    val TAG="SearchNewsFragment"

    lateinit var viewModel: NewsViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchNewsBinding.bind(view)

        viewModel = (activity as NewsActivity).viewModel
        searchAdpter = NewsAdapter()
        setUpRecycleView()

        var job:Job? =null

        binding.etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_DELAY_TIME)

                editable?.let {
                    if(it.toString().isNotEmpty()){
                        viewModel.getSearchNews(it.toString())
                    }
                }

            }

        }
        viewModel.serchMutableLiveData.observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Success ->{
                    hideProgressBar()
                    it.data?.let {newsResponse ->
                        searchAdpter.differ.submitList(newsResponse.articles.toList())
                        val totalPage = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.searchPagination == totalPage
                        if(isLastPage){
                            binding.rvSearchNews.setPadding(0,0,0,0)
                        }
                    }

                }
                is Resource.Error->{
                    it.message?.let {
                        hideProgressBar()
                        Log.d(TAG,"Error in Serch Fragment")
                    }

                }
                is Resource.Loding->{
                    showProgressBar()

                }
            }


        })







    }

    private fun hideProgressBar(){
        binding.searchPaginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }
    private fun showProgressBar(){
        binding.searchPaginationProgressBar.visibility = View.VISIBLE
        isLoading =true
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
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val ShouldPagenate = isNotLoadingAndNotLastPage && isAtLastItem && isNotABeginning
                    && isTotalMoreThanVisible && isScrolling

            if (ShouldPagenate) {
                viewModel.getSearchNews(binding.etSearch.text.toString())
                isScrolling = false
            }


        }
    }



    private fun setUpRecycleView(){
        binding.rvSearchNews.apply {
            adapter = searchAdpter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }
    }
}