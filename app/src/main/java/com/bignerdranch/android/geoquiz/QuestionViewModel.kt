package com.bignerdranch.android.geoquiz

import androidx.lifecycle.ViewModel

class QuizViewModel : ViewModel() {

    var currentIndex = 0
    var score = 0
    var answersCounter = 0
    var answers: Map<Int, Boolean> =
        mapOf(0 to false, 1 to false, 2 to false, 3 to false, 4 to false, 5 to false)
    var isCheater = false
}
