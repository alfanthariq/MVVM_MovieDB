package com.alfanthariq.mvvm_moviedb.features.model

import com.google.gson.annotations.SerializedName

data class Trailers(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("results")
	val results: List<TrailersItem?>? = null
)