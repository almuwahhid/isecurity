package com.mobile.isecurity.app.login

import android.content.Context
import com.mobile.isecurity.data.Api
import com.mobile.isecurity.data.DataConstant
import com.mobile.isecurity.data.model.UserModel
import com.mobile.isecurity.util.iSecurityUtil
import lib.alframeworkx.base.BasePresenter
import lib.alframeworkx.utils.AlRequest
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class LoginPresenter(context: Context?, view: LoginView.View) : BasePresenter(context), LoginView.Presenter {

    var view: LoginView.View
    init {
        this.view = view
    }

    override fun requestLogin(username: String, password: String) {
        AlRequest.POST(Api.login(), context, object : AlRequest.OnPostRequest{
            override fun onSuccess(response: JSONObject?) {
                view!!.onHideLoading()
                try {
                    if (response!!.getString("status").equals("ok")) {
                        val user = gson.fromJson(response.getString("data"), UserModel::class.java)
                        user.token = response.getString("token")
                        view!!.onSuccessLogin(user, response.getString("message"))
                    } else {
                        view!!.onError(response.getString("message"))
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    view!!.onError(response!!.getString("message"))
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
                param["email"] = username
                param["password"] = password
                return param
            }

            override fun requestHeaders(): MutableMap<String, String> {
                return HashMap()
            }

        })
    }
}