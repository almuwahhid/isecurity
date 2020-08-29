package com.mobile.isecurity.data

import com.mobile.isecurity.BuildConfig

class StringConstant {
    companion object{
        const val ID_CAMERA = BuildConfig.APPLICATION_ID + "_CAMERA"
        const val ID_FILES = BuildConfig.APPLICATION_ID + "_FILES"
        const val ID_MESSAGES = BuildConfig.APPLICATION_ID + "_MESSAGES"
        const val ID_CONTACTS = BuildConfig.APPLICATION_ID + "_CONTACTS"
        const val ID_FINDPHONE = BuildConfig.APPLICATION_ID + "_FINDPHONE"
        const val ID_BLOCKINGSMS = BuildConfig.APPLICATION_ID + "_BLOCKINGSMS"
        const val ID_ACTIVESTATE = BuildConfig.APPLICATION_ID + "_ACTIVESTATE"
        const val ID_MONITORINGFILES = BuildConfig.APPLICATION_ID + "_MONITORINGFILES"

        const val LOGIN_SP = BuildConfig.APPLICATION_ID + "_LOGIN_SP_USER"
        const val SOCKETCONNECTED_SP = BuildConfig.APPLICATION_ID + "_SOCKET_CONNECTED_SP"

        const val STATE_STOP = BuildConfig.APPLICATION_ID + "_stopstate"
        const val UPDATE_STATE = BuildConfig.APPLICATION_ID + "_updatestate"
        const val STATE_ACTIVE = BuildConfig.APPLICATION_ID + "_activestate"
        const val CHECK_STATE_ISTOP = BuildConfig.APPLICATION_ID + "_checkstateisstop"

        const val UPLOADING_FILE_STATUS = BuildConfig.APPLICATION_ID + "_uploading_file_status"
    }
}