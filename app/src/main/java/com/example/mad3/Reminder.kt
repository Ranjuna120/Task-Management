package com.example.mad3

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import java.util.*

class ReminderActivity : AppCompatActivity() {

    private lateinit var alarmManager: AlarmManager
    private lateinit var timePicker: TimePicker
    private lateinit var reminderInput: EditText
    private lateinit var setReminderButton: Button
    private lateinit var cancelReminderButton: Button
    private lateinit var reminderDetailsTextView: TextView
    private lateinit var pendingIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        // Request notification permission if needed
        requestNotificationPermission()

        // Initialize UI elements
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        timePicker = findViewById(R.id.timePicker)
        reminderInput = findViewById(R.id.reminderInput)
        setReminderButton = findViewById(R.id.setReminderButton)
        cancelReminderButton = findViewById(R.id.cancelReminderButton)
        reminderDetailsTextView = findViewById(R.id.reminderDetailsTextView)

        // Create notification channel (needed for Android O and above)
        createNotificationChannel()

        // Load and display saved alarm details if any
        loadSavedAlarm()

        setReminderButton.setOnClickListener {
            setAlarm()
        }

        cancelReminderButton.setOnClickListener {
            cancelAlarm()
        }
    }

    // Request notification permission for Android 13+
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }
    }

    // Create NotificationChannel for Android O+ (8.0 and above)
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "reminderChannel",
                "Reminder Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for reminder notifications"
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Function to set the alarm and save details to SharedPreferences
    private fun setAlarm() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
        calendar.set(Calendar.MINUTE, timePicker.minute)
        calendar.set(Calendar.SECOND, 0)

        val reminderText = reminderInput.text.toString()

        val intent = Intent(this, ReminderAlarmReceiver::class.java).apply {
            putExtra("reminder_text", reminderText)
        }

        pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Set the alarm using AlarmManagerCompat for backward compatibility
        AlarmManagerCompat.setExact(
            alarmManager,
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

        // Show a toast message when the alarm is set
        Toast.makeText(this, "Alarm set for ${timePicker.hour}:${timePicker.minute}", Toast.LENGTH_SHORT).show()

        // Save the reminder details to SharedPreferences
        saveAlarmDetails(reminderText, timePicker.hour, timePicker.minute)

        // Display the reminder below the TimePicker
        reminderDetailsTextView.text = "Reminder: $reminderText at ${timePicker.hour}:${timePicker.minute}"

        // Show the cancel button after the alarm is set
        cancelReminderButton.visibility = Button.VISIBLE
    }

    // Save the alarm details to SharedPreferences
    private fun saveAlarmDetails(reminderText: String, hour: Int, minute: Int) {
        val sharedPreferences = getSharedPreferences("reminderPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("reminder_text", reminderText)
        editor.putInt("hour", hour)
        editor.putInt("minute", minute)
        editor.apply()
    }

    // Load saved alarm details from SharedPreferences
    private fun loadSavedAlarm() {
        val sharedPreferences = getSharedPreferences("reminderPrefs", Context.MODE_PRIVATE)
        val reminderText = sharedPreferences.getString("reminder_text", null)
        val hour = sharedPreferences.getInt("hour", -1)
        val minute = sharedPreferences.getInt("minute", -1)

        if (reminderText != null && hour != -1 && minute != -1) {
            // Display the saved reminder
            reminderDetailsTextView.text = "Reminder: $reminderText at $hour:$minute"
            cancelReminderButton.visibility = Button.VISIBLE
        } else {
            // No reminder saved
            reminderDetailsTextView.text = "No reminder set"
            cancelReminderButton.visibility = Button.GONE
        }
    }

    // Function to cancel the alarm and clear saved details
    private fun cancelAlarm() {
        try {
            val reminderText = reminderInput.text.toString()

            // Recreate the same PendingIntent used to set the alarm
            val intent = Intent(this, ReminderAlarmReceiver::class.java).apply {
                putExtra("reminder_text", reminderText)
            }

            pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Cancel the alarm
            alarmManager.cancel(pendingIntent)

            // Clear the reminder details in SharedPreferences
            val sharedPreferences = getSharedPreferences("reminderPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            // Clear the UI
            reminderDetailsTextView.text = "No reminder set"
            cancelReminderButton.visibility = Button.GONE

            // Show a toast message when the alarm is canceled
            Toast.makeText(this, "Alarm canceled", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error canceling alarm: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
