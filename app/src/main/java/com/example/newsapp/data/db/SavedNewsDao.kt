package com.example.newsapp.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.example.newsapp.data.model.Article
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedNewsDao {
    @Query("SELECT * FROM news")
    fun getSavedNews(): Flow<List<Article>>

    @Insert(onConflict = REPLACE)
    suspend fun saveNews(article: Article): Long

    @Delete
    suspend fun deleteSavedNews(article: Article): Int
}