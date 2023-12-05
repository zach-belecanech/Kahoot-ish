package com.bignerdranch.android.kahoot_ish

import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bignerdranch.android.kahoot_ish.databinding.FragmentSecondBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val TAG = "SecondFragment"

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val playerName = arguments?.getString("playerName")
    private val uniqueUserId = UUID.randomUUID().toString()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        //Check if the game has started
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("gameStarted")
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val value = snapshot.getValue(Boolean::class.java)
                        if (value == true) {
                            binding.buttonEnterGame.visibility = View.VISIBLE
                        } else {
                            binding.buttonEnterGame.visibility = View.INVISIBLE
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
        binding.buttonEnterGame.setOnClickListener {
            val q1 = binding.q1TextBox.text.toString()
            val q2 = binding.q2TextBox.text.toString()
            val q1_ans = binding.q1TextBoxAns.text.toString()
            val q2_ans = binding.q2TextBoxAns.text.toString()

            // Check if any of the fields are empty
            if (q1 == "" || q2 == "" || q1_ans == "" || q2_ans == "") {
                Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Generate the prompt for GPT-3
            val prompt1 = "Give three incorrect answers in a comma separated list for these questions: $q1, and $q2. Please separate each answer with a colon."
            askGpt3(prompt1, q1, q2, q1_ans, q2_ans)
            findNavController().navigate(R.id.action_SecondFragment_to_lobbyFragment, bundleOf("userRole" to "player"))

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun askGpt3(message: String, q1: String, q2: String, q1_ans: String, q2_ans: String) {
        val request = ChatGptRequest(
            messages = listOf(Message(role = "user", content = message))
        )
        makeApiCallWithApiKey(request, q1, q2, q1_ans, q2_ans)
    }

    private fun makeApiCallWithApiKey(chatGptRequest: ChatGptRequest, q1: String, q2: String, q1_ans: String, q2_ans: String) {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        fetchApiKey { apiKey ->
            apiKey?.let {
                // Initialize Retrofit with the API key
                val chatGptService = Retrofit.Builder()
                    .baseUrl("https://api.openai.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(OkHttpClient.Builder()
                        .addInterceptor(loggingInterceptor)
                        .addInterceptor { chain ->
                        val newRequest = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer $it")
                            .build()
                        chain.proceed(newRequest)
                    }.build())
                    .build()
                    .create(ChatGptService::class.java)

                // Now make the API call
                chatGptService.getChatResponse(chatGptRequest).enqueue(object : Callback<ChatGptResponse> {
                    override fun onResponse(call: Call<ChatGptResponse>, response: Response<ChatGptResponse>) {
                        if (response.isSuccessful) {
                            val chatResponse = response.body()
                            val answers = chatResponse?.choices?.get(0)?.message?.content
                            val incorrect1 = answers?.split(":")?.get(0)
                            val incorrect2 = answers?.split(":")?.get(1)
                            Log.d(TAG, "The GPT Response is: $incorrect1 and $incorrect2")
                            val database = FirebaseDatabase.getInstance()
                            val myRef = database.getReference("users")
                            val userInfo = mapOf(
                                "name" to playerName,
                                "questions" to listOf(
                                    mapOf("question" to q1, "answer" to q1_ans, "incorrect" to incorrect1),
                                    mapOf("question" to q2, "answer" to q2_ans, "incorrect" to incorrect2)
                                )
                            )
                            myRef.child(uniqueUserId).setValue(userInfo)
                        } else {
                            // Handle API errors
                            // For example, display a Toast with the error message
                            Toast.makeText(requireContext(), "Error: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ChatGptResponse>, t: Throwable) {
                        // Handle network errors
                        // For example, display a Toast with the error message
                        Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } ?: run {
                // Handle the case where API key couldn't be fetched
                Toast.makeText(requireContext(), "Error: API key not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchApiKey(onResult: (String?) -> Unit) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("API_KEY")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val apiKey = snapshot.getValue(String::class.java)
                onResult(apiKey)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                onResult(null)
            }
        })
    }




}

