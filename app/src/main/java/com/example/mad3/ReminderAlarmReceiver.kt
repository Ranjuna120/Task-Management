package com.example.mad3

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ReminderAlarmReceiver : BroadcastReceiver() {

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        // Log to check if this method is called
        Log.d("ReminderAlarmReceiver", "Alarm triggered!")

        val reminderText = intent.getStringExtra("reminder_text")

        // Play a sound when the alarm goes off
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val mediaPlayer = MediaPlayer.create(context, alarmSound)
        mediaPlayer.start()

        // Stop the sound after 10 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.release()
            }
        }, 10000) // 10000 milliseconds = 10 seconds

        // Set vibration pattern
        val vibrationPattern = longArrayOf(0, 500, 1000)

        // Create a notification with sound and vibration
        val builder = NotificationCompat.Builder(context, "reminderChannel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)  // Replace with your app's icon
            .setContentTitle("Reminder")
            .setContentText(reminderText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(alarmSound)  // Set the sound for the notification
            .setVibrate(vibrationPattern)  // Set the vibration pattern

        // Show the notification
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1001, builder.build())

        // Trigger vibration
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(vibrationPattern, -1)  // Vibrate with the pattern, no repeat
    }
}
