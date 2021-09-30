package com.example.mvvmnewsapplication.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmnewsapplication.NewsApplication
import com.example.mvvmnewsapplication.models.Article
import com.example.mvvmnewsapplication.models.NewsResponse
import com.example.mvvmnewsapplication.repository.NewsRepository
import com.example.mvvmnewsapplication.util.Resource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response

class NewsViewModel(
    app: Application,
    var newsRepository: NewsRepository
) : AndroidViewModel(app) {

    val breakingNewsMLData: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var pagination = 1
    var breakingNewsResponse: NewsResponse? = null

    val serchMutableLiveData: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchPagination = 1
    var searchNewsResponse: NewsResponse? = null

    init {
        getBreakingNews("us")

    }

    fun getBreakingNews(country: String) = viewModelScope.launch {
//        breakingNewsMLData.postValue(Resource.Loding())
//        //delay(5000)
//        val responce = newsRepository.getBreakingNews(country, pagination)
//
//        breakingNewsMLData.postValue(handelBreackingNewsResponce(responce))
        safeBreakingNews(country)


    }

    fun getSearchNews(searchQuery: String) = viewModelScope.launch {
//        serchMutableLiveData.postValue(Resource.Loding())
//
//        val responce = newsRepository.getSearchNews(searchQuery, searchPagination)
//
//        serchMutableLiveData.postValue(handelSearchNewsResponse(responce))
        safeSearchingNews(searchQuery)

    }

    private suspend fun safeBreakingNews(countryCode: String) {
        breakingNewsMLData.postValue(Resource.Loding())
        try {
            if (hasInternetConnection()) {
                val responce = newsRepository.getBreakingNews(countryCode, pagination)

                breakingNewsMLData.postValue(handelBreackingNewsResponce(responce))

            } else {
                breakingNewsMLData.postValue(Resource.Error("Not Conniction with Internet"))

            }

        } catch (t: Throwable) {
            when (t) {
                is IOException -> breakingNewsMLData.postValue(Resource.Error("Rerofit Error"))
                else -> breakingNewsMLData.postValue(Resource.Error("Something Wrong"))
            }

        }
    }

    private suspend fun safeSearchingNews(querySearch: String) {
        serchMutableLiveData.postValue(Resource.Loding())
        try {
            if (hasInternetConnection()) {
                val responce = newsRepository.getSearchNews(querySearch,searchPagination)

                serchMutableLiveData.postValue(handelSearchNewsResponse(responce))

            } else {
                serchMutableLiveData.postValue(Resource.Error("Not Conniction with Internet"))

            }

        } catch (t: Throwable) {
            when (t) {
                is IOException -> serchMutableLiveData.postValue(Resource.Error("Rerofit Error"))
                else -> serchMutableLiveData.postValue(Resource.Error("Something Wrong"))
            }

        }
    }

    private fun handelBreackingNewsResponce(responce: Response<NewsResponse>): Resource<NewsResponse> {
        if (responce.isSuccessful) {
            responce.body()?.let { resultResponse ->
                pagination++
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = resultResponse
                } else {
                    var oldArticle = breakingNewsResponse?.articles
                    Log.d("ViewModeTest old size", oldArticle?.size.toString())
                    var newArticle = resultResponse.articles
                    Log.d("ViewModeTest new Size", newArticle.size.toString())
                    oldArticle?.addAll(newArticle)
                    Log.d("ViewModeTest after up", oldArticle?.size.toString())

                    Log.d("ViewModeTest breakingNe", breakingNewsResponse.toString())


                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(responce.message())


    }

    private fun handelSearchNewsResponse(responce: Response<NewsResponse>): Resource<NewsResponse> {

        if (responce.isSuccessful) {
            responce.body()?.let { resultResponse ->
                searchPagination++
                if (searchNewsResponse == null) {
                    searchNewsResponse = resultResponse
                } else {
                    var oldArticle = searchNewsResponse?.articles
                    var newArticle = resultResponse.articles
                    oldArticle?.addAll(newArticle)

                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }

        }
        return Resource.Error(responce.message())
    }


    fun saveArticle(article: Article) = GlobalScope.launch {
        newsRepository.upSet(article)
    }

    fun getSaveNews() = newsRepository.getSaveNews()


    fun deleteArticle(article: Article) = GlobalScope.launch {
        newsRepository.deleteArticle(article)
    }

    fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activityNetwork = connectivityManager.activeNetwork ?: return false
            val capabilityNetwork =
                connectivityManager.getNetworkCapabilities(activityNetwork) ?: return false
            return when {
                capabilityNetwork.hasTransport(TRANSPORT_WIFI) -> true
                capabilityNetwork.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilityNetwork.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }


        return false
    }


}