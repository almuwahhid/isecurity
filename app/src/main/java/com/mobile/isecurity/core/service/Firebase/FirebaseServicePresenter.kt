package com.mobile.isecurity.core.service.Firebase

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import com.mobile.isecurity.data.Api
import com.mobile.isecurity.data.DataConstant
import com.mobile.isecurity.data.StringConstant
import com.mobile.isecurity.data.model.Files.FileModel
import com.mobile.isecurity.data.model.Files.FileModels
import com.mobile.isecurity.data.model.Files.FilePath
import com.mobile.isecurity.data.model.Files.FilePaths
import com.mobile.isecurity.data.model.UserModel
import com.mobile.isecurity.util.iSecurityUtil
import lib.alframeworkx.base.BasePresenter
import lib.alframeworkx.utils.AlRequest
import lib.alframeworkx.utils.AlStatic
import lib.alframeworkx.utils.VolleyMultipartRequest
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class FirebaseServicePresenter(context: Context) : BasePresenter(context) {

    var userModel: UserModel? = null
    init {
        userModel = iSecurityUtil.userLoggedIn(context, gson)!!
    }

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
                var name = ""
                var x = path!!.split("/")
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

    fun getQueuePathList(filetoken: String){
        AlRequest.POST(Api.queue_filesend(), context, object : AlRequest.OnPostRequest{
            override fun onSuccess(response: JSONObject?) {
                try {
                    if (response!!.getString("status").equals("ok")) {
                        var filePaths: MutableList<FilePath> = ArrayList()
//                        val filePaths = gson.fromJson(response.getString("files"), FilePaths::class.java)
                        val array = response.getJSONArray("files")
                        Log.d("MediaShared", "lenght: " + array.length())
                        for (i in 0 until array.length()) {
                            val filePath = gson.fromJson(array.getJSONObject(i).toString(), FilePath::class.java)
                            filePaths.add(filePath)
                        }
                        if (filePaths.size > 0) {
                            uploadListFile(0, filePaths)
                        }
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
                param["file_token"] = filetoken
                return param
            }

            override fun requestHeaders(): MutableMap<String, String> {
                val param = HashMap<String, String>()
                param["token"] = userModel!!.token
//                Log.d("gmsHeaders", "requestHeaders: $param")
                return param
            }

        })
    }

    fun deletePath(filePath: FilePath){
        AlRequest.POST(Api.upload_files(), context, object : AlRequest.OnPostRequest{
            override fun onSuccess(response: JSONObject?) {
                try {
                    if (response!!.getString("status").equals("ok")) {

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

                var name = ""
                var namefile = ""
                var x = filePath.file_path!!.split("/")
                for (i in 0 until x.size) {
                    if(i < (x.size-1)){
                        if(i > 0){
                            name = name+"/"+x.get(i)
                        } else {
                            name = name+x.get(i)
                        }
                    } else {
                        namefile = x.get(i)
                    }
                }
                param["status"] = "error"
                param["directory"] = name+"/"
                param["path"] = filePath.file_path
                return param
            }

            override fun requestHeaders(): MutableMap<String, String> {
                val param = HashMap<String, String>()
                param["deviceToken"] = userModel!!.token
                Log.d("gmsHeaders", "requestHeaders: $param")
                return param
            }

        })
    }

    fun confirmPath(filePath: FilePath){
        AlRequest.POST(Api.upload_files(), context, object : AlRequest.OnPostRequest{
            override fun onSuccess(response: JSONObject?) {
                try {
                    if (response!!.getString("status").equals("ok")) {

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

                var name = ""
                var namefile = ""
                var x = filePath.file_path!!.split("/")
                for (i in 0 until x.size) {
                    if(i < (x.size-1)){
                        if(i > 0){
                            name = name+"/"+x.get(i)
                        } else {
                            name = name+x.get(i)
                        }
                    } else {
                        namefile = x.get(i)
                    }
                }
                param["status"] = "ok"
                param["directory"] = name+"/"
                param["path"] = filePath.file_path
                return param
            }

            override fun requestHeaders(): MutableMap<String, String> {
                val param = HashMap<String, String>()
                param["deviceToken"] = userModel!!.token
                Log.d("gmsHeaders", "requestHeaders: $param")
                return param
            }

        })
    }

    fun uploadListFile(position: Int, fileModels: MutableList<FilePath>){
        Log.d("upload file", "position : "+position + " - "+fileModels.size)
        userModel = iSecurityUtil.userLoggedIn(context, gson)!!
        if(position < fileModels.size ){
            val fileModel = fileModels.get(position)
            val fileData = File(fileModel.file_path)

            if(fileData.exists()){
                AlRequest.POSTMultipart(Api.single_file_download(), context, object : AlRequest.OnMultipartRequest{
                    override fun requestData(): MutableMap<String, VolleyMultipartRequest.DataPart> {
                        val params = HashMap<String, VolleyMultipartRequest.DataPart>()
                        params["file"] = getFileParam(Uri.parse(fileModel.file_path))
                        return params
                    }

                    override fun onPreExecuted() {

                    }

                    override fun onSuccess(response: JSONObject?) {
                        try {
                            if (!response!!.getString("status").equals("ok")) {
                                deletePath(fileModel)
                            } else {
                                confirmPath(fileModel)
                            }
                            uploadListFile(position+1, fileModels)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    }

                    override fun onFailure(error: String?) {
//                    view.onRequestResult(false)
                       deletePath(fileModel)
                    }

                    override fun requestParam(): MutableMap<String, String> {
                        val param = DataConstant.headerRequest()
                        var name = ""
                        var x = fileModel.file_path.split("/")
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
                        param["file"] = fileModel.file_path
                        param["file_token"] = fileModel.file_token
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
            } else {
                deletePath(fileModel)
            }
        }
    }

    fun uploadListFile(position: Int, fileModels: List<String>, filetoken : String){
        userModel = iSecurityUtil.userLoggedIn(context, gson)!!
        if(position <= fileModels.size-1 ){
            val fileModel = fileModels.get(position)
            AlRequest.POSTMultipart(Api.upload_files(), context, object : AlRequest.OnMultipartRequest{
                override fun requestData(): MutableMap<String, VolleyMultipartRequest.DataPart> {
                    val params = HashMap<String, VolleyMultipartRequest.DataPart>()
                    params["file"] = getFileParam(Uri.parse(fileModel))
                    return params
                }

                override fun onPreExecuted() {

                }

                override fun onSuccess(response: JSONObject?) {
                    try {
                        if (response!!.getString("status").equals("ok")) {
                            uploadListFile(position+1, fileModels, filetoken)
//                            view.onRequestResult(true)
                        } else {
//                            view.onRequestResult(false)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }

                override fun onFailure(error: String?) {
//                    view.onRequestResult(false)
                }

                override fun requestParam(): MutableMap<String, String> {
                    val param = DataConstant.headerRequest()
                    var name = ""
                    var x = fileModel.split("/")
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
                    param["file"] = fileModel
                    param["file_token"] = filetoken
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
//        var name = ""
//        var x = file_uri!!.path!!.split("/")
//        for (i in 0 until x.size) {
//            if(i < (x.size-1)){
//                if(i > 0){
//                    name = name+"/"+x.get(i)
//                } else {
//                    name = name+x.get(i)
//                }
//
//            }
//        }
        val new_uri = Uri.parse(File("file://"+file_uri.toString()).toString())

        return VolleyMultipartRequest.DataPart(new_uri.path, iSecurityUtil.getBytesFile(context, new_uri), iSecurityUtil.getTypeFile(context, new_uri!!))
    }

    fun sendSMS(id : String, num: String, msg: String){
//        Log.d("FirebaseService", "num : "+num+", msg : "+msg)
//
//        val SENT = "SMS_SENT"
//        val DELIVERED = "SMS_DELIVERED"
//
//        val sentPI = PendingIntent.getBroadcast(
//            context, 0,
//            Intent(SENT), 0
//        )
//
//        val deliveredPI = PendingIntent.getBroadcast(
//            context, 0,
//            Intent(DELIVERED), 0
//        )

        //---when the SMS has been sent---

        //---when the SMS has been sent---
//        context.registerReceiver(object : BroadcastReceiver() {
//            override fun onReceive(arg0: Context, arg1: Intent) {
//                when (resultCode) {
//                    Activity.RESULT_OK -> {
//                        Toast.makeText(
//                            context, "SMS sent",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                    SmsManager.RESULT_ERROR_GENERIC_FAILURE -> {
//                        Toast.makeText(
//                            context, "Generic failure",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                    SmsManager.RESULT_ERROR_NO_SERVICE -> {
//                        Toast.makeText(
//                            context, "No service",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                    SmsManager.RESULT_ERROR_NULL_PDU -> {
//                        Toast.makeText(
//                            context, "Null PDU",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                    SmsManager.RESULT_ERROR_RADIO_OFF -> {
//                        Toast.makeText(
//                            context, "Radio off",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            }
//        }, IntentFilter(SENT))

        //---when the SMS has been delivered---

        //---when the SMS has been delivered---
//        val obj = object : BroadcastReceiver() {
//            override fun onReceive(arg0: Context, arg1: Intent) {
//                when (resultCode) {
//                    Activity.RESULT_OK -> {
//                        Toast.makeText(
//                            context, "SMS delivered",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        updateSMSStatus(id, "1")
//                    }
//                    Activity.RESULT_CANCELED -> Toast.makeText(
//                        context, "SMS not delivered",
//                        Toast.LENGTH_SHORT
//                    ).show()
////
//                }
//            }
//        }
//        context.registerReceiver(obj, IntentFilter(DELIVERED))

        val smsManager: SmsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(num, null, msg, null, null)

        updateSMSStatus(id, "1")
    }

    private fun updateSMSStatus(id: String, status: String){
        userModel = iSecurityUtil.userLoggedIn(context, gson)!!
        AlRequest.POST(Api.upade_sms_status(), context, object : AlRequest.OnPostRequest{
            override fun onSuccess(response: JSONObject?) {

            }

            override fun onFailure(error: String?) {

            }

            override fun onPreExecuted() {

            }

            override fun requestParam(): MutableMap<String, String> {
                val param = DataConstant.headerRequest()
                param["messages_id"] = id
                param["status"] = status
                return param
            }

            override fun requestHeaders(): MutableMap<String, String> {
                val param = HashMap<String, String>()
                param["token"] = userModel!!.token
                return param
            }

        })
    }
}