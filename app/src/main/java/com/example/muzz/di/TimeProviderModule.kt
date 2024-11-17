package com.example.muzz.di

import com.example.muzz.util.DefaultTimeProvider
import com.example.muzz.util.TimeProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TimeProviderModule {

    @Provides
    @Singleton
    fun provideTimeProvider(): TimeProvider {
        return DefaultTimeProvider()
    }
}