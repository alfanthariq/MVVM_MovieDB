package com.alfanthariq.mvvm_moviedb.features.model

import com.google.gson.annotations.SerializedName

data class Movies(
    @field:SerializedName("page")
	val page: Int? = null,
    @field:SerializedName("total_pages")
	val totalPages: Int? = null,
    @field:SerializedName("results")
	val results: List<MoviesItem>? = null,
    @field:SerializedName("total_results")
	val totalResults: Int? = null
)
