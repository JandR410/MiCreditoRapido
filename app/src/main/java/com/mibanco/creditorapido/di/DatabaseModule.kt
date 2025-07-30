package com.mibanco.creditorapido.di

import android.content.Context
import androidx.room.Room
import com.mibanco.creditorapido.data.local.dao.PendingLoanRequestDao
import com.mibanco.creditorapido.data.local.database.AppDatabase
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "credit_rapido_database"
        )
            .build()
    }

    @Provides
    @Singleton
    fun providePendingLoanRequestDao(appDatabase: AppDatabase): PendingLoanRequestDao {
        return appDatabase.pendingLoanRequestDao()
    }
}