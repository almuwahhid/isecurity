package com.mobile.isecurity.app.login

import com.mobile.isecurity.data.model.UserModel
import lib.alframeworkx.base.BaseView

interface LoginView {
    interface View: BaseView{
        fun onSuccessLogin(userModel: UserModel, message: String)
    }

    interface Presenter{
        fun requestLogin(username: String, password : String)
    }
}