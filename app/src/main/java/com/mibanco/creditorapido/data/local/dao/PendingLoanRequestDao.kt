package com.mibanco.creditorapido.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mibanco.creditorapido.data.local.entity.PendingLoanRequestEntity

@Dao
interface PendingLoanRequestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoanRequest(request: PendingLoanRequestEntity)

    @Query("SELECT * FROM pending_loan_requests ORDER BY requestTime ASC")
    suspend fun getAllLoanRequests(): List<PendingLoanRequestEntity>

    @Query("DELETE FROM pending_loan_requests WHERE clientId = :clientId AND requestTime = :requestTime")
    suspend fun deleteLoanRequestByDetails(clientId: String, requestTime: Long)
}