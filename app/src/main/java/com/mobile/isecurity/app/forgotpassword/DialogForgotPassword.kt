package com.mobile.isecurity.app.forgotpassword

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import com.mobile.isecurity.R
import kotlinx.android.synthetic.main.dialog_forgot_password.*
import lib.alframeworkx.utils.AlStatic
import lib.alframeworkx.utils.DialogBuilder

class DialogForgotPassword(context: Context) : DialogBuilder(context, R.layout.dialog_forgot_password), ForgotPasswordView.View {

    var presenter: ForgotPasswordPresenter

    init {
        setAnimation(R.style.DialogBottomAnimation)
        setFullWidth(dialog.lay_dialog)
        setGravity(Gravity.BOTTOM)

        presenter = ForgotPasswordPresenter(context, this)

        with(dialog){
            btn_send.setOnClickListener({
                if(edt_email.text.toString().equals("")){
                    edt_email.setError("Field Required")
                } else {
                    presenter.sendEmail(edt_email.text.toString())
                }
            })
        }
        show()
    }


    override fun onSuccessSendEmail(message: String) {
        AlStatic.ToastShort(context, message)
        dismiss()
    }

    override fun onHideLoading() {
        AlStatic.hideLoadingDialog(context)
    }

    override fun onLoading() {
        AlStatic.showLoadingDialog(context, R.drawable.ic_logo)
    }

    override fun onError(message: String?) {
        AlStatic.ToastShort(context, message)
    }
}
