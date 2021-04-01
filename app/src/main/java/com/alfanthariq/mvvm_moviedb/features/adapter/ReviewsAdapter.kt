package com.alfanthariq.mvvm_moviedb.features.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.alfanthariq.mvvm_moviedb.R
import com.alfanthariq.mvvm_moviedb.databinding.ItemLoadingBinding
import com.alfanthariq.mvvm_moviedb.databinding.ItemReviewBinding
import com.alfanthariq.mvvm_moviedb.features.model.ReviewsItem
import com.alfanthariq.mvvm_moviedb.utils.DateOperationUtil
import com.bumptech.glide.Glide

class ReviewsAdapter (val items : ArrayList<ReviewsItem?>,
                      val context : Context,
                      val callback : (ReviewsItem) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_LOADING = 1
    private val VIEW_TYPE_ITEM = 0

    fun addLoading() {
        val lastIdx = items.lastIndex
        if (items[lastIdx] != null) {
            items.add(null)
            notifyItemInserted(lastIdx+1)
        }
    }

    fun removeLoading() {
        if (items.isNotEmpty()) {
            val lastIdx = items.lastIndex
            if (items[lastIdx] == null) {
                items.removeAt(lastIdx)
                notifyItemRemoved(lastIdx)
            }
        }
    }

    class ReviewViewHolder(val binding : ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            inline fun createVH(
                parent: ViewGroup,
                crossinline block: (inflater: LayoutInflater, container: ViewGroup, attach: Boolean) -> ViewBinding
            ) = ReviewViewHolder(
                block(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
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
            ReviewViewHolder.createVH(parent, ItemReviewBinding::inflate)
        } else {
            println("Loading Holder")
            LoadingHolder.createVH(parent, ItemLoadingBinding::inflate)
        }
        return vh
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ReviewViewHolder) {
            (holder.binding as ItemReviewBinding).apply {
                val data = items[position]
                txtAuthor.text = "Reviewed by ${data?.author}"
                txtRating.text = "${data?.authorDetails?.rating.toString()}/10"
                txtContent.text = data?.content?.trim()
                txtTgl.text = DateOperationUtil.dateStrFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "dd/MM/yyyy HH:mm:ss", data?.createdAt!!)

                Glide.with(context)
                    .load("http://image.tmdb.org/t/p/w92${data.authorDetails?.avatarPath}")
                    .placeholder(R.drawable.user)
                    .into(imgProfile)

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
        var viewType = if (items[position] == null) {
            VIEW_TYPE_LOADING
        } else {
            VIEW_TYPE_ITEM
        }

        return viewType
    }
}