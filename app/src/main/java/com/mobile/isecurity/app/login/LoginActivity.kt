package com.mobile.isecurity.app.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.mobile.isecurity.R
import com.mobile.isecurity.app.forgotpassword.DialogForgotPassword
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
    var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        presenter = LoginPresenter(context, this)


        if(iSecurityUtil.isUserLoggedIn(context)){
           finish()
           startActivity(Intent(context, MainActivity::class.java))
        }

        tv_forgotpassword.setOnClickListener({
            DialogForgotPassword(context)
        })

        btn_login.setOnClickListener({
            if(edt_phone.text.toString().equals("")){
                edt_phone.setError("Email is Required")
            } else if(edt_password.text.toString().equals("")){
                edt_password.setError("Password is Required")
            } else {
                FirebaseApp.initializeApp(context)
                FirebaseInstanceId.getInstance().instanceId
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.d(LoginActivity::class.java.name, "getInstanceId failed", task.exception)
                            return@OnCompleteListener
                        }
                        token = task.result?.token!!
                        presenter!!.requestLogin(edt_phone.text.toString(), edt_password.text.toString(), token)

                        // Log and toast
//                val msg = getString(R.string.msg_token_fmt, token)
//                Log.d(TAG, msg)
//                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    })

            }
        })
        btn_register.setOnClickListener({
            startActivity(Intent(context, RegisterActivity::class.java))
        })
    }

    override fun onSuccessLogin(userModel: UserModel, message : String) {
        AlStatic.ToastShort(context, message)
        userModel.firebaseToken = token
        Log.d(LoginActivity::class.java.name, "getInstanceId success"+ token)
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
