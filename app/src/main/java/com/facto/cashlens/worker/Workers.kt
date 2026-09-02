package com.facto.cashlens.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.facto.cashlens.core.network.Resource
import com.facto.cashlens.data.repository.SyncRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val syncRepository: SyncRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return when (syncRepository.syncWithServer()) {
            is Resource.Success -> Result.success()
            is Resource.Error -> Result.retry()
            Resource.Loading -> Result.retry()
        }
    }
}

@HiltWorker
class RecurringWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // ponytail: generate due recurring transactions into Room, then enqueue SyncWorker
        return Result.success()
    }
}
