package com.mobile.isecurity.app.login

import android.content.Intent
import android.os.Bundle
import com.google.gson.Gson
import com.mobile.isecurity.R
import com.mobile.isecurity.app.main.MainActivity
import com.mobile.isecurity.app.register.RegisterActivity
import com.mobile.isecurity.core.application.iSecurityActivity
import com.mobile.isecurity.core.application.iSecurityApplication
import com.mobile.isecurity.data.StringConstant
import com.mobile.isecurity.data.model.UserModel
import com.mobile.isecurity.util.iSecurityUtil
import kotlinx.android.synthetic.main.activity_login.*
import lib.alframeworkx.Activity.ActivityGeneral
import lib.alframeworkx.utils.AlStatic

class LoginActivity : iSecurityActivity(), LoginView.View {

    val gson = Gson()
    lateinit var presenter : LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        presenter = LoginPresenter(context, this)

        if(iSecurityUtil.isUserLoggedIn(context)){
           finish()
           startActivity(Intent(context, MainActivity::class.java))
        }

        btn_login.setOnClickListener({
            if(edt_username.text.toString().equals("")){
                edt_username.setError("Email is Required")
            } else if(edt_password.text.toString().equals("")){
                edt_password.setError("Password is Required")
            } else {
                presenter!!.requestLogin(edt_username.text.toString(), edt_password.text.toString())
            }
        })
        btn_register.setOnClickListener({
            startActivity(Intent(context, RegisterActivity::class.java))
        })
    }

    override fun onSuccessLogin(userModel: UserModel, message : String) {
        AlStatic.setSPString(context, StringConstant.LOGIN_SP, gson.toJson(userModel))
        finish()
        startActivity(Intent(context, MainActivity::class.java))
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
