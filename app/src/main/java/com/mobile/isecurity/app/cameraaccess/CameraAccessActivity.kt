package com.mobile.isecurity.app.cameraaccess

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Point
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import chat.rocket.android.call.GTRTC.GTRTCCLient
import com.almuwahhid.isecurityrtctest.Candidate
import com.almuwahhid.isecurityrtctest.GTRTC.CallStatic
import com.almuwahhid.isecurityrtctest.Payload
import com.google.gson.Gson
import com.mobile.isecurity.BuildConfig
import com.mobile.isecurity.R
import com.mobile.isecurity.core.rtc.GTPeerConnectionParameters
import com.mobile.isecurity.core.service.MainService
import com.mobile.isecurity.util.PermissionChecker
import com.mobile.isecurity.util.iSecurityUtil
import kotlinx.android.synthetic.main.activity_camera_access.*
import lib.alframeworkx.utils.AlStatic
import org.webrtc.MediaStream
import org.webrtc.VideoRenderer
import org.webrtc.VideoRendererGui

class CameraAccessActivity : AppCompatActivity(), GTRTCCLient.RTCListener {

    var rtcClient: GTRTCCLient? = null
    internal var gson: Gson? = null
    var filter: IntentFilter? = null
    var isRTCOk = false

    private val scalingType = VideoRendererGui.ScalingType.SCALE_ASPECT_FILL
    private var localRender: VideoRenderer.Callbacks? = null
    private var remoteRender: VideoRenderer.Callbacks? = null

    var timer: Thread? = null

    private val RequiredPermissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    protected var permissionChecker = PermissionChecker()

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val data = intent.getStringExtra("data")
            if(data.equals("rtc")){
                if(isRTCOk){
                    sendBroadcast(Intent("senddata").putExtra("data", "rtc"))
                } else {
                    initRTC(true)
                }
            } else {
                if(data.contains("\"candidate\"")){
                    Log.d("TAGSecurityRTCFore", "candidate")
                    val candidate: Candidate = gson!!.fromJson(data, Candidate::class.java)
                    rtcClient!!.answerCandidate(candidate)
                } else if(data.contains("\"answer\"")){
                    Log.d("TAGSecurityRTCFore", "truee")
                    val payload: Payload = gson!!.fromJson(data, Payload::class.java)
                    rtcClient!!.answerAnswer(payload)
                } else if(data.contains("\"offer\"")){
                    Log.d("TAGSecurityRTCFore", "false")
                    val payload: Payload = gson!!.fromJson(data, Payload::class.java)
                    rtcClient!!.answerOffer(payload)
                }
            }
        }
    }

    private fun checkPermissions() {
        permissionChecker.verifyPermissions(this, RequiredPermissions, object : PermissionChecker.VerifyPermissionsCallback {

            override fun onPermissionAllGranted() {
                var intent = Intent(this@CameraAccessActivity, MainService::class.java)
                try {
                    if(!iSecurityUtil.isServiceRunning(this@CameraAccessActivity, MainService::class.java)){
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(intent)
                        } else {
                            startService(intent)
                        }
                    }
                }
                catch (ex: IllegalStateException) {

                }
            }

            override fun onPermissionDeny(permissions: Array<String>) {
                Toast.makeText(this@CameraAccessActivity, "Please grant required permissions.", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAGSecurityRTCFore", "hooll yeaaa")
        AlStatic.setSPString(this, "iSecurity", "ok")
        initTimer()

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.addFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        setContentView(R.layout.activity_camera_access)
        checkPermissions()

        filter = IntentFilter()
        filter!!.addAction("receivedata")

        registerReceiver(receiver, filter)

        gson = Gson()

        if(intent.hasExtra("data")){
            initRTC(true)
        }
    }

    private fun initRTC(istrue: Boolean){
        isRTCOk = true
        glview_call.setPreserveEGLContextOnPause(true)
        glview_call.setKeepScreenOn(true)
        VideoRendererGui.setView(glview_call) {
            init()
            if(rtcClient!=null){
                rtcClient!!.initPeer()
            }
            if(istrue){
                sendBroadcast(Intent("senddata").putExtra("data", "rtc"))
            }
        }

        remoteRender = VideoRendererGui.create(
            CallStatic.REMOTE_X, CallStatic.REMOTE_Y,
            CallStatic.REMOTE_WIDTH, CallStatic.REMOTE_HEIGHT, scalingType, false)
        localRender = VideoRendererGui.create(
            CallStatic.LOCAL_X_CONNECTING, CallStatic.LOCAL_Y_CONNECTING,
            CallStatic.LOCAL_WIDTH_CONNECTING, CallStatic.LOCAL_HEIGHT_CONNECTING, scalingType, true)
    }

    private fun init(){
        val displaySize = Point()
        windowManager.defaultDisplay.getSize(displaySize)
        val params = GTPeerConnectionParameters(
            true, false, displaySize.x, displaySize.y, 30, 1, CallStatic.VIDEO_CODEC_VP9, true, 1, CallStatic.AUDIO_CODEC_OPUS, true)
        rtcClient = GTRTCCLient(this, params!!, this, VideoRendererGui.getEGLContext())
    }

    override fun onCallReady(type: String, sdp: String) {
        Log.d("senddata", "hee "+type)
        sendBroadcast(Intent("senddata").putExtra("data", gson!!.toJson(Payload(Payload.Sdp("cccf2335-66bf-11e2-ffed-e7f9e438c5b8\"", type!!, sdp!!)))))
    }

    override fun onCandidateCall(label: Int, id: String, candidate: String) {
        Log.d("iSecurity id", ""+label)
        Log.d("iSecurity label", id)
        Log.d("iSecurity candidate", candidate)
        sendBroadcast(Intent("senddata").putExtra("data", gson!!.toJson(Candidate(Candidate.Detail(candidate, ""+id, ""+label), "cccf2335-66bf-11e2-ffed-e7f9e438c5b8"))))
    }

    override fun onStatusChanged(newStatus: String) {
        Log.d("iSecurity new Status", newStatus)
//        val startMain = Intent(Intent.ACTION_MAIN)
//        startMain.addCategory(Intent.CATEGORY_HOME)
//        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        startActivity(startMain)
        timer!!.start()

    }

    private fun initTimer() {
        timer = object : Thread() {
            override fun run() {
                try {
                    //Create the database
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        sleep(1000)
                    } else {
                        sleep(3000)
                    }

                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    finish()
                }
            }
        }
    }

    override fun onLocalStream(localStream: MediaStream) {

    }

    override fun onAddRemoteStream(remoteStream: MediaStream) {
        Log.d("iSecurity remote Stream", "remote Stream ok")
        remoteStream.videoTracks[0].addRenderer(VideoRenderer(remoteRender))
        VideoRendererGui.update(remoteRender,
            CallStatic.REMOTE_X, CallStatic.REMOTE_Y,
            CallStatic.REMOTE_WIDTH, CallStatic.REMOTE_HEIGHT, scalingType, false)
        VideoRendererGui.update(localRender,
            CallStatic.LOCAL_X_CONNECTED, CallStatic.LOCAL_Y_CONNECTED,
            CallStatic.LOCAL_WIDTH_CONNECTED, CallStatic.LOCAL_HEIGHT_CONNECTED,
            scalingType, false)
    }

    override fun onRemoveRemoteStream() {
        VideoRendererGui.update(localRender,
            CallStatic.LOCAL_X_CONNECTING, CallStatic.LOCAL_Y_CONNECTING,
            CallStatic.LOCAL_WIDTH_CONNECTING, CallStatic.LOCAL_HEIGHT_CONNECTING,
            scalingType, false)
    }

    override fun onDestroy() {
        AlStatic.setSPString(this, "iSecurity", "")
        unregisterReceiver(receiver)
        super.onDestroy()
//        unregisterReceiver(receiver)
    }
}
