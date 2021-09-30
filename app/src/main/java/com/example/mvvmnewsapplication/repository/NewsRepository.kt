package com.example.mvvmnewsapplication.repository

import com.example.mvvmnewsapplication.data.ArticleDao
import com.example.mvvmnewsapplication.data.ArticleDatabase
import com.example.mvvmnewsapplication.models.Article
import com.example.mvvmnewsapplication.network.RetrofitInstance
import com.example.mvvmnewsapplication.network.RetrofitInstance.Companion.api
import com.example.mvvmnewsapplication.util.Constants.Companion.API_KEY

class NewsRepository(val newsdb: ArticleDatabase) {


    suspend fun getBreakingNews(countryCode: String, page: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, page)

    suspend fun getSearchNews(serchQuery:String,pageNumber:Int) =
        RetrofitInstance.api.searchNews(serchQuery,pageNumber)


    suspend fun upSet(article: Article)=newsdb.getArticleDao().upSet(article)

    fun getSaveNews()=newsdb.getArticleDao().getAllArticle()

    suspend fun deleteArticle(article: Article) = newsdb.getArticleDao().deleteData(article)


}