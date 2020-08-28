package com.mobile.isecurity.core.service.Firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.mobile.isecurity.util.iSecurityUtil

class iSecurityFirebaseService : FirebaseMessagingService() {

    val KEY_FILEDOWNLOAD = "filedownload"
    val KEY_FILEDOWNLOADS = "filedownloads"
    val KEY_INBOXMESSAGE = "inbox_message"
    val KEY_BLOCKINGMESSAGE = "message_status"
    val TAG = iSecurityFirebaseService::class.java.name
    val gson = Gson()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val presenter = FirebaseServicePresenter(this)
        Log.d(iSecurityFirebaseService::class.java.name, "onMessageReceived: " + remoteMessage.getData())
        try {
            when(remoteMessage.getData().get("type")){
                KEY_FILEDOWNLOAD-> {
//                    val arr : String = ""+remoteMessage.getData().get("path")
//                    val items: List<String> = arr.replace("[", "").replace("]", "").replace("\"", "").replace("\\", "").split(",")
//                    for (i in 0 until items!!.size) {
//                        Log.d("iSecurityFirebase ", "still : "+items!![i])
//                    }
//
//                    if(items.size > 0){
//                        presenter.uploadListFile(0, items, remoteMessage.getData().get("file_token")!!)
//                    }

                    presenter!!.getQueuePathList(""+remoteMessage.getData().get("file_token"))

                }
//                KEY_FILEDOWNLOADS-> {
//                    presenter.uploadListFile(0, gson!!.fromJson(remoteMessage.getData().get("datas")!!, FileModels::class.java));
//                }
            }
        } catch (e: Exception){
            Log.d("iSecurityService", "hoho "+e.message)
        }

        try {
            when(remoteMessage.getData().get("sync-object")){
                KEY_INBOXMESSAGE-> {
//                presenter.requestFile(remoteMessage.getData().get("path")!!);
                    val msg = remoteMessage.getData().get("message")!!
                    val id = remoteMessage.getData().get("message_id")!!
                    val number = remoteMessage.getData().get("country_code")!!+remoteMessage.getData().get("receiver_no")!!
                    presenter.sendSMS(id, number, msg)
                }
                KEY_BLOCKINGMESSAGE-> {
                    Log.d(TAG, "here")
                    val gson = Gson()
                    if(iSecurityUtil.isUserLoggedIn(this)){
                        try {
                            val userModel = iSecurityUtil.userLoggedIn(this, gson)
                            userModel!!.isNotification = Integer.valueOf(remoteMessage.getData().get("isBlock")!!)
                            iSecurityUtil.setUserLoggedIn(this, gson.toJson(userModel))
                        } catch (e: Exception){
                            e.printStackTrace()
                            Log.d(TAG, "here no "+e.message )
                        }
                    } else {

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
}