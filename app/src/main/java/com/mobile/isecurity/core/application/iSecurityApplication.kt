package com.mobile.isecurity.core.application

import com.google.gson.Gson
import com.mobile.isecurity.data.StringConstant
import com.mobile.isecurity.data.model.UserModel
import lib.alframeworkx.SuperUser.RequestHandler
import lib.alframeworkx.utils.AlStatic

class iSecurityApplication: RequestHandler() {
    val gson = Gson()

    override fun onCreate() {
        super.onCreate()

    }

}