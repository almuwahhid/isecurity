package com.mobile.isecurity.app.changepassword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import com.mobile.isecurity.R
import com.mobile.isecurity.core.application.iSecurityActivity
import com.mobile.isecurity.data.model.UserModel
import com.mobile.isecurity.util.iSecurityUtil
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.toolbar_main.*
import lib.alframeworkx.Activity.ActivityGeneral
import lib.alframeworkx.utils.AlStatic
import java.util.ArrayList

class ChangePasswordActivity : iSecurityActivity(), ChangePasswordView.View {

    lateinit var presenter: ChangePasswordPresenter
    lateinit var userModel: UserModel
    val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.title = "Change Password"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        presenter = ChangePasswordPresenter(context, this)
        userModel = iSecurityUtil.userLoggedIn(context!!, gson)!!

        setFormsToValidate()

        btn_update.setOnClickListener({
            if (AlStatic.isFormValid(this, window.decorView, forms, "Field Required")) {
                if(edt_newpassword.text.toString().equals(edt_confirm.text.toString())){
                    presenter.changePassword(userModel, edt_oldpassword.text.toString(), edt_newpassword.text.toString())
                } else {
                    edt_confirm.setError("Rewrite your new password")
                }
            }
        })
    }

    internal var forms: ArrayList<Int> = ArrayList()
    private fun setFormsToValidate() {
        forms.add(R.id.edt_oldpassword)
        forms.add(R.id.edt_newpassword)
        forms.add(R.id.edt_confirm)
    }

    override fun onSuccessChangePassword(message: String) {
        AlStatic.ToastShort(context, message)
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
