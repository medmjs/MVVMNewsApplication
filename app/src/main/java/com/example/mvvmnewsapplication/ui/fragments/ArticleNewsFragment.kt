package com.example.mvvmnewsapplication.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.mvvmnewsapplication.R
import com.example.mvvmnewsapplication.databinding.FragmentArticleNewsBinding
import com.example.mvvmnewsapplication.ui.NewsActivity
import com.example.mvvmnewsapplication.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class ArticleNewsFragment : Fragment(R.layout.fragment_article_news) {
    lateinit var viewModel: NewsViewModel
    lateinit var binding:FragmentArticleNewsBinding
     val args: ArticleNewsFragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentArticleNewsBinding.bind(view)

        viewModel = (activity as NewsActivity).viewModel

        val article = args.articl


        binding.webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url)
        }

        binding.fab.setOnClickListener {
            viewModel.saveArticle(article)

            Snackbar.make(view,"Article is Saved",Snackbar.LENGTH_LONG).show()
        }







    }
}