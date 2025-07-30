package com.mibanco.creditorapido.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mibanco.creditorapido.data.local.entity.PendingLoanRequestEntity

@Dao
interface PendingLoanRequestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendingRequest(request: PendingLoanRequestEntity)

    @Query("SELECT * FROM pending_loan_requests ORDER BY timestamp ASC")
    suspend fun getAllPendingRequests(): List<PendingLoanRequestEntity>

    @Query("DELETE FROM pending_loan_requests WHERE id = :requestId")
    suspend fun deletePendingRequest(requestId: Long)
}