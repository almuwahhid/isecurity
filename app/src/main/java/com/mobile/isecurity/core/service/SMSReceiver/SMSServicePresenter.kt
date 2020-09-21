package com.mobile.isecurity.core.service.SMSReceiver

import android.content.Context
import com.mobile.isecurity.app.detailsetting.DetailSettingView
import com.mobile.isecurity.app.detailsetting.presenter.DetailSettingPresenter
import com.mobile.isecurity.data.Api
import com.mobile.isecurity.data.DataConstant
import com.mobile.isecurity.data.model.SMS.SMSModel
import com.mobile.isecurity.data.model.UserModel
import lib.alframeworkx.base.BasePresenter
import lib.alframeworkx.utils.AlRequest
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class SMSServicePresenter(context: Context, userModel: UserModel): BasePresenter(context) {

    var userModel: UserModel

    init {
        this.userModel = userModel
    }

    fun requestSMS(smsModel: SMSModel){
        AlRequest.POST(Api.send_one_sms(), context, object : AlRequest.OnPostRequest{
            override fun onSuccess(response: JSONObject?) {
                try {
                    if (response!!.getString("status").equals("ok")) {

                    } else {

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(error: String?) {

            }

            override fun onPreExecuted() {

            }

            override fun requestParam(): MutableMap<String, String> {
                val param = DataConstant.headerRequest()
                param["token"] = userModel.token
                param["phone_number"] = ""+smsModel.phone_number
                param["messages"] = ""+smsModel.messages
                param["time"] = ""+smsModel.time
                param["folder_name"] = "inbox"
                param["read_state"] = "delivered"
                return param
            }

            override fun requestHeaders(): MutableMap<String, String> {
                val param = HashMap<String, String>()
                param["token"] = userModel.token
//                        param["Content-Type"] = DataConstant.CONTENT_TYPE
                return param
            }

        })
    }
}