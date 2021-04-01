package com.alfanthariq.mvvm_moviedb.features.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.alfanthariq.mvvm_moviedb.R
import com.alfanthariq.mvvm_moviedb.databinding.ItemReviewSmallBinding
import com.alfanthariq.mvvm_moviedb.features.model.ReviewsItem
import com.bumptech.glide.Glide

class ReviewsSmallAdapter (val items : ArrayList<ReviewsItem?>,
                           val context : Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class RevViewHolder(val binding : ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            inline fun createVH(
                parent: ViewGroup,
                crossinline block: (inflater: LayoutInflater, container: ViewGroup, attach: Boolean) -> ViewBinding
            ) = RevViewHolder(
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
        val vh = RevViewHolder.createVH(parent, ItemReviewSmallBinding::inflate)
        return vh
    }

    override fun getItemCount(): Int {
        return if (items.size > 5) 5 else items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RevViewHolder) {
            (holder.binding as ItemReviewSmallBinding).apply {
                val data = items[position]
                txtAuthor.text = "Reviewed by ${data?.author}"
                txtRating.text = "${data?.authorDetails?.rating.toString()}/10"
                txtContent.text = data?.content

                Glide.with(context)
                    .load("http://image.tmdb.org/t/p/w92${data?.authorDetails?.avatarPath}")
                    .placeholder(R.drawable.user)
                    .into(imgProfile)
            }
        }
    }
}