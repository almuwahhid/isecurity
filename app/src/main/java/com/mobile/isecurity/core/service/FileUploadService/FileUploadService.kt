package com.mobile.isecurity.core.service.FileUploadService

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.mobile.isecurity.BuildConfig
import com.mobile.isecurity.app.main.MainActivity
import com.mobile.isecurity.data.StringConstant
import com.mobile.isecurity.data.model.SecurityMenuModel
import lib.alframeworkx.utils.AlStatic


class FileUploadService : Service(), FileUploadServiceView.View{

    var manager: NotificationManager? = null
    var notificationBuilder: NotificationCompat.Builder? = null
    var presenter: FIleUploadServicePresenter? = null
    var securityMenuModel: SecurityMenuModel? = null
    var gson = Gson()

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("FileUpload", "starting")
        securityMenuModel = intent!!.getSerializableExtra("data") as SecurityMenuModel
        notificationBuilder = NotificationCompat.Builder(this, "iSecurity_uploadfile")
        presenter = FIleUploadServicePresenter(baseContext, this)

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
            notificationChannel.enableVibration(false)
            notificationChannel.setSound(null, null)

            manager!!.createNotificationChannel(notificationChannel)
            notificationBuilder!!.setChannelId("iSecurity_id")
        }

        startForeground(1201030, getMyActivityNotification("Uploading File Path...", 100, 100))

//        presenter!!.requestFiles()
        presenter!!.requestFilesTest()
//        presenter!!.requestFilesVersion2()
        return super.onStartCommand(intent, flags, startId)
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

    override fun percentage(percent: Int, isDone: Boolean) {

    }

    override fun onRequestResult(isSuccess: Boolean, message: String) {
        AlStatic.ToastShort(baseContext, message)
        if(!isSuccess){
            securityMenuModel!!.status = if(securityMenuModel!!.status == 0) 1 else 0
        }
        AlStatic.setSPString(baseContext, securityMenuModel!!.id, gson.toJson(securityMenuModel))
        sendBroadcast(Intent(StringConstant.UPLOADING_FILE_STATUS))
        AlStatic.setSPBoolean(baseContext, StringConstant.UPLOADING_FILE_STATUS, false)
        stopSelf()
    }

    override fun onScanningProgress(title: String) {
        if(manager != null){
            updateNotification(title)
        }
    }

    private fun updateNotification(title: String) {
        val notification = getMyActivityNotification(title, 100, 100);
        manager!!.notify(1201030, notification)
    }

    override fun onDestroy() {
//        AlStatic.setSPBoolean(baseContext, StringConstant.UPLOADING_FILE_STATUS, false)
        super.onDestroy()
    }
}