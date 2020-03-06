package com.example.geoquiz

import com.example.geoquiz.api.model.ApiQuestion
import com.example.geoquiz.api.model.checkAnswer
import io.kotlintest.shouldBe
import io.kotlintest.specs.DescribeSpec

class CheckAnswerTest: DescribeSpec() {

    val question = ApiQuestion("Vehicles", "boolean", "easy", "Do manuals have a clutch?", true, listOf(false))

    init {
        describe("Test that the check answer function is working") {
            context("user is correct"){
                it("should return true"){
                    question.checkAnswer(true) shouldBe true
                }
            }

            context("user is incorrect"){
                it("should return false"){
                    question.checkAnswer(false) shouldBe false
                }
            }
        }
    }
}