package com.example.newsapp.di

import android.app.Application
import androidx.room.Room
import com.example.newsapp.BuildConfig
import com.example.newsapp.data.api.APIService
import com.example.newsapp.data.db.NewsDatabase
import com.example.newsapp.data.db.SavedNewsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideNewsAPIService(retrofit: Retrofit): APIService =
        retrofit.create(APIService::class.java)

    @Provides
    @Singleton
    fun provideDatabase(app: Application): NewsDatabase =
        Room.databaseBuilder(
            app.applicationContext,
            NewsDatabase::class.java,
            "news_database"
        ).build()

    @Provides
    @Singleton
    fun provideSavedNewsDao(db: NewsDatabase): SavedNewsDao =
        db.savedNewsDao()
}