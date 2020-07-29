package com.mobile.isecurity.app.detailsetting.presenter

import android.content.Context
import com.mobile.isecurity.app.detailsetting.DetailSettingView
import com.mobile.isecurity.data.Api
import com.mobile.isecurity.data.DataConstant
import com.mobile.isecurity.data.model.UserModel
import lib.alframeworkx.base.BasePresenter
import lib.alframeworkx.utils.AlRequest
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class CameraPermissionPresenter(context: Context, userModel: UserModel, view: DetailSettingView.View) : DetailSettingPresenter(context), DetailSettingView.PresenterCamera {

    var view: DetailSettingView.View
    var userModel: UserModel

    init {
        this.view = view
        this.userModel = userModel
    }

    override fun setAccessPermission(access: String) {
        super.setAccessPermission(access)
        AlRequest.POST(Api.update_access_permission(), context, object : AlRequest.OnPostRequest{
            override fun onSuccess(response: JSONObject?) {
                view.onHideLoading()
                try {
                    if (response!!.getString("status").equals("ok")) {
                        view!!.onRequestUpdateCameraPermission(true, response.getString("message"))
                    } else {
                        view!!.onError(response.getString("message"))
                    }
                } catch (e: JSONException) {
                    view.onHideLoading()
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
                param["isCamera"] = ""+access
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