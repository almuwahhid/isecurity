package com.mobile.isecurity.app.forgotpassword

import android.content.Context
import com.mobile.isecurity.data.Api
import com.mobile.isecurity.data.DataConstant
import lib.alframeworkx.base.BasePresenter
import lib.alframeworkx.utils.AlRequest
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class ForgotPasswordPresenter(context: Context?, view: ForgotPasswordView.View) : BasePresenter(context), ForgotPasswordView.Presenter {

    var view: ForgotPasswordView.View
    init {
        this.view = view
    }

    override fun sendEmail(email: String) {
        AlRequest.POST(Api.forgot_password(), context, object : AlRequest.OnPostRequest{
            override fun onSuccess(response: JSONObject?) {
                view.onHideLoading()
                try {
                    if (response!!.getString("status").equals("200")) {
                        view!!.onSuccessSendEmail(response.getString("message"))
                    } else {
                        view!!.onError(response.getString("message"))
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(error: String?) {
                view!!.onHideLoading()
                view!!.onError(error)
            }

            override fun onPreExecuted() {
                view!!.onLoading()
            }

            override fun requestParam(): MutableMap<String, String> {
                val param = DataConstant.headerRequest()
                param["email"] = email
                return param
            }

            override fun requestHeaders(): MutableMap<String, String> {
                return HashMap()
            }

        })
    }

}