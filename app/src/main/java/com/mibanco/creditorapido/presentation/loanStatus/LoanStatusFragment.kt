package com.mibanco.creditorapido.presentation.loanStatus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mibanco.creditorapido.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoanStatusFragment : Fragment() {

    private val args: LoanStatusFragmentArgs by navArgs()

    private lateinit var ivStatusIcon: ImageView
    private lateinit var tvStatusTitle: TextView
    private lateinit var tvStatusMessage: TextView
    private lateinit var tvLoanId: TextView
    private lateinit var btnGoToHome: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_loan_status, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI(view)
        displayStatus()
        setupListeners()
    }

    private fun initUI(view: View) {
        ivStatusIcon = view.findViewById(R.id.ivStatusIcons)
        tvStatusTitle = view.findViewById(R.id.tvStatusTitles)
        tvStatusMessage = view.findViewById(R.id.tvStatusMessages)
        tvLoanId = view.findViewById(R.id.tvLoanIds)
        btnGoToHome = view.findViewById(R.id.btnGoToHomes)
    }

    private fun displayStatus() {
        if (args.isSuccess) {
            ivStatusIcon.setImageResource(R.drawable.ic_check_circle)
            ivStatusIcon.setColorFilter(requireContext().getColor(R.color.green_500))
            tvStatusTitle.text = "¡Solicitud Exitosa!"
            tvStatusMessage.text = args.message
            args.loanId?.let {
                tvLoanId.text = "ID de Préstamo: $it"
                tvLoanId.visibility = View.VISIBLE
            } ?: run {
                tvLoanId.visibility = View.GONE
            }
        } else {
            if (args.message.contains("No hay conexión a internet") || args.message.contains("guardada para reintentar")) {
                ivStatusIcon.setImageResource(R.drawable.ic_cloud_off)
                ivStatusIcon.setColorFilter(requireContext().getColor(R.color.gray))
                tvStatusTitle.text = "Sin Conexión"
                tvStatusMessage.text = "Tu solicitud se guardó y se enviará automáticamente cuando tengas conexión a internet."
                tvLoanId.visibility = View.GONE
            } else {
                ivStatusIcon.setImageResource(R.drawable.ic_error)
                ivStatusIcon.setColorFilter(requireContext().getColor(R.color.red_500))
                tvStatusTitle.text = "¡Error!"
                tvStatusMessage.text = args.message
                tvLoanId.visibility = View.GONE
            }
        }
    }

    private fun setupListeners() {
        btnGoToHome.setOnClickListener {
            findNavController().navigate(R.id.action_loanStatusFragment_to_welcomeFragment)
        }
    }
}