package com.mobile.isecurity.core.service.SMSManager

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import com.google.gson.Gson
import com.mobile.isecurity.core.service.ActiveState.ActiveStatePresenter
import com.mobile.isecurity.data.StringConstant
import com.mobile.isecurity.util.iSecurityUtil

class SMSManagerService : Service(), SMSManagerView.View {
    lateinit var presenter : SMSManagerPresenter
    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        presenter = SMSManagerPresenter(baseContext, this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent!!.getStringExtra("data")
        return super.onStartCommand(intent, flags, startId)
    }
}