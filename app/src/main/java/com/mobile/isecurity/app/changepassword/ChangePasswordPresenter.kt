package com.mobile.isecurity.app.changepassword

import android.content.Context
import com.mobile.isecurity.data.Api
import com.mobile.isecurity.data.DataConstant
import com.mobile.isecurity.data.model.UserModel
import lib.alframeworkx.base.BasePresenter
import lib.alframeworkx.utils.AlRequest
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class ChangePasswordPresenter(context: Context?, view: ChangePasswordView.View) : BasePresenter(context), ChangePasswordView.Presenter {

    var view: ChangePasswordView.View
    init {
        this.view = view
    }

    override fun changePassword(userModel: UserModel, oldpassword: String, newpass: String ) {
        AlRequest.POST(Api.change_password(), context, object : AlRequest.OnPostRequest{
            override fun onSuccess(response: JSONObject?) {
                view.onHideLoading()
                try {
                    if (response!!.getString("status").equals("ok")) {
                        view!!.onSuccessChangePassword(response.getString("message"))
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
                param["oldPass"] = oldpassword
                param["newPass"] = newpass
                return param
            }

            override fun requestHeaders(): MutableMap<String, String> {
                val param = HashMap<String, String>()
                param["token"] = userModel.token
                return param
            }

        })
    }

}