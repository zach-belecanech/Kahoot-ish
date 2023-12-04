package com.bignerdranch.android.kahoot_ish

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatGptService {
    @POST("v1/engines/gpt-3.5-turbo/completions")
    fun getAnswers(@Body body: ChatGptRequest): Call<ChatGptResponse>
}