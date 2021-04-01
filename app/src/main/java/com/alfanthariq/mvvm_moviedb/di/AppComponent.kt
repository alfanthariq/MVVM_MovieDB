package com.alfanthariq.mvvm_moviedb.di

import com.alfanthariq.mvvm_moviedb.features.viewmodel.DetailMovieViewModel
import com.alfanthariq.mvvm_moviedb.features.viewmodel.MainViewModel
import com.alfanthariq.mvvm_moviedb.features.viewmodel.ReviewsViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, DatabaseModule::class])
interface AppComponent {
    fun inject(viewmodel: MainViewModel)
    fun inject(viewmodel: DetailMovieViewModel)
    fun inject(viewmodel: ReviewsViewModel)
}