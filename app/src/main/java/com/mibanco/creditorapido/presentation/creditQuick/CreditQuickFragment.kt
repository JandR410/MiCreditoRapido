package com.mibanco.creditorapido.presentation.creditQuick

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mibanco.creditorapido.R
import com.mibanco.creditorapido.presentation.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@AndroidEntryPoint
class CreditQuickFragment : Fragment() {

    private val viewModel: CreditQuickViewModel by viewModels()
    private val args: CreditQuickFragmentArgs by navArgs()

    private lateinit var tvClientName: TextView
    private lateinit var tvCreditLineAmount: TextView
    private lateinit var tvInterestRate: TextView
    private lateinit var sbAmount: SeekBar
    private lateinit var tvSelectedAmount: TextView
    private lateinit var sbTerm: SeekBar
    private lateinit var tvSelectedTerm: TextView
    private lateinit var tvMonthlyPayment: TextView
    private lateinit var btnRequestLoan: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvErrorMessage: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_credit_quick, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI(view)
        setupObservers()
        setupListeners()

        viewModel.fetchClientCreditLine(args.clientId)
    }

    private fun initUI(view: View) {
        tvClientName = view.findViewById(R.id.tvClientName)
        tvCreditLineAmount = view.findViewById(R.id.tvCreditLineAmount)
        tvInterestRate = view.findViewById(R.id.tvInterestRate)
        sbAmount = view.findViewById(R.id.sbAmount)
        tvSelectedAmount = view.findViewById(R.id.tvSelectedAmount)
        sbTerm = view.findViewById(R.id.sbTerm)
        tvSelectedTerm = view.findViewById(R.id.tvSelectedTerm)
        tvMonthlyPayment = view.findViewById(R.id.tvMonthlyPayment)
        btnRequestLoan = view.findViewById(R.id.btnRequestLoan)
        progressBar = view.findViewById(R.id.progressBar)
        tvErrorMessage = view.findViewById(R.id.tvErrorMessage)
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.clientCreditLine.collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        tvErrorMessage.visibility = View.GONE
                        btnRequestLoan.isEnabled = false
                    }
                    is Resource.Success -> {
                        progressBar.visibility = View.GONE
                        tvErrorMessage.visibility = View.GONE
                        resource.data?.let {
                            tvClientName.text = "Cliente: ${it.clientName}"
                            tvCreditLineAmount.text = "Línea Preaprobada: S/ ${String.format("%.2f", it.preApprovedAmount)}"
                            tvInterestRate.text = "Tasa de Interés: ${String.format("%.2f", it.interestRate * 100)}%"

                            sbAmount.max = it.preApprovedAmount.roundToInt()
                            sbAmount.progress = it.preApprovedAmount.roundToInt()
                            tvSelectedAmount.text = "Monto: S/ ${String.format("%.2f", it.preApprovedAmount)}"

                            sbTerm.max = it.maxTerm - it.minTerm
                            sbTerm.progress = 0
                            tvSelectedTerm.text = "Plazo: ${it.minTerm} meses"
                            btnRequestLoan.isEnabled = true

                            viewModel.simulateLoan(it.preApprovedAmount, it.minTerm)
                        }
                    }
                    is Resource.Error -> {
                        progressBar.visibility = View.GONE
                        tvErrorMessage.visibility = View.VISIBLE
                        tvErrorMessage.text = resource.message
                        btnRequestLoan.isEnabled = false
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG).show()
                    }
                    is Resource.Empty -> {
                    }
                }
            }
        }

        viewModel.loanSimulation.observe(viewLifecycleOwner) { simulation ->
            tvSelectedAmount.text = "Monto: S/ ${String.format("%.2f", simulation.amount)}"
            tvSelectedTerm.text = "Plazo: ${simulation.term} meses"
            tvMonthlyPayment.text = "Cuota Mensual: S/ ${String.format("%.2f", simulation.monthlyPayment)}"
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loanRequestStatus.collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        btnRequestLoan.isEnabled = false
                    }
                    is Resource.Success -> {
                        progressBar.visibility = View.GONE
                        btnRequestLoan.isEnabled = true
                        val message = resource.data?.message ?: "Solicitud procesada."
                        val isSuccess = resource.data?.success ?: false
                        val loanId = resource.data?.loanId

                        val action = CreditQuickFragmentDirections.actionCreditQuickFragmentToLoanStatusFragment(
                            isSuccess, message, loanId
                        )
                        findNavController().navigate(action)
                        viewModel.resetLoanRequestStatus()
                    }
                    is Resource.Error -> {
                        progressBar.visibility = View.GONE
                        btnRequestLoan.isEnabled = true
                        val errorMessage = resource.message ?: "Error desconocido al procesar la solicitud."

                        val action = CreditQuickFragmentDirections.actionCreditQuickFragmentToLoanStatusFragment(
                            false, errorMessage, null
                        )
                        findNavController().navigate(action)
                        viewModel.resetLoanRequestStatus()
                    }
                    is Resource.Empty -> {
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        sbAmount.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val amount = progress.toDouble()
                    val minTerm = viewModel.clientCreditLine.value.data?.minTerm ?: 1
                    val currentTerm = sbTerm.progress + minTerm
                    viewModel.simulateLoan(amount, currentTerm)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        sbTerm.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val minTerm = viewModel.clientCreditLine.value.data?.minTerm ?: 1
                    val term = progress + minTerm
                    val currentAmount = sbAmount.progress.toDouble()
                    viewModel.simulateLoan(currentAmount, term)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        btnRequestLoan.setOnClickListener {
            viewModel.requestLoan()
        }
    }
}