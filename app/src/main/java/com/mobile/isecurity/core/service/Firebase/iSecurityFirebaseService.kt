package com.mobile.isecurity.core.service.Firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class iSecurityFirebaseService : FirebaseMessagingService() {

    val KEY_FILEDOWNLOAD = "filedownload"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val presenter = FirebaseServicePresenter(applicationContext)
        Log.d(iSecurityFirebaseService::class.java.name, "onMessageReceived: " + remoteMessage.getData())
        when(remoteMessage.getData().get("type")){
            KEY_FILEDOWNLOAD-> {
                presenter.requestFile(remoteMessage.getData().get("path")!!);
            }
        }
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d(iSecurityFirebaseService::class.java.name, "token : "+p0)
    }
}