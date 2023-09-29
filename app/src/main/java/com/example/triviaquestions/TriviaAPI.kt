package com.example.triviaquestions
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TriviaApi {
    @GET("api.php")
    fun getTriviaQuestions(
        @Query("amount") amount: Int,
        @Query("type") type: String
    ): Call<TriviaResponse>
}