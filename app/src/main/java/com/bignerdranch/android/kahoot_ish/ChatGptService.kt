package com.bignerdranch.android.kahoot_ish

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ChatGptService {

    @POST("v1/chat/completions")
    fun getChatResponse(@Body body: ChatGptRequest): Call<ChatGptResponse>
}
