package com.example.geoquiz.api.model

import com.google.gson.annotations.SerializedName

data class QuestionList (
    @SerializedName("request_code")val requestCode: Int,
    val results: Collection<ApiQuestion>
)