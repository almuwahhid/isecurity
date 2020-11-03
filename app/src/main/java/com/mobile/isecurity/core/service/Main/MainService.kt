package com.mobile.isecurity.core.service.Main

//import io.socket.client.Ack
//import io.socket.client.Socket
//import io.socket.emitter.Emitter
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
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Ack
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.mobile.isecurity.R
import com.mobile.isecurity.app.cameraaccess.CameraAccessActivity
import com.mobile.isecurity.app.main.MainActivity
import com.mobile.isecurity.core.socket.SocketSingleton
import com.mobile.isecurity.data.model.UserModel
import com.mobile.isecurity.util.iSecurityUtil
import org.json.JSONException
import java.io.File
import java.net.URISyntaxException
import java.util.*
import kotlin.collections.ArrayList

class MainService : Service(){

    var manager: NotificationManager? = null
    var notificationBuilder: NotificationCompat.Builder? = null
    var filter: IntentFilter? = null
    var userModel: UserModel? = null
    var isTriggered: Boolean? = false
    val TAG = MainService::class.java.name

    var internalPath = ""
    var externalPath = ""

    var presenter : MainPresenter? = null

    private var mSocket: Socket? = null

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
                    if (!mSocket!!.connected()){
                        mSocket!!.close()
                        mSocket!!.disconnect()
                        mSocket!!.connect()
                        Log.d("connect", "connect")
                    }
                    mSocket!!.emit("rtc-receiver"+userModel!!.firebaseToken, ""+intent.getStringExtra("data"),
//                    mSocket!!.emit("rtc-receiver"+userModel!!.firebaseToken, "rtc",
                        object : Ack {
                            override fun call(vararg args: Any?) {
                                Log.d("TAGSecurityRTCFore", "call: getDatas " + args.size)
                                if (args.size > 0) {
                                    Log.d("TAGSecurityRTCFore", """emitGetListUser() ACK :${args[0]}""".trimIndent())
                                }
                            }
                        })
                    Log.d(TAG, "huh here it is - "+"rtc-receiver"+userModel!!.firebaseToken+"---"+intent.getStringExtra("data"))
                }
                "init-socket" -> {
                    Log.d(TAG, "SOCKET INIT")
                    isTriggered = false
                    if (!mSocket!!.connected()){
                        mSocket!!.close()
                        mSocket!!.disconnect()
                        mSocket!!.connect()
                        Log.d("connect", "connect")
                    }
                }
                "init-monitoringfiles" -> {
                    observe()
                }
                "refresh" -> {
                    mSocket!!.emit("rtc-receiver"+userModel!!.firebaseToken, "refresh",
                        object : Ack {
                            override fun call(vararg args: Any?) {
                                Log.d(TAG, "call: getDatas " + args.size)
                                if (args.size > 0) {
                                    Log.d(TAG, """emitGetListUser() ACK :${args[0]}""".trimIndent())
                                }
                            }
                        })
                }
            }
        }
    }

    private fun initSocket(context : Context){
        userModel = iSecurityUtil.userLoggedIn(applicationContext, Gson())
        Log.d(TAG, "Here we comes "+userModel!!.firebaseToken)
        val singleton = SocketSingleton(context)
//        mSocket = singleton.socket
        try {
            val opts =
                IO.Options()
            opts.forceNew = true
            opts.reconnection = true
            mSocket = IO.socket("https://camera.isecurity.mobi/", opts)

            mSocket!!.on(Socket.EVENT_CONNECT, Emitter.Listener {
                Log.d(TAG, "SOCKET CONNECTED")
            })
            mSocket!!.on(Socket.EVENT_DISCONNECT, Emitter.Listener {
                Log.d(TAG, "SOCKET DISCONNECTED")
            })
            mSocket!!.on(Socket.EVENT_ERROR, Emitter.Listener {args ->
                Log.d(TAG, "EVENT ERROR "+args)
            })
            mSocket!!.on(Socket.EVENT_MESSAGE, Emitter.Listener {args ->
                Log.d(TAG, "EVENT MESSAGE "+args)
            })
            mSocket!!.on(Socket.EVENT_CONNECT_ERROR, Emitter.Listener {args ->
                Log.d(TAG, "EVENT CONNECT ERROR "+args)
            })
            mSocket!!.on("rtc-sender"+userModel!!.firebaseToken) { args ->
                Log.d(TAG, "emitGetListUser() received listen to room called " + args[0].toString())
                try {
                    if(args[0].toString().equals("rtc")){
                        Log.d(TAG, "hell yeaaa "+args[0].toString())
                        val dialogIntent = Intent(applicationContext, CameraAccessActivity::class.java)
                        dialogIntent.addCategory(Intent.CATEGORY_HOME)
//                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
//                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
//                                dialogIntent.putExtra("data", args[0].toString())
                        dialogIntent.putExtra("data", "front")
                        startActivity(dialogIntent)

                    } else if(args[0].toString().equals("rtc-back")){
                        Log.d(TAG, "hell yeaaa "+args[0].toString())
                        val dialogIntent = Intent(applicationContext, CameraAccessActivity::class.java)
                        dialogIntent.addCategory(Intent.CATEGORY_HOME)
                        dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
//                                dialogIntent.putExtra("data", args[0].toString())
                        dialogIntent.putExtra("data", "back")
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
                    } else if(args[0].toString().equals("rtc-changeMode-front")){
//                    sendBroadcast(Intent("changemode").putExtra("data", args[0].toString()))
                        Log.d(TAG, "hell yeaaa")
                        val dialogIntent = Intent(applicationContext, CameraAccessActivity::class.java)
                        dialogIntent.addCategory(Intent.CATEGORY_HOME)
//                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
//                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                        dialogIntent.putExtra("data", "front")
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

            /*if (!mSocket!!.connected()){
                mSocket!!.connect()
                Log.d("connect", "connect")
            }*/
            mSocket!!.connect()
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
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
        filter!!.addAction("init-monitoringfiles")
        filter!!.addAction("refresh")
        registerReceiver(receiver, filter)

        presenter = MainPresenter(baseContext)

        if(iSecurityUtil.isUserLoggedIn(applicationContext)){
            initSocket(applicationContext)
        }

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
        var internal: File? = null
        try {
            val files: Array<File> = parent.listFiles()
            if (files != null) {
                for (i in files.indices) {
                    if (files[i].getName().toLowerCase().startsWith("sdcard") && !files[i]
                            .equals(external)
                    ) {
                        internal = files[i]
                    }
                }
            }
        } catch (e : Exception){

        }
        return internal
    }

    fun getExtenerStoragePath(): File? {
        return Environment.getExternalStorageDirectory()
    }

    fun observe() {
        Log.d("MainService", "observing")
        val t = Thread(Runnable { //File[]  listOfFiles = new File(path).listFiles();
            var str = getInternalStoragePath()
            if (str != null) {
                internalPath = str.absolutePath
                Obsever(
                    internalPath, presenter!!
                ).startWatching()
            }
            str = getExtenerStoragePath()
            if (str != null) {
                externalPath = str.absolutePath
                Obsever(
                    externalPath, presenter!!
                ).startWatching()
            }
        })
        t.priority = Thread.MIN_PRIORITY
        t.start()
    }

    internal class Obsever @JvmOverloads constructor(var mPath: String, var presenter : MainPresenter, var mMask: Int = ALL_EVENTS) : FileObserver(mPath, mMask) {
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
                if(!path!!.contains("cache")){
                    presenter.uploadFile(path!!)
                }
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