package com.mobile.isecurity.app.detailsetting

import com.mobile.isecurity.data.model.SecurityMenuModel
import lib.alframeworkx.base.BaseView

interface DetailSettingView {
    interface PresenterLocation{
        fun requestNewLocation(isLoadingShown: Boolean)
    }
    interface PresenterFiles{
        fun requestFilesUpdate(isLoadingShown: Boolean)
        fun setAccessPermission(access: String, securityMenuModel: SecurityMenuModel)
    }
    interface PresenterCamera{

    }
    interface PresenterContact{
        fun requestContact(isLoadingShown: Boolean)
    }
    interface PresenterSMS{
        fun requestSMS(isLoadingShown: Boolean)
        fun requestBlocking(access: String)
    }
    interface View: BaseView{
        fun onRequestNewLocation(isSuccess: Boolean, message: String)
        fun onRequestNewSMS(isSuccess: Boolean, message: String)
        fun onRequestBlockingSMS(isSuccess: Boolean, message: String)
        fun onRequestNewContact(isSuccess: Boolean, message: String)
        fun onRequestNewFiles(isSuccess: Boolean, message: String)
        fun onRequestUpdateCameraPermission(isSuccess: Boolean, message: String)
        fun onCheckFileUploadStatus()
    }

    interface Presenter{
        fun setAccessPermission(access: String)
    }
}