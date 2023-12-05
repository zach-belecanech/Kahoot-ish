package com.bignerdranch.android.kahoot_ish

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bignerdranch.android.kahoot_ish.databinding.LobbyBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LobbyFragment: Fragment() {

        private var _binding: LobbyBinding? = null
        private lateinit var usersAdapter: UsersAdapter
        private val usersList = mutableListOf<String>()

        // This property is only valid between onCreateView and
        // onDestroyView.
        private val binding get() = _binding!!

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
            usersAdapter = UsersAdapter(usersList)
            _binding?.usersRecyclerView?.adapter = usersAdapter

            listenForUserChanges()
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }

        override fun onDestroy() {
            updateFirebaseVariable()
            super.onDestroy()
        }

        private fun updateFirebaseVariable() {
            val database = FirebaseDatabase.getInstance()
            val gameStarted = database.getReference("gameStarted")
            val userRef = database.getReference("users")
            userRef.removeValue()
            gameStarted.setValue(false)
        }

        private fun listenForUserChanges() {
            val databaseReference = FirebaseDatabase.getInstance().getReference("users")
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        usersList.clear()
                        snapshot.children.forEach {
                            val userName = it.getValue(String::class.java)
                            if (userName != null) {
                                usersList.add(userName)
                            }
                        usersAdapter.notifyDataSetChanged()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        private fun setupRecyclerView(userNames: List<String>) {
            val adapter = UsersAdapter(userNames)
            _binding?.usersRecyclerView?.adapter = adapter
        }

}