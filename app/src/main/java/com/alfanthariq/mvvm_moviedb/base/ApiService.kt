package com.alfanthariq.mvvm_moviedb.base

import com.alfanthariq.mvvm_moviedb.features.model.*
import kotlinx.coroutines.Deferred
import retrofit2.http.*

interface ApiService {
    @GET("genre/movie/list")
    fun getGenres(@Query("api_key") apikey : String): Deferred<Genres?>

    @GET("discover/movie")
    fun getAllMovie(@Query("api_key") apikey : String,
                    @Query("page") page : String): Deferred<Movies?>

    @GET("discover/movie")
    fun getGenreMovie(@Query("api_key") apikey : String,
                      @Query("with_genres") genres : String,
                      @Query("page") page : String): Deferred<Movies?>

    @GET("movie/{movie_id}")
    fun getDetailMovie(@Path("movie_id") movie_id : String,
                       @Query("api_key") apikey : String): Deferred<MovieDetail?>

    @GET("movie/{movie_id}/videos")
    fun getTrailers(@Path("movie_id") movie_id : String,
                    @Query("api_key") apikey : String): Deferred<Trailers?>

    @GET("movie/{movie_id}/reviews")
    fun getReviews(@Path("movie_id") movie_id : String,
                    @Query("api_key") apikey : String,
                   @Query("page") page : String): Deferred<Reviews?>

}