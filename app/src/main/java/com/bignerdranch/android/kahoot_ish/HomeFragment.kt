package com.bignerdranch.android.kahoot_ish

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bignerdranch.android.kahoot_ish.databinding.HomePageBinding

class HomeFragment: Fragment() {

        private var _binding: HomePageBinding? = null

        // This property is only valid between onCreateView and
        // onDestroyView.
        private val binding get() = _binding!!

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {

            _binding = HomePageBinding.inflate(inflater, container, false)
            return binding.root

        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            binding.playerButton.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_FirstFragment)
            }

            binding.hostButton.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_authenticationFragment)
            }

        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
}