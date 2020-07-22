package com.mobile.isecurity.app.splashscreen

import android.content.Intent
import android.os.Bundle
import com.mobile.isecurity.R
import com.mobile.isecurity.app.login.LoginActivity
import com.mobile.isecurity.app.main.MainActivity
import lib.alframeworkx.Activity.ActivityPermission

class SplashScreenActivity : ActivityPermission() {

    var timer: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        initTimer()
        timer!!.start()
    }

    private fun initTimer() {
        timer = object : Thread() {
            override fun run() {
                try {
                    //Create the database
                    sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    startActivity(Intent(context, LoginActivity::class.java))
                    finish()
                }
            }
        }
    }
}
