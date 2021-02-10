package com.bignerdranch.android.geoquiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bignerdranch.android.geoquiz.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewBinding()
        setupClickListener()
        updateQuestion()
    }

    private fun setupClickListener() {
        binding.trueButton.setOnClickListener { view: View ->
            checkAnswer(true)
        }
        binding.falseButton.setOnClickListener { view: View ->
            checkAnswer(false)
        }
        binding.nextButton.setOnClickListener {
            currentIndex = (currentIndex + 1) % QuestionsBank.getBankSize()
            updateQuestion()
        }
        binding.prevButton.setOnClickListener {
            if (currentIndex - 1 < 0) {
                currentIndex = QuestionsBank.getBankSize() - 1
            } else {
                currentIndex = (currentIndex - 1) % QuestionsBank.getBankSize()
            }
            updateQuestion()
        }
        binding.questionTextView.setOnClickListener { view: View ->
            currentIndex = (currentIndex + 1) % QuestionsBank.getBankSize()
            updateQuestion()
        }
    }

    private fun setupViewBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun updateQuestion() {
        val questionTextResId = QuestionsBank.getQuestionText(currentIndex)
        binding.questionTextView.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = QuestionsBank.getQuestionAnswer(currentIndex)
        val messageResId = if (userAnswer == correctAnswer) {
            R.string.correct_toast
        } else {
            R.string.incorrect_toast
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
                .show()
    }
}
