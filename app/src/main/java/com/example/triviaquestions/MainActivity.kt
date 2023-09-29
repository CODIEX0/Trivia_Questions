package com.example.triviaquestions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.triviaquestions.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var correct_answer: String
    private var currentQuestionIndex = 0
    private var correctAnswersInARow = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnFalse.setOnClickListener(btnFalse)
        binding.btnTrue.setOnClickListener(btnTrue)

        loadNextQuestion()
    }

    private fun loadNextQuestion() {
        binding.txtQuestionNumber.text = "Question ${currentQuestionIndex + 1}"

        val retrofit = Retrofit.Builder()
            .baseUrl("https://opentdb.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val triviaApiService = retrofit.create(TriviaApi::class.java)

        val call = triviaApiService.getTriviaQuestions(10, "boolean")

        call.enqueue(object : Callback<TriviaResponse> {
            override fun onResponse(call: Call<TriviaResponse>, response: Response<TriviaResponse>) {
                if (response.isSuccessful) {
                    val triviaResponse = response.body()
                    val questions = triviaResponse?.results

                    if (questions != null && currentQuestionIndex < questions.size) {
                        val question = questions[currentQuestionIndex]
                        val questionText = question.question
                        correct_answer = question.correct_answer

                        binding.txtQuestion.text = questionText
                    } else {
                        binding.txtQuestionNumber.text = "Game Over!"
                        binding.txtQuestion.text = "Correct Answers in a Row: $correctAnswersInARow"
                    }
                } else {
                    Toast.makeText(applicationContext, "Failed to fetch trivia questions.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TriviaResponse>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(applicationContext, "Failed to fetch trivia questions.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private val btnTrue = View.OnClickListener {
        if (binding.txtQuestionNumber.text.toString() != "Game Over!") {
            checkAnswer("True")
        }
    }


    private val btnFalse = View.OnClickListener {
        if (binding.txtQuestionNumber.text.toString() != "Game Over!") {
            checkAnswer("False")
        }
    }

    private fun checkAnswer(selectedAnswer: String) {
        if (selectedAnswer == correct_answer) {
            binding.root.setBackgroundColor(ContextCompat.getColor(this, R.color.light_green))
            correctAnswersInARow++
            currentQuestionIndex++
            loadNextQuestion()
        } else {
            binding.root.setBackgroundColor(ContextCompat.getColor(this, R.color.light_red))
            //tell the user that the game is over
            binding.txtQuestionNumber.text = "Game Over!"
            //show the winning streak
            binding.txtQuestion.text = "Correct Answers Streak: $correctAnswersInARow"
            //disable the buttons
            binding.btnFalse.isEnabled = false
            binding.btnTrue.isEnabled = false
        }
    }
}
