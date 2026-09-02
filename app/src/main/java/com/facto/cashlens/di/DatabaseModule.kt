package com.facto.cashlens.di

import android.content.Context
import androidx.room.Room
import com.facto.cashlens.data.local.CashLensDatabase
import com.facto.cashlens.data.local.dao.CategoryDao
import com.facto.cashlens.data.local.dao.TransactionDao
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
    fun provideDatabase(@ApplicationContext context: Context): CashLensDatabase =
        Room.databaseBuilder(context, CashLensDatabase::class.java, "cashlens.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideTransactionDao(db: CashLensDatabase): TransactionDao = db.transactionDao()

    @Provides
    fun provideCategoryDao(db: CashLensDatabase): CategoryDao = db.categoryDao()
}
