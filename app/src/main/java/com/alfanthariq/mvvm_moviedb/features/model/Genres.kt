package com.alfanthariq.mvvm_moviedb.features.model

import com.google.gson.annotations.SerializedName

data class Genres(

	@field:SerializedName("genres")
	val genres: List<GenresItem?>? = null
)