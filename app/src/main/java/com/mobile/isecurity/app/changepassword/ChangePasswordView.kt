package com.mobile.isecurity.app.changepassword

import com.mobile.isecurity.data.model.UserModel
import lib.alframeworkx.base.BaseView

interface ChangePasswordView {
    interface View: BaseView{
        fun onSuccessChangePassword(message: String)
    }
    interface Presenter{
        fun changePassword(userModel: UserModel, oldpassword: String, newpass: String)
    }
}