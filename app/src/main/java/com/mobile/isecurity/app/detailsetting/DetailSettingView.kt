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

    }
    interface View: BaseView{
        fun onRequestNewLocation(message: String)
    }

    interface Presenter{
        fun setAccessPermission(access: String)
    }
}