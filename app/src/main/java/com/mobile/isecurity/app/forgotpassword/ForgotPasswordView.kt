package com.mobile.isecurity.app.forgotpassword

import lib.alframeworkx.base.BaseView

interface ForgotPasswordView {
    interface View: BaseView{
        fun onSuccessSendEmail(message: String)
    }
    interface Presenter {
        fun sendEmail(email: String)
    }
}