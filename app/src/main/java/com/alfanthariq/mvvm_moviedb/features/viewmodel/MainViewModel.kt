package com.alfanthariq.mvvm_moviedb.features.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.alfanthariq.mvvm_moviedb.R
import com.alfanthariq.mvvm_moviedb.base.ApiService
import com.alfanthariq.mvvm_moviedb.base.BaseViewModel
import com.alfanthariq.mvvm_moviedb.features.model.Genres
import com.alfanthariq.mvvm_moviedb.features.model.Movies
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

class MainViewModel : BaseViewModel() {
    @Inject
    lateinit var service: ApiService
    @Inject
    lateinit var apps : Application

    val movieItems = MutableLiveData<Movies>()
    val genres = MutableLiveData<Genres>()
    val errorMsg = MutableLiveData<String>()

    fun getMovieItems(genres: String, page : String) {
        CoroutineScope(Dispatchers.IO).launch {
            val request = if (genres.isEmpty())
                service.getAllMovie(apps.getString(R.string.api_key), page)
            else
                service.getGenreMovie(apps.getString(R.string.api_key), genres, page)
            withContext(Dispatchers.Main) {
                try {
                    val response = request.await()

                    if (response != null) {
                        movieItems.postValue(response)
                    } else {
                        errorMsg.postValue("Failed to get movies")
                    }
                } catch (e: HttpException) {
                    println(e.message())
                    errorMsg.postValue("Failed to get movies (${e.message()})")
                } catch (e: Throwable) {
                    println(e.message)
                    errorMsg.postValue("Failed to get movies (${e.message})")
                }
            }
        }
    }

    fun getGenres(){
        CoroutineScope(Dispatchers.IO).launch {
            val request = service.getGenres(apps.getString(R.string.api_key))
            withContext(Dispatchers.Main) {
                try {
                    val response = request.await()

                    if (response != null) {
                        genres.postValue(response)
                    } else {
                        errorMsg.postValue("Failed to get genres")
                    }
                } catch (e: HttpException) {
                    println(e.message())
                    errorMsg.postValue("Failed to get genres (${e.message()})")
                } catch (e: Throwable) {
                    println(e.message)
                    errorMsg.postValue("Failed to get genres (${e.message})")
                }
            }
        }
    }
}