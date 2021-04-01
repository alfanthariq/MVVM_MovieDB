package com.alfanthariq.mvvm_moviedb.di

import android.content.Context
import androidx.room.Room.databaseBuilder
import com.alfanthariq.mvvm_moviedb.data.AppDatabase
import com.alfanthariq.mvvm_moviedb.data.entity.MovieDAO
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule(val context : Context) {
    val dbName = "aplikasi.db"

    @Provides
    @Singleton
    fun provideDatabase() : AppDatabase {
        return databaseBuilder(context, AppDatabase::class.java, dbName)
            .fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideMoviesItemDao(db : AppDatabase) : MovieDAO {
        return db.moviesItemDao()
    }
}