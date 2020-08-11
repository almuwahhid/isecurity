package com.mobile.isecurity.core.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import com.google.gson.Gson
import com.mobile.isecurity.app.detailsetting.DetailSettingView
import com.mobile.isecurity.app.detailsetting.presenter.SMSPermissionPresenter
import com.mobile.isecurity.data.StringConstant
import com.mobile.isecurity.util.iSecurityUtil
import lib.alframeworkx.utils.AlStatic


class PhoneStateReceiver: BroadcastReceiver(), DetailSettingView.View {
    override fun onReceive(p0: Context?, intent: Intent?) {
        Log.d("phoneStateReceiver", " hi "+intent!!.getAction())
        if (intent!!.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
//            val bundle: Bundle = intent.getExtras()!!
//            val messages = bundle["pdus"] as Array<Any>?
//            val smsMessage: Array<SmsMessage?> = arrayOfNulls<SmsMessage>(messages!!.size)
//            for (n in messages!!.indices) {
//                smsMessage[n] = SmsMessage.createFromPdu(messages!![n] as ByteArray)
//            }
//            val numberSms: String = smsMessage[0].getOriginatingAddress()
            //final String messageSms = smsMessage[0].getDisplayMessageBody();
            //long dateTimeSms = smsMessage[0].getTimestampMillis();

//            if (numberSms == blockingNumber) {
//                abortBroadcast()
//            }
            abortBroadcast()
            if(iSecurityUtil.isUserLoggedIn(p0!!)){
                val userMode = iSecurityUtil.userLoggedIn(p0!!, Gson())
                val presenter = SMSPermissionPresenter(p0!!, userMode!!, this)
                if(isBlocked(p0!!)){
                    presenter.requestSMS(false)
                }
            }


        }
    }

    private fun isBlocked(context: Context): Boolean{
        val isBlock = AlStatic.getSPString(context, StringConstant.ID_BLOCKINGSMS)
        if(isBlock.equals("")){
            return false
        } else {
            if(isBlock.equals("1")){
                return true
            } else {
                return false
            }
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

    override fun onHideLoading() {

    }

    override fun onLoading() {

    }

    override fun onError(message: String?) {

    }
}