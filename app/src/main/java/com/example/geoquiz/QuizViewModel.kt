package com.example.geoquiz

import android.util.Log
import androidx.lifecycle.ViewModel

class QuizViewModel: ViewModel() {

    var currentIndex = 0

    private val questionBank = listOf(
        Question(R.string.question_australia, true, 0),
        Question(R.string.question_oceans, true, 0),
        Question(R.string.question_mideast, false, 0),
        Question(R.string.question_africa, false, 0),
        Question(R.string.question_americas, true, 0),
        Question(R.string.question_asia, true, 0)
    )

    fun correctAnswers(): Double{
        var totalCorrect = 0.0
        for(answers in questionBank){totalCorrect += answers.userAnswer}
        return totalCorrect
    }

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    val currentQuestionUserAnswer: Int
        get() = questionBank[currentIndex].userAnswer

    fun setUserAnswer(answer: Int){
        questionBank[currentIndex].userAnswer(answer)
    }

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun moveToPrevious(){
        currentIndex = (currentIndex - 1) % questionBank.size
    }
    fun size(): Int{
        return questionBank.size
    }
}