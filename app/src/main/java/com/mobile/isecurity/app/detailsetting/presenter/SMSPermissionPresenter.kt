package com.mobile.isecurity.app.detailsetting.presenter

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import com.mobile.isecurity.app.detailsetting.DetailSettingView
import com.mobile.isecurity.data.Api
import com.mobile.isecurity.data.DataConstant
import com.mobile.isecurity.data.model.SMS.SMSModel
import com.mobile.isecurity.data.model.UserModel
import lib.alframeworkx.base.BasePresenter
import lib.alframeworkx.utils.AlRequest
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class SMSPermissionPresenter(context: Context, userModel: UserModel, view: DetailSettingView.View) : DetailSettingPresenter(context), DetailSettingView.PresenterSMS {

    var view: DetailSettingView.View
    var userModel: UserModel

    init {
        this.view = view
        this.userModel = userModel
    }

    override fun requestSMS(isLoadingShown: Boolean) {
        SMSListRequest(context, object : OnAfterRequestSMS{
            override fun afterRequestSMS(result: MutableList<SMSModel>) {
                AlRequest.POST(Api.update_sms(), context, object : AlRequest.OnPostRequest{
                    override fun onSuccess(response: JSONObject?) {
                        view.onHideLoading()
                        try {
                            if (response!!.getString("status").equals("ok")) {
                                view!!.onRequestNewSMS(response.getString("message"))
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
                        if(isLoadingShown)
                            view!!.onLoading()
                    }

                    override fun requestParam(): MutableMap<String, String> {
                        val param = DataConstant.headerRequest()
                        param["messages"] = gson.toJson(result)
                        return param
                    }

                    override fun requestHeaders(): MutableMap<String, String> {
                        val param = HashMap<String, String>()
                        param["token"] = userModel.token
                        return param
                    }

                })
            }
        }).execute()
    }

    override fun setAccessPermission(access: String) {
        AlRequest.POST(Api.update_access_permission(), context, object : AlRequest.OnPostRequest{
            override fun onSuccess(response: JSONObject?) {
                try {
                    if (response!!.getString("status").equals("ok")) {
                        if(access.equals("1")){
                            requestSMS(false)
                        } else {
                            view.onHideLoading()
                            view!!.onRequestNewLocation(response.getString("message"))
                        }
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
                param["isSms"] = ""+access
                return param
            }

            override fun requestHeaders(): MutableMap<String, String> {
                val param = HashMap<String, String>()
                param["token"] = userModel.token
                return param
            }

        })
    }

    private class SMSListRequest(context: Context, onAfterRequestSMS : OnAfterRequestSMS) : AsyncTask<String, String, MutableList<SMSModel>>() {

        val onAfterRequestSMS : OnAfterRequestSMS
        val context : Context
        init {
            this.onAfterRequestSMS = onAfterRequestSMS
            this.context = context
        }

        override fun doInBackground(vararg p0: String?): MutableList<SMSModel> {
            var result: MutableList<SMSModel> = ArrayList()
            val uriSms: Uri = Uri.parse("content://sms")

            val cursor: Cursor = context.getContentResolver().query(
                uriSms, arrayOf(
                    "_id", "address", "date", "body",
                    "type", "read"
                ), null, null,
                "date" + " COLLATE LOCALIZED ASC"
            )!!
            if (cursor != null) {
                cursor.moveToLast()
                if (cursor.getCount() > 0) {
                    do {
                        val message =
                            SMSModel()
                        message.phone_number = cursor.getString(cursor.getColumnIndex("address"))
                        message.messages = cursor.getString(cursor.getColumnIndex("body"))
                        message.read_state = when(cursor.getString(cursor.getColumnIndex("read"))){
                            "1" -> SMSModel.STATE_READ
                            "0" -> SMSModel.STATE_DELIVERED
                            else -> SMSModel.STATE_PENDING
                        }
                        message.folder_name = when(cursor.getString(cursor.getColumnIndex("type"))){
                            "1" -> SMSModel.FOLDER_INBOX
                            "2" -> SMSModel.FOLDER_OUTBOX
                            else -> SMSModel.FOLDER_INBOX
                        }
                        message.time = cursor.getString(cursor.getColumnIndex("date"))
                        result.add(message)
                    } while (cursor.moveToPrevious())
                }
            }
            cursor.close()
            return result
        }

        override fun onPostExecute(result: MutableList<SMSModel>?) {
            super.onPostExecute(result)
            onAfterRequestSMS.afterRequestSMS(result!!)
        }
    }

    private interface OnAfterRequestSMS{
        fun afterRequestSMS(result: MutableList<SMSModel>)
    }
}