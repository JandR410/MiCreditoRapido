package com.mibanco.creditorapido.domain.usecase

import com.mibanco.creditorapido.domain.model.ClientCreditLine
import com.mibanco.creditorapido.domain.repository.CreditRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class GetClientCreditLineUseCaseTest {

    @Mock
    private lateinit var mockCreditRepository: CreditRepository

    private lateinit var getClientCreditLineUseCase: GetClientCreditLineUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        getClientCreditLineUseCase = GetClientCreditLineUseCase(mockCreditRepository)
    }

    @Test
    fun `invoke should return success when repository returns client credit line successfully`() = runTest {
        val clientId = "12345"
        val expectedCreditLine = ClientCreditLine(
            clientId = clientId,
            clientName = "Juan Perez",
            preApprovedAmount = 5000.0,
            interestRate = 0.05,
            minTerm = 6,
            maxTerm = 24
        )

        whenever(mockCreditRepository.getClientCreditLine(any()))
            .thenReturn(Result.success(expectedCreditLine))

        val result = getClientCreditLineUseCase.invoke(clientId)

        assert(result.isSuccess)
        assert(result.getOrNull() == expectedCreditLine)
        verify(mockCreditRepository).getClientCreditLine(clientId)
    }

    @Test
    fun `invoke should return failure when repository getClientCreditLine fails`() = runTest {
        val clientId = "invalid_id"
        val expectedException = Exception("Client not found or network error")

        whenever(mockCreditRepository.getClientCreditLine(any()))
            .thenReturn(Result.failure(expectedException))

        val result = getClientCreditLineUseCase.invoke(clientId)
        assert(result.isFailure)
        assert(result.exceptionOrNull() == expectedException)
        verify(mockCreditRepository).getClientCreditLine(clientId)
    }
}