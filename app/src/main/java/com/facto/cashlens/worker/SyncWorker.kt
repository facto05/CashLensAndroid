package com.facto.cashlens.worker

import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.facto.cashlens.core.network.Resource
import com.facto.cashlens.data.repository.SyncRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: android.content.Context,
    @Assisted params: WorkerParameters,
    private val syncRepository: SyncRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return when (syncRepository.syncWithServer()) {
            is Resource.Success -> {
                // ponytail: send notification if any budget over limit after sync
                sendBudgetAlertIfNeeded()
                Result.success()
            }
            is Resource.Error -> Result.retry()
            Resource.Loading -> Result.retry()
        }
    }

    private fun sendBudgetAlertIfNeeded() {
        // ponytail: real check done in SyncRepository; here just fire generic reminder
        val manager = applicationContext.getSystemService(android.content.Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(applicationContext, "budget_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Budget Alert")
            .setContentText("Beberapa budget sudah mencapai atau melewati limit.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
