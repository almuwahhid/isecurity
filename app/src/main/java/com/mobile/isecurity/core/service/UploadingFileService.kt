package com.mobile.isecurity.core.service

import android.R
import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.mobile.isecurity.BuildConfig
import com.mobile.isecurity.app.main.MainActivity


class UploadingFileService : Service(){

    var manager: NotificationManager? = null
    var notificationBuilder: NotificationCompat.Builder? = null

    override fun onCreate() {
        super.onCreate()

        notificationBuilder = NotificationCompat.Builder(this, "iSecurity_uploadfile")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager = getSystemService(NotificationManager::class.java)
        }

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this,
            0, notificationIntent, 0)

        notificationBuilder = NotificationCompat.Builder(applicationContext, "iSecurity_id")

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            val notificationChannel = NotificationChannel(
                "iSecurity_id",
                BuildConfig.application_name,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationChannel.description = "iSecurity"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = ContextCompat.getColor(applicationContext, com.mobile.isecurity.R.color.colorPrimary)
            notificationChannel.vibrationPattern = longArrayOf(0, 1000)
            notificationChannel.enableVibration(true)

            manager!!.createNotificationChannel(notificationChannel)
            notificationBuilder!!.setChannelId("iSecurity_id")
        };

        startForeground(1201029, getMyActivityNotification("Uploading...", 0, 100))
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    //Notification provider
    private fun getMyActivityNotification(caption: String, completedUnits: Long, totalUnits: Long): Notification? {
        var percentComplete = 0
        if (totalUnits > 0) {
            percentComplete = (100 * completedUnits / totalUnits).toInt()
        }

        return notificationBuilder!!
            .setSmallIcon(com.mobile.isecurity.R.drawable.ic_logo)
            .setContentTitle(BuildConfig.application_name)
            .setContentText(caption)
            .setProgress(100, percentComplete, false)
            .setContentInfo("$percentComplete%")
            .setOngoing(true)
            .setAutoCancel(false)
            .build()
    }
}