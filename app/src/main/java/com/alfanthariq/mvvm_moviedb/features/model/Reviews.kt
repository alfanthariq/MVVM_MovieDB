package com.alfanthariq.mvvm_moviedb.features.model

import com.google.gson.annotations.SerializedName

data class Reviews(

    @field:SerializedName("id")
	val id: Int? = null,

    @field:SerializedName("page")
	val page: Int? = null,

    @field:SerializedName("total_pages")
	val totalPages: Int? = null,

    @field:SerializedName("results")
	val results: List<ReviewsItem?>? = null,

    @field:SerializedName("total_results")
	val totalResults: Int? = null
)