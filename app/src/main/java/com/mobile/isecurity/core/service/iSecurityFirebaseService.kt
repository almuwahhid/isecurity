package com.mobile.isecurity.core.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class iSecurityFirebaseService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(
            iSecurityFirebaseService::class.java.name, "onMessageReceived: " + remoteMessage.getData().get("data")
        )
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d(iSecurityFirebaseService::class.java.name, "token : "+p0)
    }
}