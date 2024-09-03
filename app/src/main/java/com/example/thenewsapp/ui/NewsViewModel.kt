package com.example.thenewsapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Query
import com.bumptech.glide.load.engine.Resource
import com.example.thenewsapp.models.Article
import com.example.thenewsapp.models.NewsResponse
import com.example.thenewsapp.repository.NewsRepository
import com.example.thenewsapp.util.Resources
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response

class NewsViewModel (app:Application,val newsRepository: NewsRepository):AndroidViewModel(app){
    val headlines:MutableLiveData<Resources<NewsResponse>> = MutableLiveData()
    var headlinesPage=1
    var headlinesResponse:NewsResponse?=null
    val searchNews: MutableLiveData<Resources<NewsResponse>> =MutableLiveData()
    var searchNewsPage=1
    var searchNewsResponse:NewsResponse?=null
    var newSearchQuery:String?=null
    var oldSearchQuery:String?=null



    init {
        getHeadlines(countryCode = "us")
    }
 fun getHeadlines(countryCode: String)=viewModelScope.launch {
    headlinesInternet(countryCode)
}

    fun searchNews(searchQuery: String)=viewModelScope.launch {
        searchNewsInternet(searchQuery)
    }


    private fun handleHeadlinesResponse(response: Response<NewsResponse>):Resources<NewsResponse>{
        if (response.isSuccessful){
            response.body()?.let {
                resultResponse->
                headlinesPage++
                if (headlinesResponse==null)
                {
                    headlinesResponse=resultResponse
                }else{
                    val oldArticles=headlinesResponse?.articles
                    val newArtists=resultResponse.articles
                    oldArticles?.addAll(newArtists)
                }
                return Resources.Success(headlinesResponse?:resultResponse)
            }
        }
        return Resources.Error(response.message())
    }


    private fun handelSearchNewsResponse(response: Response<NewsResponse>):Resources<NewsResponse>{
        if (response.isSuccessful){
            response.body()?.let {
                    resultResponse->

                if (searchNewsResponse==null||newSearchQuery==null)
                {
                    searchNewsPage=1
                    oldSearchQuery=newSearchQuery
                    searchNewsResponse=resultResponse
                }else{
                    searchNewsPage++
                    val oldArticles=searchNewsResponse?.articles
                    val newArtists=resultResponse.articles
                    oldArticles?.addAll(newArtists)
                }
                return Resources.Success(headlinesResponse?:resultResponse)
            }
        }
        return Resources.Error(response.message())
    }


    fun addToFavorites(article:Article)=viewModelScope.launch {
        newsRepository.upsert(article)
    }
    fun getFavouriteNews()=newsRepository.getFavoriteNews()

    fun deleteArticle(article: Article)=viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    fun internetConnection(context: Context):Boolean{
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
        return getNetworkCapabilities(activeNetwork)?.run {
            when{
                hasTransport(NetworkCapabilities.TRANSPORT_WIFI)->true
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)->true
                hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)->true
                else ->false
            }
        }?:false
    }
    }


    private suspend fun headlinesInternet(countryCode:String){
        headlines.postValue(Resources.Loading())


    try{
        if (internetConnection(this.getApplication())) {
        val response= newsRepository.getHeadlines(countryCode,headlinesPage )
             headlines.postValue(handleHeadlinesResponse(response))
            }
        }catch(t:Throwable){
      when(t){
          is IOException ->headlines.postValue(Resources.Error("Unable to connection"))
          else ->headlines.postValue(Resources.Error("No Signal"))
      }
        }
    }


    private suspend fun searchNewsInternet(searchQuery:String ){
        newSearchQuery=searchQuery
        searchNews.postValue(Resources.Loading())
        try{
            if (internetConnection(this.getApplication())) {
                val response= newsRepository.searchNews(searchQuery,searchNewsPage )
                headlines.postValue(handelSearchNewsResponse(response))
            }
        }catch(t:Throwable){
            when(t){
                is IOException ->headlines.postValue(Resources.Error("Unable to connection"))
                else ->headlines.postValue(Resources.Error("No Signal"))
            }
        }
    }









}