package com.mobile.isecurity.core.service.ActiveState

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import com.google.gson.Gson
import com.mobile.isecurity.core.service.MainService
import com.mobile.isecurity.data.StringConstant
import com.mobile.isecurity.data.model.UserModel
import com.mobile.isecurity.util.iSecurityUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import java.util.concurrent.TimeUnit

class ActiveStateService : Service(), ActiveStateView.View{

    val TAG = ActiveStateService::class.java.name
    lateinit var presenter: ActiveStatePresenter

    lateinit var userModel: UserModel
    var filter: IntentFilter? = null

    private var serviceIsRunning = true

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "huh "+intent.action)
            when(intent.action){
                StringConstant.STATE_STOP -> {
                    Observable.interval(2, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .take(2)
                        .subscribeWith(object : DisposableObserver<Long?>() {
                            override fun onNext(aLong: Long) {
                                if (!serviceIsRunning) {
                                    presenter.updateActiveState(0)
                                } else {
                                    serviceIsRunning = false
                                    applicationContext.sendBroadcast(Intent(StringConstant.CHECK_STATE_ISTOP))
                                }
                            }

                            override fun onError(e: Throwable) {

                            }
                            override fun onComplete() {

                            }
                        })
                }

                StringConstant.STATE_ACTIVE -> {
                    serviceIsRunning = true
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        if(iSecurityUtil.isUserLoggedIn(this)){
            Log.d(TAG, "huh create")
            filter = IntentFilter()
            filter!!.addAction(StringConstant.STATE_STOP)
            filter!!.addAction(StringConstant.STATE_ACTIVE)

            registerReceiver(receiver, filter)
            userModel = iSecurityUtil.userLoggedIn(this, Gson())!!
            presenter = ActiveStatePresenter(this, userModel, this)

            presenter.updateActiveState(1)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
//        return START_NOT_STICKY;
    }

    override fun onDestroy() {
        super.onDestroy()
        if(iSecurityUtil.isUserLoggedIn(this)){
            Log.d(TAG, "huh destroy")
            unregisterReceiver(receiver)
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onUpdateActiveState(state : Int) {
        if(state == 0){
            stopSelf()
        }
    }
}