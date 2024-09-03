package com.example.thenewsapp.repository

import androidx.room.Query
import com.example.thenewsapp.api.RetrofitInstance
import com.example.thenewsapp.db.ArticleDatabase
import com.example.thenewsapp.models.Article
import retrofit2.Retrofit

class NewsRepository(val db:ArticleDatabase) {

    suspend fun getHeadlines(countryCode:String,pageNumber:Int)=
        RetrofitInstance.api.getHeadlines(countryCode,pageNumber)

    suspend fun searchNews(searchQuery: String,pageNumber: Int)=
        RetrofitInstance.api.searchForNews(searchQuery,pageNumber)


    suspend fun upsert(article: Article)=
        db.getArticleDao().upsert(article)


    fun getFavoriteNews()=db.getArticleDao().getAllArticles()


    suspend fun deleteArticle(article: Article)=db.getArticleDao().deleteArticle(article)
}