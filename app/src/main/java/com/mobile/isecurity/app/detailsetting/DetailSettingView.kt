package com.mobile.isecurity.app.detailsetting

import lib.alframeworkx.base.BaseView

interface DetailSettingView {
    interface PresenterLocation{
        fun requestNewLocation(isLoadingShown: Boolean)
    }
    interface PresenterFiles{

    }
    interface PresenterCamera{

    }
    interface PresenterSMS{
        fun requestSMS(isLoadingShown: Boolean)
    }
    interface View: BaseView{
        fun onRequestNewLocation(message: String)
        fun onRequestNewSMS(message: String)
    }

    interface Presenter{
        fun setAccessPermission(access: String)
    }
}