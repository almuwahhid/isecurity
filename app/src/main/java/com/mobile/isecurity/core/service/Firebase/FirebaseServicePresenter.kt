package com.mobile.isecurity.core.service.Firebase

import android.content.Context
import android.net.Uri
import android.os.Environment
import com.mobile.isecurity.data.Api
import com.mobile.isecurity.data.DataConstant
import com.mobile.isecurity.data.model.UserModel
import com.mobile.isecurity.util.iSecurityUtil
import lib.alframeworkx.base.BasePresenter
import lib.alframeworkx.utils.AlRequest
import lib.alframeworkx.utils.VolleyMultipartRequest
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class FirebaseServicePresenter(context: Context) : BasePresenter(context) {

    var userModel: UserModel? = null

    fun requestFile(path: String){
        userModel = iSecurityUtil.userLoggedIn(context, gson)!!
        AlRequest.POSTMultipart(Api.upload_files(), context, object : AlRequest.OnMultipartRequest{
            override fun requestData(): MutableMap<String, VolleyMultipartRequest.DataPart> {
                val params = HashMap<String, VolleyMultipartRequest.DataPart>()
                params["file"] = getFileParam(Uri.parse(path))
                return params
            }

            override fun onPreExecuted() {

            }

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

            override fun requestParam(): MutableMap<String, String> {
                val param = DataConstant.headerRequest()
                param["directory"] = path
                return param
            }

            override fun requestHeaders(): MutableMap<String, String> {
                val param = HashMap<String, String>()
                param["token"] = userModel!!.token
                return param
            }

        })
    }

    private fun getFileParam(file_uri: Uri) : VolleyMultipartRequest.DataPart{
        return VolleyMultipartRequest.DataPart(file_uri!!.path, iSecurityUtil.getBytesFile(context, file_uri), iSecurityUtil.getTypeFile(context, file_uri!!))
    }
}