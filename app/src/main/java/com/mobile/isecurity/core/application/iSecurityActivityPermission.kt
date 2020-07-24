package com.mobile.isecurity.core.application

import android.app.Activity
import android.os.Bundle
import android.os.PersistableBundle
import lib.alframeworkx.Activity.ActivityPermission

open class iSecurityActivityPermission : ActivityPermission() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    protected open fun getActivity(): Activity? {
        return this
    }

//    val app = application as iSecurityApplication

}