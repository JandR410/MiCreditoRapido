package com.mibanco.creditorapido.presentation.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mibanco.creditorapido.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WelcomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnSearchUser: Button = view.findViewById(R.id.btnSearchUsers)

        btnSearchUser.setOnClickListener {
            val clientId = "Junior"
            val action = WelcomeFragmentDirections.actionWelcomeFragmentToCreditQuickFragment(clientId)
            findNavController().navigate(action)
        }
    }
}