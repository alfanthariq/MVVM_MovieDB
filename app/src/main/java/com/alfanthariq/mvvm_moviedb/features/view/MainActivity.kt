package com.alfanthariq.mvvm_moviedb.features.view

import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.setPadding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alfanthariq.mvvm_moviedb.ProjectApplication
import com.alfanthariq.mvvm_moviedb.R
import com.alfanthariq.mvvm_moviedb.base.ApiService
import com.alfanthariq.mvvm_moviedb.base.BaseActivity
import com.alfanthariq.mvvm_moviedb.databinding.ActivityMainBinding
import com.alfanthariq.mvvm_moviedb.di.ViewModelFactory
import com.alfanthariq.mvvm_moviedb.features.adapter.MovieAdapter
import com.alfanthariq.mvvm_moviedb.features.model.MoviesItem
import com.alfanthariq.mvvm_moviedb.features.viewmodel.MainViewModel
import com.alfanthariq.mvvm_moviedb.utils.*
import com.google.android.material.appbar.AppBarLayout
import com.mancj.slideup.SlideUp
import com.mancj.slideup.SlideUpBuilder
import nl.bryanderidder.themedtogglebuttongroup.ThemedButton
import javax.inject.Inject

class MainActivity : BaseActivity<MainViewModel>() {

    private var doubleBackToExitPressedOnce = false
    private var items : ArrayList<MoviesItem?> = ArrayList()
    private var selectedGenres = ""
    private lateinit var movieAdapter : MovieAdapter
    private var dataPage = 1
    private var positionIndex: Int = 0
    private var scrollListener: EndlessRecyclerViewScrollListener? = null
    private var totalPage = 0
    private lateinit var slideUp: SlideUp
    private lateinit var txtBadge : TextView
    private lateinit var recLayout : LinearLayoutManager
    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun nightMode(): Int = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

    override var viewModel: MainViewModel
        get() = ViewModelProvider(this, ViewModelFactory{
            MainViewModel()
        })[MainViewModel::class.java]
        set(value) {}

    override fun onPause() {
        super.onPause()

        positionIndex = (binding.recMovies.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
    }

    override fun onResume() {
        super.onResume()

        toggleEmpty()
        binding.recMovies.scrollToPosition(positionIndex)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
    }

    fun init() {
        (application as ProjectApplication).appComponent.inject(viewModel)

        binding.apply {
            btnApply.setOnClickListener {
                val selButton = genresGroup.selectedButtons
                val ids = ArrayList<Int>()
                selButton.forEach {
                    ids.add(it.tag as Int)
                }
                selectedGenres = TextUtils.join(",", ids)
                if (selButton.isNotEmpty()) txtBadge.visible() else txtBadge.gone()
                txtBadge.text = selButton.size.toString()
                slideUp.hide()
                showLoadingDialog("Getting data ...")
                scrollListener?.resetState()
                dataPage = 1
                viewModel.getMovieItems(selectedGenres, dataPage.toString())
            }

            txtClearSelection.setOnClickListener {
                for (btn : ThemedButton in ArrayList<ThemedButton>(genresGroup.selectedButtons)) {
                    genresGroup.selectButtonWithAnimation(btn)
                }
            }

            dim.setOnClickListener { if (slideUp.isVisible) slideUp.hide() }

            slideUp = SlideUpBuilder(contentGenres)
                .withListeners(object : SlideUp.Listener.Events {
                    override fun onSlide(percent: Float) {
                        dim.visibility = View.VISIBLE
                        dim.alpha = 0.7f - percent / 100
                    }

                    override fun onVisibilityChanged(visibility: Int) {
                        if (visibility == View.GONE) {
                            dim.alpha = 0f
                            dim.visibility = View.GONE
                            dim.isClickable = false
                        } else {
                            dim.visibility = View.VISIBLE
                            dim.isClickable = true
                        }
                    }
                })
                .withStartGravity(Gravity.BOTTOM)
                .withLoggingEnabled(false)
                .withGesturesEnabled(true)
                .withStartState(SlideUp.State.HIDDEN)
                .build()

            setToolbar(toolbar, getString(R.string.app_name), false)
            appBar.addOnOffsetChangedListener(object : AppBarStateChangedListener() {
                override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
                    if (state == State.COLLAPSED) fabUp.show() else fabUp.hide()
                }
            })

            fabUp.setOnClickListener {
                appBar.setExpanded(true, true)
                recLayout.smoothScrollToPosition(recMovies, null, 0)
            }
        }

        setupRecycler()
        observeMoviesData()
        observeGenreData()
        failedRequest()

        showLoadingDialog("Loading data ...")
        viewModel.getGenres()
    }

    fun observeMoviesData() {
        viewModel.movieItems.observe(this, Observer { datas ->
            movieAdapter.removeLoading()
            totalPage = datas.totalPages!!
            if (dataPage == 1) {
                items.clear()
            }
            items.addAll(datas.results!!)
            movieAdapter.notifyDataSetChanged()
            toggleEmpty()
            hideLoadingDialog()
        })
    }

    fun observeGenreData() {
        viewModel.genres.observe(this, Observer { data ->
            binding.apply {
                genresGroup.removeAllViews()
                genresGroup.selectableAmount = data.genres!!.size
                val margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, resources.displayMetrics).toInt()
                val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics).toInt()
                val layParam = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                data.genres.forEach { item ->
                    val btn = ThemedButton(this@MainActivity)
                    btn.id = ViewCompat.generateViewId()
                    btn.text = item?.name!!
                    btn.tag = item.id
                    btn.selectedBgColor = ContextCompat.getColor(this@MainActivity, R.color.selectedToggle)
                    btn.textColor = ContextCompat.getColor(this@MainActivity, R.color.iconTextColorRevert)
                    btn.bgColor = ContextCompat.getColor(this@MainActivity, R.color.bgToggle)

                    val dimenFontSize = resources.getDimension(R.dimen.toggleFontSize) / resources.displayMetrics.density

                    btn.tvText.setPadding(padding)
                    btn.tvText.textSize = dimenFontSize
                    btn.tvSelectedText.setPadding(padding)
                    btn.tvSelectedText.textSize = dimenFontSize

                    layParam.setMargins(margin, margin, margin, margin)
                    genresGroup.addView(btn, layParam)
                }
            }
        })

        viewModel.getMovieItems(selectedGenres, dataPage.toString())
    }

    fun failedRequest() {
        viewModel.errorMsg.observe(this, Observer { msg ->
            if (msg.isNotEmpty()) {
                hideLoadingDialog()
                toggleEmpty()
                toast(msg)
            }
        })
    }

    fun toggleEmpty(){
        if (items.isEmpty()) binding.emptyView.visible() else binding.emptyView.gone()
    }

    override fun onBackPressed() {
        if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Press BACK again to exit.", Toast.LENGTH_SHORT).show()

            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
        } else {
            super.onBackPressed()
            return
        }
    }

    fun setupRecycler(){
        movieAdapter = MovieAdapter(items, this, {
            val map = HashMap<String, String>()
            map["movie_id"] = it.id.toString()
            AppRoute.open(this, DetailMovieActivity::class.java, map)
        }, { searchCount ->

        })

        recLayout = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        binding.recMovies.apply {
            layoutManager = recLayout
            adapter = movieAdapter
        }

        scrollListener = object : EndlessRecyclerViewScrollListener(recLayout) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                if (dataPage < totalPage) {
                    movieAdapter.addLoading()
                    dataPage += 1
                    viewModel.getMovieItems(selectedGenres, dataPage.toString())
                }
            }
        }

        binding.recMovies.addOnScrollListener(scrollListener!!)

        toggleEmpty()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu)

        val menuItem = menu.findItem(R.id.action_filter)
        val actionView = menuItem.actionView
        txtBadge = actionView.findViewById(R.id.badge)

        actionView.setOnClickListener {
            onOptionsItemSelected(menuItem)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter -> {
                slideUp.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
