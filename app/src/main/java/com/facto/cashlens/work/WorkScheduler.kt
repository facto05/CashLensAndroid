package com.facto.cashlens.work

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.facto.cashlens.worker.RecurringWorker
import com.facto.cashlens.worker.SyncWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkScheduler @Inject constructor(private val workManager: WorkManager) {

    fun scheduleSync(intervalMinutes: Long = 15) {
        val request = PeriodicWorkRequestBuilder<SyncWorker>(intervalMinutes, TimeUnit.MINUTES)
            .build()
        workManager.enqueueUniquePeriodicWork(
            "sync", ExistingPeriodicWorkPolicy.UPDATE, request
        )
    }

    fun scheduleRecurring(intervalHours: Long = 6) {
        val request = PeriodicWorkRequestBuilder<RecurringWorker>(intervalHours, TimeUnit.HOURS)
            .build()
        workManager.enqueueUniquePeriodicWork(
            "recurring", ExistingPeriodicWorkPolicy.UPDATE, request
        )
    }

    fun scheduleReminder(intervalHours: Long = 24) {
        val request = PeriodicWorkRequestBuilder<com.facto.cashlens.worker.ReminderWorker>(intervalHours, TimeUnit.HOURS)
            .build()
        workManager.enqueueUniquePeriodicWork(
            "reminder", ExistingPeriodicWorkPolicy.UPDATE, request
        )
    }
}
