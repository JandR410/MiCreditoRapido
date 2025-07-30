package com.mibanco.creditorapido.di

import android.content.Context
import com.mibanco.creditorapido.data.repository.CreditRepositoryImpl
import com.mibanco.creditorapido.domain.repository.CreditRepository
import com.mibanco.creditorapido.worker.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBindsModule {

    @Binds
    @Singleton
    abstract fun bindCreditRepository(
        creditRepositoryImpl: CreditRepositoryImpl
    ): CreditRepository
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryProvidesModule {

    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor {
        return NetworkMonitor(context)
    }
}
