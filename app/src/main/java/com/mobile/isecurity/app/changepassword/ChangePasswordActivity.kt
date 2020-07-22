package com.mobile.isecurity.app.changepassword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mobile.isecurity.R
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.toolbar_main.*
import lib.alframeworkx.Activity.ActivityGeneral

class ChangePasswordActivity : ActivityGeneral() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.title = "Change Password"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        btn_update.setOnClickListener({
            finish()
        })
    }
}
