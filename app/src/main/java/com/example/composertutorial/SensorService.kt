package com.example.composertutorial

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.SensorEvent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class SensorService : Service(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var linearAccelerationSensor: Sensor? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = createForegroundNotification()
        startForeground(1, notification)

        //initializing sensor manager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        //get sensor
        linearAccelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        //register sensor listener
        linearAccelerationSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null)
            return

        if (event?.sensor?.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            val acceleration = event.values[1]
            if (acceleration > 1) {
                triggerNotification(acceleration)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    private fun createForegroundNotification(): Notification {
        return NotificationCompat.Builder(this, "SENSOR_CHANNEL")
            .setContentTitle("Running Detection Active")
            .setContentText("Detecting motion...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }


    //create a notification channel
    // API 26 or higher requires this due the NotificationChannel class is not in the Support Library
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "SENSOR_CHANNEL",
                "Sensor Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // create and trigger a notification when motion is detected
    private fun triggerNotification(acceleration: Float){
        val context = this

        /*
        //create notification channel
        val channelId = "SENSOR_CHANNEL"
        val channelName = "Sensor Notifications"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = "Channel for motion sensor notifications"
        }*/

        //intent for opening the app when notification is clicked
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        //notification builder
        val notification = NotificationCompat.Builder(this, "SENSOR_CHANNEL")
            .setContentTitle("Are you running?")
            .setContentText("Linear acceleration detected: $acceleration m/s^2")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            // Set the intent that fires when the user taps the notification.
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(2, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

}