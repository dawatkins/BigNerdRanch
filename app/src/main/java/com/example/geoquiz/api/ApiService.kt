package com.example.geoquiz.api

import retrofit2.Call
import retrofit2.http.GET
import com.example.geoquiz.api.model.QuestionList

interface ApiService {
    @GET("/api.php?amount=10&category=28&type=boolean")
    fun getQuestions(): Call<QuestionList>
}