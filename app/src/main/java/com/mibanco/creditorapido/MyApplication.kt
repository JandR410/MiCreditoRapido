package com.mibanco.creditorapido

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.mibanco.creditorapido.worker.LoanRetryWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        setupPeriodicLoanRetryWorker()
    }

    private fun setupPeriodicLoanRetryWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            LoanRetryWorker::class.java,
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag("loan_retry_periodic_work")
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "loan_retry_periodic_unique_work",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
        println("Periodic Loan Retry Worker encolado para ejecutarse cada 15 minutos.")
    }

}