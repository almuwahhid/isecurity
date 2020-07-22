package com.mobile.isecurity.core.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.almuwahhid.isecurityrtctest.Candidate
import com.google.gson.Gson
import com.mobile.isecurity.R
import com.mobile.isecurity.app.cameraaccess.CameraAccessActivity
import com.mobile.isecurity.app.main.MainActivity
import com.mobile.isecurity.core.socket.SocketSingleton
import io.socket.client.Ack
import io.socket.client.Socket
import io.socket.emitter.Emitter
import lib.alframeworkx.utils.AlStatic
import org.json.JSONException

class MainService : Service(){

    var manager: NotificationManager? = null
    var notificationBuilder: NotificationCompat.Builder? = null
    private var mSocket: Socket? = null
    var filter: IntentFilter? = null

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action){
                "stopservice" -> {
                    Log.d("stopservice", "huh")
                    stopForeground(true)
                    stopSelf()
                }
                "senddata" -> {
                    mSocket!!.emit("rtc-receiver2", intent.getStringExtra("data"),
                        object : Ack {
                            override fun call(vararg args: Any?) {
                                Log.d("TAGSecurityRTCFore", "call: getDatas " + args.size)
                                if (args.size > 0) {
                                    Log.d("TAGSecurityRTCFore", """emitGetListUser() ACK :${args[0]}""".trimIndent())
                                }
                            }
                        })
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        mSocket!!.disconnect()
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        filter = IntentFilter()
        filter!!.addAction("senddata")
        filter!!.addAction("stopservice")
        registerReceiver(receiver, filter)

        Log.d("TAGSecurityRTCFore", "Here we comes")
        val singleton = SocketSingleton.get(applicationContext)
        mSocket = singleton.socket

        mSocket!!.on(Socket.EVENT_CONNECT, Emitter.Listener {
            Log.d("TAGSecurityRTCFore", "SOCKET CONNECTED")
            //                t.schedule(new ClassEmitNotifNews(), 0, 5000);
        })
        mSocket!!.on("rtc-sender2") { args ->
            Log.d("TAGSecurityRTCFore", "emitGetListUser() received listen to room called " + args[0].toString())
            try {

                if(args[0].toString().equals("rtc")){
                    /*if(AlStatic.getSPString(applicationContext, "iSecurity").equals("")){

                    } else {
                        sendBroadcast(Intent("receivedata").putExtra("data", args[0].toString()))
                    }*/
                    Log.d("TAGSecurityRTCFore", "hell yeaaa")
                    val dialogIntent = Intent(applicationContext, CameraAccessActivity::class.java)
                    dialogIntent.addCategory(Intent.CATEGORY_HOME)
//                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
//                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    dialogIntent.putExtra("data", args[0].toString())
                    startActivity(dialogIntent)
                } else {
                    sendBroadcast(Intent("receivedata").putExtra("data", args[0].toString()))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        if (!mSocket!!.connected()){
            mSocket!!.connect()
            Log.d("connect", "connect")
        }

        manager = getSystemService(NotificationManager::class.java)
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this,
            0, notificationIntent, 0)

        notificationBuilder = NotificationCompat.Builder(applicationContext, "iSecurity_id")

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            val notificationChannel = NotificationChannel(
                "iSecurity_id",
                "My Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            // Configure the notification channel.
            // Configure the notification channel.
            notificationChannel.description = "iSecurity"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = ContextCompat.getColor(applicationContext, R.color.colorPrimary)
//            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            //            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.vibrationPattern = longArrayOf(0, 1000)
            notificationChannel.enableVibration(true)

            manager!!.createNotificationChannel(notificationChannel)
            notificationBuilder!!.setChannelId("iSecurity_id")
        };

        val notification = notificationBuilder!!
            .setContentTitle("iSecurity")
            .setContentText("service is on")
            .setSmallIcon(R.drawable.ic_logo)
            .setContentIntent(pendingIntent)
            .build()


        startForeground(1201029, notification)
    }

    /*override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        return START_NOT_STICKY
        return null
    }*/
}