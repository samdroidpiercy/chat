package com.example.muzz.di

import android.content.Context
import com.example.muzz.data.MessageDao
import com.example.muzz.data.MessageDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MessageDatabase {
        return MessageDatabase.getDatabase(context)
    }

    @Provides
    fun provideMessageDao(database: MessageDatabase): MessageDao {
        return database.messageDao()
    }
}