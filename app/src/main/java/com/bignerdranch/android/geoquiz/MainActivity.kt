package com.bignerdranch.android.geoquiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProviders
import com.bignerdranch.android.geoquiz.databinding.ActivityMainBinding

private const val KEY_INDEX = "index"
private const val KEY_SCORE = "score"
private const val KEY_TOTALANSWERS = "total_answers"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewBinding()
        getSavedInstanceState(savedInstanceState)
        setupClickListener()
        checkQuestion()
        updateQuestion()
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
        savedInstanceState.putInt(KEY_SCORE, quizViewModel.score)
        savedInstanceState.putInt(KEY_TOTALANSWERS, quizViewModel.answersCounter)
    }

    private fun setupViewBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun getSavedInstanceState(savedInstanceState: Bundle?) {
        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex
        val score = savedInstanceState?.getInt(KEY_SCORE, 0) ?: 0
        quizViewModel.score = score
        val totalAnswers = savedInstanceState?.getInt(KEY_TOTALANSWERS, 0) ?: 0
        quizViewModel.answersCounter = totalAnswers
    }

    private fun setupClickListener() {
        binding.trueButton.setOnClickListener { view: View ->
            setAnswer()
            checkAnswer(true)
            checkResult()
        }
        binding.falseButton.setOnClickListener { view: View ->
            setAnswer()
            checkAnswer(false)
            checkResult()
        }
        binding.nextButton.setOnClickListener {
            moveToNext()
            checkQuestion()
            updateQuestion()
        }
        binding.prevButton.setOnClickListener {
            moveToPrev()
            checkQuestion()
            updateQuestion()
        }
        binding.questionTextView.setOnClickListener { view: View ->
            moveToNext()
            checkQuestion()
            updateQuestion()
        }
    }

    private fun checkQuestion() {
        if (quizViewModel.answers[quizViewModel.currentIndex]) {
            disableButtons()
        } else {
            enableButtons()
        }
    }

    private fun updateQuestion() {
        val questionTextResId = QuestionsBank.getQuestionText(quizViewModel.currentIndex)
        binding.questionTextView.setText(questionTextResId)
    }

    private fun setAnswer() {
        quizViewModel.answers[quizViewModel.currentIndex] = true
        quizViewModel.answersCounter++
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = QuestionsBank.getQuestionAnswer(quizViewModel.currentIndex)
        val messageResId: String
        if (userAnswer == correctAnswer) {
            quizViewModel.score++
            messageResId = "Correct!"
        } else {
            messageResId = "Incorrect!"
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
        disableButtons()
    }

    private fun checkResult() {
        if (quizViewModel.answersCounter == QuestionsBank.getBankSize()) {
            val result = ((quizViewModel.score.toDouble() / quizViewModel.answersCounter) * 100)
            var resultMessage = "Your punctuation is $result %"
            Toast.makeText(this, resultMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun moveToPrev() {
        quizViewModel.currentIndex =
            if (quizViewModel.currentIndex - 1 < 0) {
                QuestionsBank.getBankSize() - 1
            } else {
                quizViewModel.currentIndex - 1
            }
    }

    private fun moveToNext() {
        quizViewModel.currentIndex = (quizViewModel.currentIndex + 1) % QuestionsBank.getBankSize()
    }

    private fun disableButtons() {
        binding.trueButton.isClickable = false
        binding.trueButton.alpha = 0.5f
        binding.falseButton.isClickable = false
        binding.falseButton.alpha = 0.5f
    }

    private fun enableButtons() {
        binding.trueButton.isClickable = true
        binding.trueButton.alpha = 1f
        binding.falseButton.isClickable = true
        binding.falseButton.alpha = 1f
    }
}

