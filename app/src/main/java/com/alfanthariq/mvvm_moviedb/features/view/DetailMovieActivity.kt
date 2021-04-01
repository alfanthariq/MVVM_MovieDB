package com.alfanthariq.mvvm_moviedb.features.view

import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alfanthariq.mvvm_moviedb.ProjectApplication
import com.alfanthariq.mvvm_moviedb.R
import com.alfanthariq.mvvm_moviedb.base.BaseActivity
import com.alfanthariq.mvvm_moviedb.databinding.ActivityDetailMovieBinding
import com.alfanthariq.mvvm_moviedb.databinding.ActivityMainBinding
import com.alfanthariq.mvvm_moviedb.di.ViewModelFactory
import com.alfanthariq.mvvm_moviedb.features.adapter.ReviewsSmallAdapter
import com.alfanthariq.mvvm_moviedb.features.model.ReviewsItem
import com.alfanthariq.mvvm_moviedb.features.viewmodel.DetailMovieViewModel
import com.alfanthariq.mvvm_moviedb.features.viewmodel.MainViewModel
import com.alfanthariq.mvvm_moviedb.utils.DateOperationUtil
import com.alfanthariq.mvvm_moviedb.utils.gone
import com.alfanthariq.mvvm_moviedb.utils.toast
import com.alfanthariq.mvvm_moviedb.utils.visible
import com.bumptech.glide.Glide

class DetailMovieActivity : BaseActivity<DetailMovieViewModel>() {

    private var movieId : String? = null
    lateinit var reviewAdapter : ReviewsSmallAdapter
    private var reviews = ArrayList<ReviewsItem?>()
    private val binding by viewBinding(ActivityDetailMovieBinding::inflate)

    override fun nightMode(): Int = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

    override var viewModel: DetailMovieViewModel
        get() = ViewModelProvider(this, ViewModelFactory{
            DetailMovieViewModel()
        })[DetailMovieViewModel::class.java]
        set(value) {}

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (intent.hasExtra("movie_id")) movieId = intent.getStringExtra("movie_id")

        init()
    }

    fun init() {
        (application as ProjectApplication).appComponent.inject(viewModel)

        binding.apply {
            setSupportActionBar(toolbar)
            toolbar.navigationIcon = ContextCompat.getDrawable(this@DetailMovieActivity, R.drawable.ic_back_white)
            toolbar.navigationIcon!!.setColorFilter(ContextCompat.getColor(this@DetailMovieActivity, R.color.white), PorterDuff.Mode.SRC_ATOP)
            title = "Movie details"

            txtSeeReviews.setOnClickListener {
                val reviewDialog = ReviewsFragment.newInstance(movieId!!)
                val ft = supportFragmentManager.beginTransaction()
                reviewDialog.show(ft, "DialogFragment")
            }
        }

        observeTrailer()
        observeReview()
        observeDetailMovie()
        observeFailedRequest()
        setupRecycler()

        showLoadingDialog("Getting detail data ...")
        if (movieId != null) {
            viewModel.getDetailMovie(movieId!!)
            viewModel.getTrailers(movieId!!)
            viewModel.getReviews(movieId!!)
        }
    }

    fun setupRecycler(){
        reviewAdapter = ReviewsSmallAdapter(reviews, this)

        binding.recReviews.apply {
            val gridLayout = LinearLayoutManager(this@DetailMovieActivity, RecyclerView.HORIZONTAL, false)
            layoutManager = gridLayout
            adapter = reviewAdapter
        }

        toggleEmpty()
    }

    fun toggleEmpty(){
        if (reviews.isEmpty()) {
            binding.txtEmptyReview.visible()
            binding.txtSeeReviews.gone()
        } else {
            binding.txtEmptyReview.gone()
            binding.txtSeeReviews.visible()
        }
    }

    fun String.toFlagEmoji(): String {
        // 1. It first checks if the string consists of only 2 characters: ISO 3166-1 alpha-2 two-letter country codes (https://en.wikipedia.org/wiki/Regional_Indicator_Symbol).
        if (this.length != 2) {
            return this
        }

        val countryCodeCaps = this.toUpperCase() // upper case is important because we are calculating offset
        val firstLetter = Character.codePointAt(countryCodeCaps, 0) - 0x41 + 0x1F1E6
        val secondLetter = Character.codePointAt(countryCodeCaps, 1) - 0x41 + 0x1F1E6

        // 2. It then checks if both characters are alphabet
        if (!countryCodeCaps[0].isLetter() || !countryCodeCaps[1].isLetter()) {
            return this
        }

        return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
    }

    fun observeDetailMovie(){
        viewModel.detailMovie.observe(this, Observer { movies->
            hideLoadingDialog()
            binding.apply {
                title = "${movies.title} (${DateOperationUtil.dateStrFormat("yyyy-mm-dd", "yyyy", movies.releaseDate!!)})"

                Glide.with(this@DetailMovieActivity).load("https://image.tmdb.org/t/p/w185${movies.posterPath}").into(imgPoster)
                txtJudul.text = movies.title
                txtYear.text = DateOperationUtil.dateStrFormat("yyyy-mm-dd", "yyyy", movies.releaseDate!!)
                val genres = ArrayList<String>()
                movies.genres?.forEach {
                    genres.add(it?.name!!)
                }
                txtGenre.text = TextUtils.join(" / ", genres)
                txtOverview.text = movies.overview
                txtStatus.text = movies.status

                val countries = ArrayList<String>()
                movies.productionCountries?.forEach {
                    countries.add(it?.iso31661!!.toLowerCase().toFlagEmoji())
                }
                txtCountry.text = "Country : \n${TextUtils.join("  ", countries)}"

                val lang = ArrayList<String>()
                movies.spokenLanguages?.forEach {
                    lang.add(it?.name!!)
                }
                txtLanguages.text = "Spoken language(s) : \n${TextUtils.join(", ", lang)}"

                txtStatus.visible()
                txtRating.text = movies.voteAverage.toString()
                containerBox.visible()
            }
        })
    }

    fun observeTrailer(){
        viewModel.trailers.observe(this, Observer {trailer ->
            binding.apply {
                if (trailer.results!!.isNotEmpty()) {
                    if (trailer.results[0]?.site?.toLowerCase() == "youtube") {
                        youtubeWebView.setBackgroundColor(Color.TRANSPARENT)
                        youtubeWebView.webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                                return false
                            }
                        }

                        val webSettings = youtubeWebView.settings
                        webSettings.javaScriptEnabled = true
                        webSettings.loadWithOverviewMode = true
                        webSettings.useWideViewPort = true

                        youtubeWebView.loadUrl("https://www.youtube.com/embed/${trailer.results[0]?.key}")
                        youtubeWebView.visible()
                        txtEmptyTrailer.gone()
                    } else {
                        youtubeWebView.gone()
                        txtEmptyTrailer.visible()
                        txtEmptyTrailer.text = "Not available"
                    }
                } else {
                    youtubeWebView.gone()
                    txtEmptyTrailer.visible()
                    txtEmptyTrailer.text = "Not available"
                }
            }
        })
    }

    fun observeReview(){
        viewModel.reviews.observe(this, Observer {review ->
            reviews.clear()
            reviews.addAll(review.results!!.toMutableList())
            reviewAdapter.notifyDataSetChanged()
            toggleEmpty()
        })
    }

    fun observeFailedRequest(){
        viewModel.errMsg.observe(this, Observer {
            hideLoadingDialog()
            toast(it)
        })
    }
}