package com.mobile.isecurity.app.login

import android.content.Intent
import android.os.Bundle
import com.mobile.isecurity.R
import com.mobile.isecurity.app.main.MainActivity
import com.mobile.isecurity.app.register.RegisterActivity
import kotlinx.android.synthetic.main.activity_login.*
import lib.alframeworkx.Activity.ActivityGeneral

class LoginActivity : ActivityGeneral() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_login.setOnClickListener({
            startActivity(Intent(context, MainActivity::class.java))
            finish()
        })
        btn_register.setOnClickListener({
            startActivity(Intent(context, RegisterActivity::class.java))
        })
    }
}
