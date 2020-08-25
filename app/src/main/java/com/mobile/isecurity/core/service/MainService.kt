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
import android.os.Environment
import android.os.FileObserver
import android.os.FileObserver.ALL_EVENTS
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.mobile.isecurity.R
import com.mobile.isecurity.app.cameraaccess.CameraAccessActivity
import com.mobile.isecurity.app.main.MainActivity
import com.mobile.isecurity.core.socket.SocketSingleton
import com.mobile.isecurity.data.model.UserModel
import com.mobile.isecurity.util.iSecurityUtil
import io.socket.client.Ack
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class MainService : Service(){

    var manager: NotificationManager? = null
    var notificationBuilder: NotificationCompat.Builder? = null
    private var mSocket: Socket? = null
    var filter: IntentFilter? = null
    var userModel: UserModel? = null
    var isTriggered: Boolean? = false
    val TAG = MainService::class.java.name

    var internalPath = ""
    var externalPath = ""


    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action){
                "stopservice" -> {
                    Log.d(TAG, "stop service huhu")
//                    mSocket!!.close()
//                    mSocket!!.disconnect()
//                    stopForeground(true)
//                    stopSelf()
//                    sendBroadcast(Intent("stopService"))
                }
                "senddata" -> {
                    Log.d(TAG, "huh "+intent.getStringExtra("data"))
                    mSocket!!.emit("rtc-receiver"+userModel!!.firebaseToken, intent.getStringExtra("data"),
                        object : Ack {
                            override fun call(vararg args: Any?) {
                                Log.d("TAGSecurityRTCFore", "call: getDatas " + args.size)
                                if (args.size > 0) {
                                    Log.d("TAGSecurityRTCFore", """emitGetListUser() ACK :${args[0]}""".trimIndent())
                                }
                            }
                        })
                }
                "init-socket" -> {
                    Log.d(TAG, "SOCKET INIT")
                    isTriggered = false

                    userModel = iSecurityUtil.userLoggedIn(applicationContext, Gson())

                    Log.d(TAG, "Here we comes "+userModel!!.firebaseToken)
                    val singleton = SocketSingleton(context)
                    mSocket = singleton.socket

                    mSocket!!.on(Socket.EVENT_CONNECT, Emitter.Listener {
                        Log.d(TAG, "SOCKET CONNECTED")
                    })
                    mSocket!!.on(Socket.EVENT_DISCONNECT, Emitter.Listener {
                        Log.d(TAG, "SOCKET DISCONNECTED")
                    })
                    mSocket!!.on("rtc-sender"+userModel!!.firebaseToken) { args ->
                        Log.d(TAG, "emitGetListUser() received listen to room called " + args[0].toString())
                        try {
                            if(args[0].toString().equals("rtc")){
                                /*if(AlStatic.getSPString(applicationContext, "iSecurity").equals("")){

                                } else {
                                    sendBroadcast(Intent("receivedata").putExtra("data", args[0].toString()))
                                }*/
                                Log.d(TAG, "hell yeaaa")
                                val dialogIntent = Intent(applicationContext, CameraAccessActivity::class.java)
                                dialogIntent.addCategory(Intent.CATEGORY_HOME)
//                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
//                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                dialogIntent.putExtra("data", args[0].toString())
                                startActivity(dialogIntent)

                            } else if(args[0].toString().equals("rtc-changeMode")){
//                    sendBroadcast(Intent("changemode").putExtra("data", args[0].toString()))
                                Log.d(TAG, "hell yeaaa")
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

                    mSocket!!.on("rtc-disconnected"+userModel!!.firebaseToken){args ->
                        Log.d(TAG, "emitGetListUser() received listen to disconnected called ")
                        sendBroadcast(Intent("disconnectdata").putExtra("data", ""))
                    }

                    if (!mSocket!!.connected()){
                        mSocket!!.connect()
                        Log.d("connect", "connect")
                    }
                }
                "init-monitoringfiles" -> {
                    observe()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        if(mSocket != null && mSocket!!.connected()){
            mSocket!!.close()
            mSocket!!.disconnect()
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        filter = IntentFilter()
        filter!!.addAction("senddata")
        filter!!.addAction("stopservice")
        filter!!.addAction("init-socket")
        registerReceiver(receiver, filter)

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
        }

        val notification = notificationBuilder!!
            .setContentTitle("iSecurity")
            .setContentText("service is on")
            .setSmallIcon(R.drawable.ic_logo)
            .setContentIntent(pendingIntent)
            .build()


        startForeground(1201029, notification)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    fun getInternalStoragePath(): File? {
        val parent: File = Environment.getExternalStorageDirectory().getParentFile()
        val external: File = Environment.getExternalStorageDirectory()
        val files: Array<File> = parent.listFiles()
        var internal: File? = null
        if (files != null) {
            for (i in files.indices) {
                if (files[i].getName().toLowerCase().startsWith("sdcard") && !files[i]
                        .equals(external)
                ) {
                    internal = files[i]
                }
            }
        }
        return internal
    }

    fun getExtenerStoragePath(): File? {
        return Environment.getExternalStorageDirectory()
    }

    fun observe() {
        val t = Thread(Runnable { //File[]   listOfFiles = new File(path).listFiles();
            var str = getInternalStoragePath()
            if (str != null) {
                internalPath = str.absolutePath
                Obsever(internalPath).startWatching()
            }
            str = getExtenerStoragePath()
            if (str != null) {
                externalPath = str.absolutePath
                Obsever(externalPath).startWatching()
            }
        })
        t.priority = Thread.MIN_PRIORITY
        t.start()
    }

    internal class Obsever @JvmOverloads constructor(var mPath: String, var mMask: Int = ALL_EVENTS) : FileObserver(mPath, mMask) {
        var mObservers: MutableList<SingleFileObserver>? = null
        override fun startWatching() {
            // TODO Auto-generated method stub
            if (mObservers != null) return
            mObservers = ArrayList()
            val stack: Stack<String> = Stack<String>()
            stack.push(mPath)
            while (!stack.empty()) {
                val parent: String = stack.pop()
                mObservers!!.add(SingleFileObserver(parent, mMask))
                val path = File(parent)
                val files = path.listFiles() ?: continue
                for (i in files.indices) {
                    if (files[i].isDirectory && files[i]
                            .name != "." && files[i].name != ".."
                    ) {
                        stack.push(files[i].path)
                    }
                }
            }
            for (i in 0 until mObservers!!.size) {
                mObservers!!.get(i).startWatching()
            }
        }

        override fun stopWatching() {
            // TODO Auto-generated method stub
            if (mObservers == null) return
            for (i in 0 until mObservers!!.size) {
                mObservers!![i].stopWatching()
            }
            mObservers!!.clear()
            mObservers = null
        }

        override fun onEvent(event: Int, path: String?) {
            if (event == FileObserver.OPEN) {
                //do whatever you want
            } else if (event == FileObserver.CREATE) {
                Log.d("MainService", "files created "+path)
            } else if (event == FileObserver.DELETE_SELF || event == FileObserver.DELETE) {
                //do whatever you want
            } else if (event == FileObserver.MOVE_SELF || event == FileObserver.MOVED_FROM || event == FileObserver.MOVED_TO) {
                //do whatever you want
            }
        }

        inner class SingleFileObserver(private val mPath: String, mask: Int) : FileObserver(mPath, mask) {
            override fun onEvent(event: Int, path: String?) {
                val newPath = "$mPath/$path"
                this@Obsever.onEvent(event, newPath)
            }

        }

    }
}