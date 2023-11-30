package com.bignerdranch.android.kahoot_ish

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bignerdranch.android.kahoot_ish.databinding.LobbyBinding
import com.google.firebase.database.FirebaseDatabase

class LobbyFragment: Fragment() {

        private var _binding: LobbyBinding? = null

        // This property is only valid between onCreateView and
        // onDestroyView.
        private val binding get() = _binding!!
        private val userInfo = arguments?.getString("userRole")
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {

            _binding = LobbyBinding.inflate(inflater, container, false)

            val userRole = arguments?.getString("userRole")
            val database = FirebaseDatabase.getInstance()
            val userRef = database.getReference("users")
            if (userRole == "host") {
                binding.startGameButton.visibility = View.VISIBLE
            } else {
                binding.startGameButton.visibility = View.INVISIBLE
            }
            return binding.root

        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)


        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }

        override fun onDestroy() {
            super.onDestroy()
            val database = FirebaseDatabase.getInstance()
            val userRef = database.getReference("users")
            if (userInfo != null) {
                userRef.child(userInfo).removeValue()
            }
        }
}