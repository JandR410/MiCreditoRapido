package com.mibanco.creditorapido.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mibanco.creditorapido.data.local.dao.PendingLoanRequestDao
import com.mibanco.creditorapido.data.local.entity.PendingLoanRequestEntity

@Database(entities = [PendingLoanRequestEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pendingLoanRequestDao(): PendingLoanRequestDao
}

