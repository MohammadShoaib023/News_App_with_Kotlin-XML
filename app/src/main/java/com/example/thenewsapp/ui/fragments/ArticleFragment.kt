package com.example.thenewsapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.webkit.WebViewClient
import com.example.thenewsapp.R
import com.example.thenewsapp.databinding.FragmentArticleBinding
import com.example.thenewsapp.ui.NewsActivity
import com.example.thenewsapp.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.fragment.navArgs


class ArticleFragment : Fragment(R.layout.fragment_article) {
    lateinit var newsViewModel: NewsViewModel
    val args:ArticleFragmentArgs by navArgs()
    lateinit var bining:FragmentArticleBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bining= FragmentArticleBinding.bind(view)

        newsViewModel=(activity as NewsActivity).newsViewModel
        val  article=args.article

        bining.webView.apply {
            webViewClient= WebViewClient()
            article.url?.let{
                loadUrl(it)
            }

        }


        bining.fab.setOnClickListener {
            newsViewModel.addToFavorites(article)
            Snackbar.make(view,"added to favorites",Snackbar.LENGTH_SHORT).show()
        }
    }

}