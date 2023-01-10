package com.example.newsapp.di

import com.example.newsapp.data.repositories.NewsRepositoryImpl
import com.example.newsapp.domain.repositories.NewsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindNewsRepository(repo: NewsRepositoryImpl): NewsRepository
}