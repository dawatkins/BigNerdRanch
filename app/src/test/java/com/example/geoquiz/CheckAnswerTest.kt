package com.example.geoquiz

import com.example.geoquiz.api.model.ApiQuestion
import com.example.geoquiz.api.model.checkAnswer
import io.kotlintest.shouldBe
import io.kotlintest.specs.DescribeSpec

class CheckAnswerTest: DescribeSpec() {

    private val trueQuestion = ApiQuestion("Vehicles", "boolean", "easy", "Do manuals have a clutch?", true, listOf(false))
    private val falseQuestion = ApiQuestion("Vehicles", "boolean", "easy", "Texting and driving is legal in the US", false, listOf(false))

    init {
        describe("Test that the check answer function is working") {
            context("user is correct"){
                it("should return true"){
                    trueQuestion.checkAnswer(true) shouldBe true
                }
            }

            context("user is incorrect"){
                it("should return false"){
                    trueQuestion.checkAnswer(false) shouldBe false
                }
            }
        }
    }

    init {
        describe("Test for check answer question"){
            context("user is correct") {
                it("should return true"){
                    falseQuestion.checkAnswer(false) shouldBe true
                }
            }

            context("user is incorrect"){
                it("should return false"){
                    falseQuestion.checkAnswer(true) shouldBe false
                }
            }
        }

    }
}