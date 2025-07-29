package com.example.news.di

import android.app.Application
import com.example.news.data.manager.LocalUserManagerImpl
import com.example.news.domain.manager.LocalUserManager
import com.example.news.domain.usecases.AppEntryUseCases
import com.example.news.domain.usecases.ReadAppEntry
import com.example.news.domain.usecases.SaveAppEntry
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLocalUserManager(application: Application): LocalUserManager =
        LocalUserManagerImpl(application)

    @Provides
    @Singleton
    fun provideAppEntryUseCases(localUserManager: LocalUserManager) = AppEntryUseCases(
        readAppEntry = ReadAppEntry(localUserManager),
        saveAppEntry = SaveAppEntry(localUserManager)
    )

}