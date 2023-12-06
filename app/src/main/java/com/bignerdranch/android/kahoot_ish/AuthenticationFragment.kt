package com.bignerdranch.android.kahoot_ish

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bignerdranch.android.kahoot_ish.databinding.AuthentificationBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AuthenticationFragment: Fragment() {

        private var _binding: AuthentificationBinding? = null

        // This property is only valid between onCreateView and
        // onDestroyView.
        private val binding get() = _binding!!

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {

            _binding = AuthentificationBinding.inflate(inflater, container, false)
            return binding.root

        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            binding.enterPasswordButton.setOnClickListener {
                val passwordEntered = binding.passwordTextBox.text.toString()
                val database = FirebaseDatabase.getInstance()
                val myRef = database.getReference("password")
                val gameStarted = database.getReference("gameStarted")
                gameStarted.setValue(true)
                myRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        val password = dataSnapshot.getValue(String::class.java).toString()
                        if (passwordEntered == password) {
                            findNavController().navigate(R.id.action_authenticationFragment_to_lobbyFragment, bundleOf("userRole" to "host", "user_id" to "host"))
                        } else {
                            binding.passwordTextView.text = "Incorrect Password, try again."
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Failed to read value
                        Log.w("Failed to read value.", error.toException())
                    }
                })
            }

        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
}