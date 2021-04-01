package com.alfanthariq.mvvm_moviedb.features.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.alfanthariq.mvvm_moviedb.R
import com.alfanthariq.mvvm_moviedb.base.ApiService
import com.alfanthariq.mvvm_moviedb.base.BaseViewModel
import com.alfanthariq.mvvm_moviedb.features.model.Reviews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

class ReviewsViewModel : BaseViewModel() {
    @Inject
    lateinit var service: ApiService
    @Inject
    lateinit var apps: Application

    val reviews = MutableLiveData<Reviews>()
    val errMsg = MutableLiveData<String>()

    fun getReviews(id: String, page : String) {
        CoroutineScope(Dispatchers.IO).launch {
            val request = service.getReviews(id, apps.getString(R.string.api_key), page)
            withContext(Dispatchers.Main) {
                try {
                    val response = request.await()

                    if (response != null) {
                        reviews.postValue(response)
                    } else {
                        errMsg.postValue("Failed to get reviews")
                    }
                } catch (e: HttpException) {
                    println(e.message())
                    errMsg.postValue("Failed to get reviews (${e.message()})")
                } catch (e: Throwable) {
                    println(e.message)
                    errMsg.postValue("Failed to get reviews (${e.message})")
                }
            }
        }
    }
}