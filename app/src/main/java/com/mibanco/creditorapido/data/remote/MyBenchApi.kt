package com.mibanco.creditorapido.data.remote

import com.mibanco.creditorapido.data.model.request.LoanRequest
import com.mibanco.creditorapido.data.model.response.ClientCreditLineResponse
import com.mibanco.creditorapido.data.model.response.LoanResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MyBenchApi {

    @GET("client/{clientId}/credit-line")
    suspend fun getClientCreditLine(@Path("clientId") clientId: String): ClientCreditLineResponse

    @POST("loan/request")
    suspend fun sendLoanRequest(@Body request: LoanRequest): LoanResponse
}