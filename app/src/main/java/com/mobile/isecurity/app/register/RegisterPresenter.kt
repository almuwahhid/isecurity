package com.mobile.isecurity.app.register

import android.content.Context
import com.mobile.isecurity.data.Api
import com.mobile.isecurity.data.DataConstant
import lib.alframeworkx.base.BasePresenter
import lib.alframeworkx.utils.AlRequest
import lib.alframeworkx.utils.VolleyMultipartRequest
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class RegisterPresenter(context: Context?, view: RegisterView.View) : BasePresenter(context), RegisterView.Presenter {

    var view: RegisterView.View
    init {
        this.view = view
    }

    override fun sendRegisterData(params: HashMap<String, String>, data: VolleyMultipartRequest.DataPart?) {
        if(data == null){
            AlRequest.POST(Api.register(), context, object : AlRequest.OnPostRequest{
                override fun onSuccess(response: JSONObject?) {
                    view.onHideLoading()
                    try {
                        if (response!!.getString("status").equals("ok")) {
                            view!!.onSuccessRegister(response.getString("message"))
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
                    val headers = HashMap<String, String>()
//                    headers["Content-Type"] = DataConstant.CONTENT_TYPE
                    return headers
                }

            })
        } else {
            AlRequest.POSTMultipart(Api.register(), context, object : AlRequest.OnMultipartRequest{
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
                            view!!.onSuccessRegister(response.getString("message"))
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
                    val headers = HashMap<String, String>()
//                    headers["Content-Type"] = DataConstant.CONTENT_TYPE
                    return headers
                }

            })
        }
    }
}