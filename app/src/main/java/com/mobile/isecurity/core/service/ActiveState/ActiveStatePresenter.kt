package com.mobile.isecurity.core.service.ActiveState

import android.content.Context
import com.mobile.isecurity.data.Api
import com.mobile.isecurity.data.DataConstant
import com.mobile.isecurity.data.StringConstant
import com.mobile.isecurity.data.model.UserModel
import lib.alframeworkx.base.BasePresenter
import lib.alframeworkx.utils.AlRequest
import lib.alframeworkx.utils.AlStatic
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class ActiveStatePresenter(context: Context, userModel: UserModel, view: ActiveStateView.View) : BasePresenter(context), ActiveStateView.Presenter {
    var view: ActiveStateView.View
    var userModel: UserModel

    init {
        this.view = view
        this.userModel = userModel
    }

    override fun updateActiveState(state: Int) {
        AlRequest.POST(Api.update_access_permission(), context, object : AlRequest.OnPostRequest{
            override fun onSuccess(response: JSONObject?) {
                try {
                    if (response!!.getString("status").equals("ok")) {
                        AlStatic.setSPString(context, StringConstant.ID_ACTIVESTATE, ""+state)
//                        view!!.onUpdateActiveState(state)
                    }
                    view!!.onUpdateActiveState(state)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    view!!.onUpdateActiveState(state)
                }
            }

            override fun onFailure(error: String?) {
                view!!.onUpdateActiveState(state)
            }

            override fun onPreExecuted() {

            }

            override fun requestParam(): MutableMap<String, String> {
                val param = DataConstant.headerRequest()
                param["isActive"] = ""+state
                return param
            }

            override fun requestHeaders(): MutableMap<String, String> {
                val param = HashMap<String, String>()
                param["token"] = userModel.token
//                param["Content-Type"] = DataConstant.CONTENT_TYPE
                return param
            }
        })
    }
}