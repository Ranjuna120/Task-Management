package com.example.mad3

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import com.example.mad3.databinding.ActivityTimerBinding

class TimerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTimerBinding
    private var isRunning = false
    private var startTime: Long = 0L
    private var elapsedTime: Long = 0L
    private val handler = Handler(Looper.getMainLooper())

    private val updateTimeRunnable: Runnable = object : Runnable {
        override fun run() {
            val timeNow = SystemClock.uptimeMillis() - startTime
            val totalTime = elapsedTime + timeNow
            val seconds = (totalTime / 1000).toInt() % 60
            val minutes = (totalTime / 1000 / 60).toInt() % 60
            val hours = (totalTime / 1000 / 60 / 60).toInt()

            binding.timerTextView.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)

            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Start Button Logic
        binding.btnStart.setOnClickListener {
            if (!isRunning) {
                startTime = SystemClock.uptimeMillis()
                handler.post(updateTimeRunnable)
                isRunning = true
            }
        }

        // Pause Button Logic
        binding.btnPause.setOnClickListener {
            if (isRunning) {
                elapsedTime += SystemClock.uptimeMillis() - startTime
                handler.removeCallbacks(updateTimeRunnable)
                isRunning = false
            }
        }

        // Reset Button Logic
        binding.btnReset.setOnClickListener {
            startTime = 0L
            elapsedTime = 0L
            binding.timerTextView.text = "00:00:00"
            handler.removeCallbacks(updateTimeRunnable)
            isRunning = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTimeRunnable)
    }
}
