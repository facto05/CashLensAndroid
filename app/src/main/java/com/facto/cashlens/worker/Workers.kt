package com.facto.cashlens.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import javax.inject.Inject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    @Inject lateinit var syncRepository: com.facto.cashlens.data.repository.AuthRepository

    override suspend fun doWork(): Result {
        // ponytail: real sync (POST /sync + pull updated_after) added when backend ready
        return Result.success()
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
