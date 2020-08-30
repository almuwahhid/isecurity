package com.mobile.isecurity.core.service.Main

import android.content.Context
import android.net.Uri
import android.util.Log
import com.mobile.isecurity.data.Api
import com.mobile.isecurity.data.DataConstant
import com.mobile.isecurity.data.model.UserModel
import com.mobile.isecurity.util.iSecurityUtil
import lib.alframeworkx.base.BasePresenter
import lib.alframeworkx.utils.AlRequest
import lib.alframeworkx.utils.VolleyMultipartRequest
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.HashMap

class MainPresenter(context: Context) : BasePresenter(context) {

    var userModel: UserModel? = null
    init {
        userModel = iSecurityUtil.userLoggedIn(context, gson)!!
    }

    fun uploadFile(path : String){
        val fileData = File(path)
        if(fileData.exists()){
            AlRequest.POSTMultipart(Api.single_file_download(), context, object : AlRequest.OnMultipartRequest{
                override fun requestData(): MutableMap<String, VolleyMultipartRequest.DataPart> {
                    val params = HashMap<String, VolleyMultipartRequest.DataPart>()
                    params["file"] = getFileParam(Uri.parse(path))
                    return params
                }

                override fun onPreExecuted() {

                }

                override fun onSuccess(response: JSONObject?) {
                    try {
                        if (!response!!.getString("status").equals("ok")) {

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
                    var name = ""
                    var x = path.split("/")
                    for (i in 0 until x.size) {
                        if(i < (x.size-1)){
                            if(i > 0){
                                name = name+"/"+x.get(i)
                            } else {
                                name = name+x.get(i)
                            }

                        }
                    }
                    param["directory"] = name+"/"
                    param["file"] = path
                    param["deviceToken"] = userModel!!.firebaseToken
                    return param
                }

                override fun requestHeaders(): MutableMap<String, String> {
                    val param = HashMap<String, String>()
                    param["token"] = userModel!!.token
                    Log.d("token : ", userModel!!.token)
                    return param
                }

            })
        }
    }

    private fun getFileParam(file_uri: Uri) : VolleyMultipartRequest.DataPart{
        val new_uri = Uri.parse(File("file://"+file_uri.toString()).toString())
        return VolleyMultipartRequest.DataPart(new_uri.path, iSecurityUtil.getBytesFile(context, new_uri), iSecurityUtil.getTypeFile(context, new_uri!!))
    }
}