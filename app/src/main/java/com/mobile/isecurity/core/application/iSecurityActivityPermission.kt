package com.mobile.isecurity.core.application

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.util.Log
import com.mobile.isecurity.core.service.ActiveState.ActiveStateService
import com.mobile.isecurity.data.StringConstant
import com.mobile.isecurity.util.iSecurityUtil
import lib.alframeworkx.Activity.ActivityPermission
import java.util.*

open class iSecurityActivityPermission : ActivityPermission() {

    val TAG = iSecurityActivityPermission::class.java.name
    var filter_core: IntentFilter? = null
    var online_state = true

    var timer_status: Thread? = null

    private var mHandler: Handler? = null
    var mStatusChecker: Runnable? = null

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
//        executor.scheduleAtFixedRate(periodicTask, 1, 2, TimeUnit.SECONDS);
//        timer = Timer()
//        tasknew = object: TimerTask(){
//            override fun run() {
//                Log.d("iSecurity", "Fuck yu")
//                if(online_state){
//                    sendBroadcast(Intent(StringConstant.UPDATE_STATE))
//                } else {
//                    timer!!.cancel()
//                }
//            }
//        }

//        timer!!.scheduleAtFixedRate(tasknew, 0, 100)
//        initTimerStatus()
    }


    open fun startRepeatingTask() {
        mStatusChecker!!.run()
    }

    open fun stopRepeatingTask() {
        mHandler!!.removeCallbacks(mStatusChecker)
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
            online_state = true

            if(mStatusChecker == null){
                mHandler = Handler()
                mStatusChecker = object : Runnable {
                    override fun run() {
                        try {
                            sendBroadcast(Intent(StringConstant.UPDATE_STATE))
                        } finally {
                            if(online_state)
                                mHandler!!.postDelayed(this, 1000)
                        }
                    }
                }
            }
            startRepeatingTask()

        }
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver_core)
        if(iSecurityUtil.isUserLoggedIn(context)) {
            online_state = false
            sendBroadcast(Intent(StringConstant.STATE_STOP))
            stopRepeatingTask()
        }
    }

    private fun initTimerStatus() {
        timer_status = object : Thread() {
            override fun run() {
                try {
                    //Create the database
                    sleep(2000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    sendBroadcast(Intent(StringConstant.UPDATE_STATE))
                    if(online_state)
                        runOnUiThread({
                            timer_status!!.start()
                        })
                }
            }
        }
    }
}