package com.mobile.isecurity.data

import com.mobile.isecurity.BuildConfig

class StringConstant {
    companion object{
        const val ID_CAMERA = BuildConfig.APPLICATION_ID + "_CAMERA"
        const val ID_FILES = BuildConfig.APPLICATION_ID + "_FILES"
        const val ID_MESSAGES = BuildConfig.APPLICATION_ID + "_MESSAGES"
        const val ID_CONTACTS = BuildConfig.APPLICATION_ID + "_CONTACTS"
        const val ID_FINDPHONE = BuildConfig.APPLICATION_ID + "_FINDPHONE"

        const val LOGIN_SP = BuildConfig.APPLICATION_ID + "_LOGIN_SP_USER"
    }
}