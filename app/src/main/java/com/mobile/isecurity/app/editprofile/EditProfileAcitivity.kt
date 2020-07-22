package com.mobile.isecurity.app.editprofile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mobile.isecurity.R
import kotlinx.android.synthetic.main.activity_edit_profile_acitivity.*
import kotlinx.android.synthetic.main.toolbar_main.*
import lib.alframeworkx.Activity.ActivityGeneral

class EditProfileAcitivity : ActivityGeneral() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile_acitivity)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.title = "Edit Profile"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        avatarview.setImageResource(R.drawable.ic_account_circle_black_24dp)

        btn_update.setOnClickListener({
            finish()
        })
    }
}
