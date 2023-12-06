package com.bignerdranch.android.kahoot_ish

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bignerdranch.android.kahoot_ish.databinding.LobbyBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LobbyFragment: Fragment() {

        private var _binding: LobbyBinding? = null
        private lateinit var usersAdapter: UsersAdapter
        private val usersList = mutableListOf<User>()
        private var userChangesListener: ValueEventListener? = null
        private var gameStartListener: ValueEventListener? = null

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
            _binding?.usersRecyclerView?.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = usersAdapter
            }
            binding.startGameButton.setOnClickListener {
                val database = FirebaseDatabase.getInstance()
                val gameStarted = database.getReference("gameStarted")
                gameStarted.setValue(true)
            }
            listenForUserChanges()
            listenForGameStart()
        }

        override fun onDestroyView() {
            super.onDestroyView()
            val database = FirebaseDatabase.getInstance()
            database.getReference("users").removeEventListener(userChangesListener!!)
            database.getReference("gameStarted").removeEventListener(gameStartListener!!)
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
            if (arguments?.getString("userRole") == "host") {
                userRef.removeValue()
                gameStarted.setValue(false)
            }
        }

        private fun listenForUserChanges() {
            val databaseReference = FirebaseDatabase.getInstance().getReference("users")
            userChangesListener = (object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        usersList.clear()
                        for (childSnapshot in snapshot.children) {
                            val user = childSnapshot.getValue(User::class.java)
                            user?.let { usersList.add(it) }
                        }
                        usersAdapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
            databaseReference.addValueEventListener(userChangesListener!!)
        }

        private fun listenForGameStart() {
            val databaseReference = FirebaseDatabase.getInstance().getReference("gameStarted")
            gameStartListener = (object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val gameStarted = snapshot.getValue(Boolean::class.java)
                        if (gameStarted == true) {
                            findNavController().navigate(R.id.action_lobbyFragment_to_readyScreenFragment, bundleOf("user_id" to arguments?.getString("user_id"),
                                "questionNum" to 0, "score" to 0))
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
            databaseReference.addValueEventListener(gameStartListener!!)
        }





        data class User(val name: String? = null)

}