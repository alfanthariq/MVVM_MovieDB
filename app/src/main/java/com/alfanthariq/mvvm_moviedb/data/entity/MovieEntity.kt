package com.alfanthariq.mvvm_moviedb.data.entity

import androidx.room.*
import com.alfanthariq.mvvm_moviedb.data.dao.BaseDao

@Entity(tableName = "MOVIE")
data class MovieEntity (
    @PrimaryKey
    val id: Int,
    val overview: String = "",
    val originalLanguage: String = "",
    val originalTitle: String = "",
    val video: Boolean? = null,
    val title: String = "",
    val genreStr: String = "",
    val posterPath: String = "",
    val backdropPath: String = "",
    val releaseDate: String = "",
    val popularity: String = "",
    val voteAverage: String = "",
    val adult: Boolean? = null,
    val voteCount: Int = 0
)

@Dao
interface MovieDAO : BaseDao<MovieEntity> {
    @Query("SELECT * FROM MOVIE")
    fun getAll(): List<MovieEntity>
}