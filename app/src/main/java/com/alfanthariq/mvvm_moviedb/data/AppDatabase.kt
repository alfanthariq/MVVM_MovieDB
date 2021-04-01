package com.alfanthariq.mvvm_moviedb.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alfanthariq.mvvm_moviedb.data.entity.MovieDAO
import com.alfanthariq.mvvm_moviedb.data.entity.MovieEntity

@Database(entities = [MovieEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun moviesItemDao(): MovieDAO
}