package com.alfanthariq.mvvm_moviedb.features.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alfanthariq.mvvm_moviedb.ProjectApplication
import com.alfanthariq.mvvm_moviedb.R
import com.alfanthariq.mvvm_moviedb.base.BaseActivity
import com.alfanthariq.mvvm_moviedb.base.BaseFragment
import com.alfanthariq.mvvm_moviedb.databinding.ActivityMainBinding
import com.alfanthariq.mvvm_moviedb.databinding.FragmentReviewsBinding
import com.alfanthariq.mvvm_moviedb.di.ViewModelFactory
import com.alfanthariq.mvvm_moviedb.features.adapter.ReviewsAdapter
import com.alfanthariq.mvvm_moviedb.features.model.ReviewsItem
import com.alfanthariq.mvvm_moviedb.features.viewmodel.DetailMovieViewModel
import com.alfanthariq.mvvm_moviedb.features.viewmodel.ReviewsViewModel
import com.alfanthariq.mvvm_moviedb.utils.EndlessRecyclerViewScrollListener
import com.alfanthariq.mvvm_moviedb.utils.toast

private const val movieIdArgms = "movie_id"

class ReviewsFragment : BaseFragment<ReviewsViewModel>() {
    private var  movieId: String? = null
    private var reviews = ArrayList<ReviewsItem?>()
    lateinit var reviewAdapter : ReviewsAdapter
    private var dataPage = 1
    private var positionIndex: Int = 0
    private var scrollListener: EndlessRecyclerViewScrollListener? = null
    private var totalPage = 0
    private val binding by viewBinding(FragmentReviewsBinding::inflate)

    override var viewModel: ReviewsViewModel
        get() = ViewModelProvider(this, ViewModelFactory{ReviewsViewModel()})[ReviewsViewModel::class.java]
        set(value) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
        arguments?.let {
            movieId = it.getString(movieIdArgms)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = binding.root

    companion object {
        @JvmStatic
        fun newInstance(movieId: String) =
            ReviewsFragment().apply {
                arguments = Bundle().apply {
                    putString(movieIdArgms, movieId)
                }
            }
    }

    override fun onPause() {
        super.onPause()

        positionIndex = (binding.recReviews.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
    }

    override fun onResume() {
        super.onResume()
        binding.recReviews.scrollToPosition(positionIndex)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    fun init(){
        (activity?.application as ProjectApplication).appComponent.inject(viewModel)

        binding.apply {
            btnClose.setOnClickListener {
                dismiss()
            }
        }

        observeReview()
        observeFailedRequest()
        setupRecycler()

        (activity as DetailMovieActivity).showLoadingDialog("Loading data ...")
        viewModel.getReviews(movieId!!, dataPage.toString())
    }

    fun setupRecycler(){
        reviewAdapter = ReviewsAdapter(reviews, requireContext()){

        }

        binding.apply {
            val gridLayout : LinearLayoutManager
            recReviews.apply {
                gridLayout = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                layoutManager = gridLayout
                adapter = reviewAdapter
            }

            scrollListener = object : EndlessRecyclerViewScrollListener(gridLayout) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                    if (dataPage < totalPage) {
                        reviewAdapter.addLoading()
                        dataPage += 1
                        viewModel.getReviews(movieId!!, dataPage.toString())
                    }
                }
            }

            recReviews.addOnScrollListener(scrollListener!!)
        }
    }

    fun observeReview(){
        viewModel.reviews.observe(viewLifecycleOwner, Observer {review ->
            (activity as DetailMovieActivity).hideLoadingDialog()
            reviewAdapter.removeLoading()
            totalPage = review.totalPages!!
            if (dataPage == 1) {
                reviews.clear()
            }
            reviews.addAll(review.results!!)
            reviewAdapter.notifyDataSetChanged()
        })
    }

    fun observeFailedRequest(){
        viewModel.errMsg.observe(viewLifecycleOwner, Observer {
            (activity as DetailMovieActivity).hideLoadingDialog()
            requireContext().toast(it)
        })
    }
}