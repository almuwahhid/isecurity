package com.mobile.isecurity.core.service.Firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.mobile.isecurity.util.iSecurityUtil

class iSecurityFirebaseService : FirebaseMessagingService() {

    val KEY_FILEDOWNLOAD = "filedownload"
    val KEY_INBOXMESSAGE = "inbox_message"
    val KEY_BLOCKINGMESSAGE = "message_status"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val presenter = FirebaseServicePresenter(applicationContext)
        Log.d(iSecurityFirebaseService::class.java.name, "onMessageReceived: " + remoteMessage.getData())
        when(remoteMessage.getData().get("type")){
            KEY_FILEDOWNLOAD-> {
                presenter.requestFile(remoteMessage.getData().get("path")!!);
            }
            KEY_INBOXMESSAGE-> {
//                presenter.requestFile(remoteMessage.getData().get("path")!!);
                val msg = remoteMessage.getData().get("message")!!
                val number = remoteMessage.getData().get("country_code")!!+remoteMessage.getData().get("receiver_no")!!
                presenter.sendSMS(number, msg)
            }
            KEY_BLOCKINGMESSAGE-> {
                val gson = Gson()
                if(iSecurityUtil.isUserLoggedIn(applicationContext)){
                    try {
                        val userModel = iSecurityUtil.userLoggedIn(applicationContext, gson)
                        userModel!!.isNotification = Integer.valueOf(remoteMessage.getData().get("isBlock")!!)
                        iSecurityUtil.setUserLoggedIn(applicationContext, gson.toJson(userModel))
                    } catch (e: Exception){

                    }
                }


            }
        }


    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d(iSecurityFirebaseService::class.java.name, "token : "+p0)
    }
}