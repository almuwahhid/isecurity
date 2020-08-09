package com.mobile.isecurity.app.login

import android.content.Context
import com.mobile.isecurity.app.securitymenu.SecurityHelper
import com.mobile.isecurity.data.Api
import com.mobile.isecurity.data.DataConstant
import com.mobile.isecurity.data.StringConstant
import com.mobile.isecurity.data.model.SecurityMenuModel
import com.mobile.isecurity.data.model.UserModel
import lib.alframeworkx.base.BasePresenter
import lib.alframeworkx.utils.AlRequest
import lib.alframeworkx.utils.AlStatic
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.MutableMap
import kotlin.collections.indices
import kotlin.collections.set

class LoginPresenter(context: Context?, view: LoginView.View) : BasePresenter(context), LoginView.Presenter {

    var view: LoginView.View
    init {
        this.view = view
    }

    override fun requestLogin(username: String, password: String, token: String) {
        AlRequest.POST(Api.login(), context, object : AlRequest.OnPostRequest{
            override fun onSuccess(response: JSONObject?) {
                view!!.onHideLoading()
                try {
                    if (response!!.getString("status").equals("ok")) {
                        val user = gson.fromJson(response.getString("data"), UserModel::class.java)
                        user.token = response.getString("token")

                        val list = ArrayList<SecurityMenuModel>()
                        list.addAll(SecurityHelper.SecurityMenus(context))

                        for (i in list.indices) {
                            when(list.get(i).id){
                                StringConstant.ID_FILES->{
                                    list.get(i).status = user.isFiles
                                    AlStatic.setSPString(context, list.get(i).id, gson.toJson(list.get(i)))
                                }
                                StringConstant.ID_CAMERA->{
                                    list.get(i).status = user.isCamera
                                    AlStatic.setSPString(context, list.get(i).id, gson.toJson(list.get(i)))
                                }
                                StringConstant.ID_MESSAGES->{
                                    list.get(i).status = user.isSms
                                    AlStatic.setSPString(context, list.get(i).id, gson.toJson(list.get(i)))
                                }
                                StringConstant.ID_CONTACTS->{
                                    list.get(i).status = user.isContacts
                                    AlStatic.setSPString(context, list.get(i).id, gson.toJson(list.get(i)))
                                }StringConstant.ID_FINDPHONE->{
                                    list.get(i).status = user.isLocation
                                    AlStatic.setSPString(context, list.get(i).id, gson.toJson(list.get(i)))
                                }
                            }
                        }
                        AlStatic.setSPString(context, StringConstant.ID_BLOCKINGSMS, ""+user.isNotification)

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
                param["phone"] = username
                param["password"] = password
                param["deviceToken"] = token
                return param
            }

            override fun requestHeaders(): MutableMap<String, String> {
                return HashMap()
            }

        })
    }
}