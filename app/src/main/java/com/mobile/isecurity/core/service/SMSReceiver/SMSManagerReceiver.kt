package com.mobile.isecurity.core.service.SMSReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import com.google.gson.Gson
import com.mobile.isecurity.app.detailsetting.DetailSettingView
import com.mobile.isecurity.app.detailsetting.presenter.SMSPermissionPresenter
import com.mobile.isecurity.core.service.SMSManager.SMSManagerService
import com.mobile.isecurity.data.model.SMS.SMSModel
import com.mobile.isecurity.data.model.UserModel
import com.mobile.isecurity.util.iSecurityUtil


class SMSManagerReceiver: BroadcastReceiver(), DetailSettingView.View {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("phoneStateReceiver", " hi "+intent!!.getAction())
        if (intent!!.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            val bundle: Bundle = intent.getExtras()!!
            val messages = bundle["pdus"] as Array<Any>?
            val smsMessage: Array<SmsMessage?> = arrayOfNulls<SmsMessage>(messages!!.size)
            for (n in messages!!.indices) {
                smsMessage[n] = SmsMessage.createFromPdu(messages!![n] as ByteArray)
            }
            val numberSms: String = smsMessage[0]!!.getOriginatingAddress()!!
            val messageSms = smsMessage[0]!!.getDisplayMessageBody()
            var dateTimeSms = smsMessage[0]!!.getTimestampMillis()





//            if (numberSms == blockingNumber) {
//                abortBroadcast()
//            }

            if(iSecurityUtil.isUserLoggedIn(context!!)){
                val userMode = iSecurityUtil.userLoggedIn(context!!, Gson())
                if(isBlocked(userMode!!)){
                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
                        abortBroadcast()
                    } else {
//                        context.stopService(Intent(context, SMSManagerService::class.java))
//                        context.startService(Intent(context, SMSManagerService::class.java))
                        deleteSMS(context, messageSms, numberSms)
                    }
                    val presenter = SMSServicePresenter(context!!, userMode!!)
                    presenter.requestSMS(SMSModel(messageSms, numberSms, "delivered", ""+dateTimeSms, "inbox"))
                }
            }


        }
    }

    fun deleteSMS(context: Context, message: String, number: String) {
        try {
            Log.d("SMSManagerReceiver", "Deleting SMS from inbox")
            val uriSms: Uri = Uri.parse("content://sms/inbox")
            val c: Cursor? = context.contentResolver.query(
                uriSms, arrayOf(
                    "_id", "thread_id", "address",
                    "person", "date", "body"
                ), null, null, null
            )
            if (c != null && c.moveToFirst()) {
                do {
                    val id: Long = c.getLong(0)
                    val threadId: Long = c.getLong(1)
                    val address: String = c.getString(2)
                    val body: String = c.getString(5)
                    if (message == body && address == number) {
                        Log.d("SMSManagerReceiver", "Deleting SMS with id: $threadId")
                        context.contentResolver.delete(
                            Uri.parse("content://sms/$id"), null, null
                        )
                    }
                } while (c.moveToNext())
            }
        } catch (e: Exception) {
            Log.d("SMSManagerReceiver", "Could not delete SMS from inbox: " + e.message)
        }
    }

    private fun isBlocked(userModel: UserModel): Boolean{
        val isBlock = userModel.isNotification
        if(isBlock == 1){
            return true
        } else {
            return false
        }
    }

    override fun onRequestNewLocation(isSuccess: Boolean, message: String) {
        TODO("Not yet implemented")
    }

    override fun onRequestNewSMS(isSuccess: Boolean, message: String) {

    }

    override fun onRequestBlockingSMS(isSuccess: Boolean, message: String) {

    }

    override fun onRequestNewContact(isSuccess: Boolean, message: String) {
        TODO("Not yet implemented")
    }

    override fun onRequestNewFiles(isSuccess: Boolean, message: String) {
        TODO("Not yet implemented")
    }

    override fun onRequestUpdateCameraPermission(isSuccess: Boolean, message: String) {
        TODO("Not yet implemented")
    }

    override fun onCheckFileUploadStatus() {

    }

    override fun onHideLoading() {

    }

    override fun onLoading() {

    }

    override fun onError(message: String?) {

    }
}