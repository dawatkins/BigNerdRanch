package com.example.geoquiz

import androidx.annotation.StringRes

data class Question(@StringRes val textResId: Int, val answer: Boolean, var userAnswer: Int) {

    fun userAnswer(userAnswer: Int) = apply{this.userAnswer = userAnswer}

}