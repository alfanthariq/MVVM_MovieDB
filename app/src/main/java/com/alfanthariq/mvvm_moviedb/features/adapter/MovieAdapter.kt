package com.alfanthariq.mvvm_moviedb.features.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.alfanthariq.mvvm_moviedb.R
import com.alfanthariq.mvvm_moviedb.databinding.ItemLoadingBinding
import com.alfanthariq.mvvm_moviedb.databinding.ItemMovieBinding
import com.alfanthariq.mvvm_moviedb.features.model.MoviesItem
import com.alfanthariq.mvvm_moviedb.utils.DateOperationUtil
import com.bumptech.glide.Glide
import java.util.*
import kotlin.collections.ArrayList

class MovieAdapter (val items : ArrayList<MoviesItem?>,
                    val context : Context,
                    val callback : (MoviesItem) -> Unit,
                    val callbackFilter : (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private val VIEW_TYPE_LOADING = 1
    private val VIEW_TYPE_ITEM = 0

    var itemsFiltered : ArrayList<MoviesItem?>

    init {
        itemsFiltered = items
    }

    fun addLoading() {
        val lastIdx = items.lastIndex
        if (items[lastIdx] != null) {
            items.add(null)
            itemsFiltered = items
            notifyItemInserted(lastIdx+1)
        }
    }

    fun removeLoading() {
        if (items.isNotEmpty()) {
            val lastIdx = items.lastIndex
            if (items[lastIdx] == null) {
                items.removeAt(lastIdx)
                itemsFiltered = items
                notifyItemRemoved(lastIdx)
                //notifyDataSetChanged()
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                itemsFiltered = if (charSearch.isEmpty()) {
                    items
                } else {
                    val resultList = ArrayList<MoviesItem?>()
                    for (row in items) {
                        if (row?.title?.toLowerCase(Locale.ROOT)!!.contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = itemsFiltered
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                itemsFiltered = results?.values as ArrayList<MoviesItem?>
                callbackFilter(itemsFiltered.size)
                notifyDataSetChanged()
            }
        }
    }

    class MovieViewHolder(val binding : ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            inline fun createVH(
                parent: ViewGroup,
                crossinline block: (inflater: LayoutInflater, container: ViewGroup, attach: Boolean) -> ViewBinding
            ) = MovieViewHolder(block(LayoutInflater.from(parent.context), parent, false))
        }
    }

    class LoadingHolder(binding : ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            inline fun createVH(
                parent: ViewGroup,
                crossinline block: (inflater: LayoutInflater, container: ViewGroup, attach: Boolean) -> ViewBinding
            ) = LoadingHolder(block(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val vh: RecyclerView.ViewHolder = if (viewType == VIEW_TYPE_ITEM) {
            println("Movie Holder")
            MovieViewHolder.createVH(parent, ItemMovieBinding::inflate)
        } else {
            println("Loading Holder")
            LoadingHolder.createVH(parent, ItemLoadingBinding::inflate)
        }
        return vh
    }

    override fun getItemCount(): Int {
        println("filtered : ${itemsFiltered.size} items : ${items.size}")
        return itemsFiltered.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MovieViewHolder) {
            (holder.binding as ItemMovieBinding).apply {
                val data = itemsFiltered[position]
                txtJudul.text = data?.title
                txtOverview.text = data?.overview
                txtReleaseDate.text = "Release date : ${DateOperationUtil.dateStrFormat("yyyy-MM-dd", "dd/MM/yyyy", data?.releaseDate!!)}"

                Glide.with(context)
                    .load("https://image.tmdb.org/t/p/w92${data.posterPath}")
                    .into(imgPoster)

                if (position % 2 == 0) {
                    container.setBackgroundColor(ContextCompat.getColor(context, R.color.bgColor1))
                } else {
                    container.setBackgroundColor(ContextCompat.getColor(context, R.color.bgColor2))
                }

                container.setOnClickListener {
                    callback(data)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        var viewType = if (itemsFiltered[position] == null) {
            VIEW_TYPE_LOADING
        } else {
            VIEW_TYPE_ITEM
        }

        return viewType
    }
}