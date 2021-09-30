package com.example.mvvmnewsapplication.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.mvvmnewsapplication.R
import com.example.mvvmnewsapplication.data.ArticleDatabase
import com.example.mvvmnewsapplication.databinding.ActivityNewsBinding
import com.example.mvvmnewsapplication.repository.NewsRepository

class NewsActivity : AppCompatActivity() {

    lateinit var binding: ActivityNewsBinding
    lateinit var navController: NavController
    lateinit var viewModel:NewsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var navHostfragment =
            supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment

        navController = navHostfragment.navController
        binding.bnvMain.setupWithNavController(navController)


       val newsRepository = NewsRepository(ArticleDatabase(this))
        val newsViewModelProviderFactory = NewsViewModelProviderFactory(application,newsRepository)

        viewModel = ViewModelProvider(this,newsViewModelProviderFactory).get(NewsViewModel::class.java)






    }
}