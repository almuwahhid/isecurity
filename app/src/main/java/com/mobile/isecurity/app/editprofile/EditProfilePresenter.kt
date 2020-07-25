package com.mobile.isecurity.app.editprofile

import android.content.Context
import com.mobile.isecurity.data.Api
import com.mobile.isecurity.data.model.UserModel
import lib.alframeworkx.base.BasePresenter
import lib.alframeworkx.utils.AlRequest
import lib.alframeworkx.utils.VolleyMultipartRequest
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class EditProfilePresenter(context: Context?, view: EditProfileView.View) : BasePresenter(context), EditProfileView.Presenter {

    var view: EditProfileView.View
    init {
        this.view = view
    }

    override fun sendUpdateData(token: String, params: HashMap<String, String>, data: VolleyMultipartRequest.DataPart?) {
        if(data == null){
            AlRequest.POST(Api.update_profile(), context, object : AlRequest.OnPostRequest{
                override fun onSuccess(response: JSONObject?) {
                    view.onHideLoading()
                    try {
                        if (response!!.getString("status").equals("ok")) {
                            val user = gson.fromJson(response.getString("data"), UserModel::class.java)
                            view!!.onSuccessEditProfile(user, response.getString("message"))
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
                    return params
                }

                override fun requestHeaders(): MutableMap<String, String> {
                    val param = HashMap<String, String>()
                    param["token"] = token
                    return param
                }

            })
        } else {
            AlRequest.POSTMultipart(Api.update_profile(), context, object : AlRequest.OnMultipartRequest{
                override fun requestData(): MutableMap<String, VolleyMultipartRequest.DataPart> {
                    val params = HashMap<String, VolleyMultipartRequest.DataPart>()
                    params["profileImage"] = data
                    return params
                }

                override fun onPreExecuted() {
                    view.onLoading()
                }

                override fun onSuccess(response: JSONObject?) {
                    view.onHideLoading()
                    try {
                        if (response!!.getString("status").equals("ok")) {
                            val user = gson.fromJson(response.getString("data"), UserModel::class.java)
                            view!!.onSuccessEditProfile(user, response.getString("message"))
                        } else {
                            view!!.onError(response.getString("message"))
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }

                override fun onFailure(error: String?) {
                    view.onHideLoading()
                    view!!.onError(error)
                }

                override fun requestParam(): MutableMap<String, String> {
                    return params
                }

                override fun requestHeaders(): MutableMap<String, String> {
                    val param = HashMap<String, String>()
                    param["token"] = token
                    return param
                }

            })
        }
    }
}