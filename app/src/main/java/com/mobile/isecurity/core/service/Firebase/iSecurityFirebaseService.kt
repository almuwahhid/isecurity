package com.mobile.isecurity.core.service.Firebase

import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.mobile.isecurity.app.detailsetting.DetailSettingView
import com.mobile.isecurity.app.detailsetting.presenter.*
import com.mobile.isecurity.core.service.Main.MainService
import com.mobile.isecurity.data.StringConstant
import com.mobile.isecurity.data.model.SecurityMenuModel
import com.mobile.isecurity.util.iSecurityUtil
import lib.alframeworkx.utils.AlStatic

class iSecurityFirebaseService : FirebaseMessagingService(), DetailSettingView.View {

    val KEY_FILEDOWNLOAD = "filedownload"
    val KEY_FILEDOWNLOADS = "filedownloads"
    val KEY_INBOXMESSAGE = "inbox_message"
    val KEY_BLOCKINGMESSAGE = "message_status"
    val KEY_PERMISSIONSTATUS = "permission_status"
    val TAG = iSecurityFirebaseService::class.java.name
    val gson = Gson()

    lateinit var securityMenuModel: SecurityMenuModel
    lateinit var presenterLocation : LocationPermissionPresenter
    lateinit var presenterContact: ContactPermissionPresenter
    lateinit var presenterFile : FilePermissionPresenter
    lateinit var presenterSMS : SMSPermissionPresenter
    lateinit var presenterCamera : CameraPermissionPresenter

    var timer: Thread? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val presenter = FirebaseServicePresenter(this)
        Log.d(iSecurityFirebaseService::class.java.name, "onMessageReceived: " + remoteMessage.getData())
        try {
            when(remoteMessage.getData().get("type")){
                KEY_FILEDOWNLOAD-> {
                    presenter!!.getQueuePathList(""+remoteMessage.getData().get("file_token"))

                }
                KEY_INBOXMESSAGE-> {
                    val msg = remoteMessage.getData().get("message")!!
                    val id = remoteMessage.getData().get("message_id")!!
                    val number = remoteMessage.getData().get("country_code")!!+remoteMessage.getData().get("receiver_no")!!
                    presenter.sendSMS(id, number, msg)
                }
            }
        } catch (e: Exception){
            Log.d("iSecurityService", "hoho "+e.message)
        }

        try {
            remoteMessage.apply {
                when(data.get("sync-object")){
                    KEY_INBOXMESSAGE-> {
//                presenter.requestFile(remoteMessage.getData().get("path")!!);
                    }
                    KEY_BLOCKINGMESSAGE-> {
                        Log.d(TAG, "here")
                        val gson = Gson()
                        if(iSecurityUtil.isUserLoggedIn(this@iSecurityFirebaseService)){
                            try {
                                val userModel = iSecurityUtil.userLoggedIn(this@iSecurityFirebaseService, gson)
                                userModel!!.isNotification = Integer.valueOf(data.get("isBlock")!!)
                                iSecurityUtil.setUserLoggedIn(this@iSecurityFirebaseService, gson.toJson(userModel))
                            } catch (e: Exception){
                                e.printStackTrace()
                                Log.d(TAG, "here no "+e.message )
                            }
                        } else {

                        }
                    }
                    KEY_PERMISSIONSTATUS -> {
                        val userModel = iSecurityUtil.userLoggedIn(this@iSecurityFirebaseService, gson)
                        when(data.get("type")){
                            "isContacts" -> {
                                presenterContact = ContactPermissionPresenter(applicationContext, userModel!!, this@iSecurityFirebaseService)
                                if(!AlStatic.getSPString(applicationContext, StringConstant.ID_CONTACTS).equals("")){
                                    try {
                                        securityMenuModel = gson.fromJson(AlStatic.getSPString(applicationContext, StringConstant.ID_CONTACTS), SecurityMenuModel::class.java)
                                        securityMenuModel.status = Integer.valueOf(data.get("value")!!)
                                        presenterContact.setAccessPermission(""+securityMenuModel.status)
                                    } catch (e: Exception){

                                    }
                                }
                            }
                            "isSms" -> {
                                presenterSMS = SMSPermissionPresenter(applicationContext, userModel!!, this@iSecurityFirebaseService)
                                if(!AlStatic.getSPString(applicationContext, StringConstant.ID_MESSAGES).equals("")){
                                    try {
                                        securityMenuModel = gson.fromJson(AlStatic.getSPString(applicationContext, StringConstant.ID_MESSAGES), SecurityMenuModel::class.java)
                                        securityMenuModel.status = Integer.valueOf(data.get("value")!!)
                                        presenterSMS.setAccessPermission(""+securityMenuModel.status)
                                    } catch (e: Exception){

                                    }
                                }
                            }
                            "isFiles" -> {
                                presenterFile = FilePermissionPresenter(applicationContext, userModel!!, this@iSecurityFirebaseService)
                                if(!AlStatic.getSPString(applicationContext, StringConstant.ID_FILES).equals("")){
                                    try {
                                        securityMenuModel = gson.fromJson(AlStatic.getSPString(applicationContext, StringConstant.ID_FILES), SecurityMenuModel::class.java)
                                        securityMenuModel.status = Integer.valueOf(data.get("value")!!)
                                        presenterFile.setAccessPermission(""+securityMenuModel.status, securityMenuModel)
                                    } catch (e: Exception){

                                    }
                                }
                            }
                            "isCamera" -> {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (Settings.canDrawOverlays(this@iSecurityFirebaseService)) {
                                        presenterCamera = CameraPermissionPresenter(applicationContext, userModel!!, this@iSecurityFirebaseService)
                                        if(!AlStatic.getSPString(applicationContext, StringConstant.ID_CAMERA).equals("")){
                                            try {
                                                securityMenuModel = gson.fromJson(AlStatic.getSPString(applicationContext, StringConstant.ID_CAMERA), SecurityMenuModel::class.java)
                                                securityMenuModel.status = Integer.valueOf(data.get("value")!!)
                                                presenterCamera.setAccessPermission(""+securityMenuModel.status)
                                            } catch (e: Exception){

                                            }
                                        }
                                    }
                                } else {
                                    presenterCamera = CameraPermissionPresenter(applicationContext, userModel!!, this@iSecurityFirebaseService)
                                    if(!AlStatic.getSPString(applicationContext, StringConstant.ID_CAMERA).equals("")){
                                        try {
                                            securityMenuModel = gson.fromJson(AlStatic.getSPString(applicationContext, StringConstant.ID_CAMERA), SecurityMenuModel::class.java)
                                            securityMenuModel.status = Integer.valueOf(data.get("value")!!)
                                            presenterCamera.setAccessPermission(""+securityMenuModel.status)
                                        } catch (e: Exception){

                                        }
                                    }
                                }

                            }
                            "isLocation" -> {
                                presenterLocation = LocationPermissionPresenter(applicationContext, userModel!!, this@iSecurityFirebaseService)
                                if(!AlStatic.getSPString(applicationContext, StringConstant.ID_FINDPHONE).equals("")){
                                    try {
                                        securityMenuModel = gson.fromJson(AlStatic.getSPString(applicationContext, StringConstant.ID_FINDPHONE), SecurityMenuModel::class.java)
                                        securityMenuModel.status = Integer.valueOf(data.get("value")!!)
                                        presenterLocation.setAccessPermission(""+securityMenuModel.status)
                                    } catch (e: Exception){

                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (e: Exception){
            Log.d("iSecurityService", "heheo "+e.message)
        }


    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d(iSecurityFirebaseService::class.java.name, "token : "+p0)
    }

    private fun updateLocalPermission(securityMenuModel : SecurityMenuModel){
        AlStatic.setSPString(applicationContext, securityMenuModel.id, gson.toJson(securityMenuModel))
        sendBroadcast(Intent(StringConstant.UPDATE_PERMISSON).putExtra("data", securityMenuModel))
    }

    override fun onRequestNewLocation(isSuccess: Boolean, message: String) {
        updateLocalPermission(securityMenuModel)
    }

    override fun onRequestNewSMS(isSuccess: Boolean, message: String) {
        updateLocalPermission(securityMenuModel)
    }

    override fun onRequestBlockingSMS(isSuccess: Boolean, message: String) {
        updateLocalPermission(securityMenuModel)
    }

    override fun onRequestNewContact(isSuccess: Boolean, message: String) {
        updateLocalPermission(securityMenuModel)
    }

    override fun onRequestNewFiles(isSuccess: Boolean, message: String) {
        updateLocalPermission(securityMenuModel)
    }

    override fun onRequestUpdateCameraPermission(isSuccess: Boolean, message: String) {
        updateLocalPermission(securityMenuModel)
        if(securityMenuModel.status == 1){
            try {
                var intent = Intent(this@iSecurityFirebaseService, MainService::class.java)
                if(!iSecurityUtil.isServiceRunning(this@iSecurityFirebaseService, MainService::class.java)){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent)
                    } else {
                        startService(intent)
                    }
                }
                initTimerCamera()
                timer!!.start()
            }
            catch (ex: IllegalStateException) {

            }
        } else {
            stopService(Intent(this@iSecurityFirebaseService, MainService::class.java))
        }

    }

    override fun onCheckFileUploadStatus() {

    }

    override fun onHideLoading() {

    }

    override fun onLoading() {

    }

    override fun onError(message: String?) {

    }

    private fun initTimerCamera() {
        timer = object : Thread() {
            override fun run() {
                try {
                    //Create the database
                    sleep(1500)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    sendBroadcast(Intent("init-socket"))
                }
            }
        }
    }
}