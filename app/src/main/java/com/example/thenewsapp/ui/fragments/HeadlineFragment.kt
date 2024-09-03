package com.example.thenewsapp.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thenewsapp.R
import com.example.thenewsapp.adapters.NewsAdapter
import com.example.thenewsapp.databinding.FragmentHeadlineBinding
import com.example.thenewsapp.databinding.ItemNewsBinding
import com.example.thenewsapp.ui.NewsActivity
import com.example.thenewsapp.ui.NewsViewModel
import com.example.thenewsapp.util.Constants
import com.example.thenewsapp.util.Resources


class HeadlineFragment : Fragment() {
    lateinit var newsViewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var retryButton: Button
    lateinit var errorTextView: TextView
    lateinit var itemHeadlinesError: CardView
    lateinit var binding: FragmentHeadlineBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentHeadlineBinding.bind(view)


        itemHeadlinesError=view.findViewById(R.id.itemHeadlinesError)
        val inflater=requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)as LayoutInflater
        val view:View= inflater.inflate(R.layout.item_error,null)


        retryButton=view.findViewById(R.id.retryButton)

        errorTextView=view.findViewById(R.id.errorText)

        newsViewModel=(activity as NewsActivity).newsViewModel

        setupHeadlinesRecycler()

        newsAdapter.setOnItemClickListener {
            val bundle=Bundle().apply {
                putSerializable("article",it)
            }
            findNavController().navigate(R.id.action_headlineFragment_to_articleFragment,bundle)
        }

        newsViewModel.headlines.observe(viewLifecycleOwner) { response ->

            when (response) {
                is Resources.Error -> {
                    TODO()
                }

                is Resources.Loading -> {
                    TODO()
                }

                is Resources.Success -> {
                    TODO()
                }
            }
        }
    }
    var isError=false
    var isLoading=false
    var isLastPage=false
    var isScrolling=false



private fun  hideProgressBar(){
    binding.paginationProgressBar.visibility=View.INVISIBLE
    isLoading=false
}


    private fun showProgressBar(){
        binding.paginationProgressBar.visibility=View.VISIBLE
        isLoading=true
    }

    private fun showErrorMessage(message:String){

        itemHeadlinesError.visibility=View.VISIBLE
        errorTextView.text=message
        isError=true
    }


    val scrollListener=object :RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager

            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val isNoError = !isError

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
             val isNotAtBeginning=firstVisibleItemPosition>=0

            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate=isNoError&&isNotLoadingAndNotLastPage&&isAtLastItem&&isTotalMoreThanVisible
            if (shouldPaginate)
                newsViewModel.getHeadlines("us")
                isScrolling=false
        }



        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }

        }

        }


        private fun setupHeadlinesRecycler(){
            newsAdapter=NewsAdapter()
            binding.recyclerHeadlines.apply {
                adapter=newsAdapter
                layoutManager=LinearLayoutManager(activity
                )
                addOnScrollListener(this@HeadlineFragment.scrollListener)
            }
        }






    }









