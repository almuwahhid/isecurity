package com.mobile.isecurity.core.application

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import com.mobile.isecurity.core.service.ActiveState.ActiveStateService
import com.mobile.isecurity.data.StringConstant
import com.mobile.isecurity.util.iSecurityUtil
import lib.alframeworkx.Activity.ActivityPermission

open class iSecurityActivityPermission : ActivityPermission() {

    val TAG = iSecurityActivityPermission::class.java.name
    var filter_core: IntentFilter? = null

    private val receiver_core: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action){
                StringConstant.CHECK_STATE_ISTOP -> {
                    Log.d(TAG, "huh")
                    sendBroadcast(Intent(StringConstant.STATE_ACTIVE))
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        filter_core = IntentFilter()
        filter_core!!.addAction(StringConstant.CHECK_STATE_ISTOP)
    }

    protected open fun getActivity(): Activity? {
        return this
    }

    override fun onResume() {
        super.onResume()
        filter_core = IntentFilter()
        filter_core!!.addAction(StringConstant.CHECK_STATE_ISTOP)
        registerReceiver(receiver_core, filter_core)
        if(iSecurityUtil.isUserLoggedIn(context)){

            if (!iSecurityUtil.isServiceRunning(context, ActiveStateService::class.java)) {
                stopService(Intent(context, ActiveStateService::class.java))
                startService(Intent(context, ActiveStateService::class.java))
            } else {
                sendBroadcast(Intent(StringConstant.STATE_ACTIVE))
            }
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver_core)
        if(iSecurityUtil.isUserLoggedIn(context)) {

            sendBroadcast(Intent(StringConstant.STATE_STOP))
        }
    }
}