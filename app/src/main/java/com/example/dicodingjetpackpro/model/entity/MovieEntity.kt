package com.example.dicodingjetpackpro.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dicodingjetpackpro.model.response.movie.MovieDetailResponse
import com.example.dicodingjetpackpro.model.response.movie.Result
import com.example.dicodingjetpackpro.utils.getCurrentDate
import java.time.LocalDateTime
import java.util.*

@Entity(tableName = "tbl_movie")
data class MovieEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "poster_path")
    val posterPath: String,
    @ColumnInfo(name = "release_date")
    val releaseDate: String,
    @ColumnInfo(name = "is_bookmark")
    val isBookmark: Boolean = false,
    @ColumnInfo(name = "created_at")
    val createdAt: String
) {
    companion object {
        fun mapToMovieEntity(data: MovieDetailResponse, isBookmark: Boolean) = MovieEntity(
            data.id,
            data.title,
            data.posterPath,
            data.releaseDate,
            isBookmark,
            getCurrentDate()
        )
    }
}