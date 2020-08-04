package com.mobile.isecurity.app.detailsetting.presenter

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import com.mobile.isecurity.app.detailsetting.DetailSettingView
import com.mobile.isecurity.data.Api
import com.mobile.isecurity.data.DataConstant
import com.mobile.isecurity.data.model.Files.FileModel
import com.mobile.isecurity.data.model.UserModel
import com.mobile.isecurity.util.iSecurityUtil
import lib.alframeworkx.utils.AlRequest
import lib.alframeworkx.utils.VolleyMultipartRequest
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set


class FilePermissionPresenter(context: Context, userModel: UserModel, view: DetailSettingView.View) : DetailSettingPresenter(context), DetailSettingView.PresenterFiles {
    var view: DetailSettingView.View
    var userModel: UserModel
    init {
        this.view = view
        this.userModel = userModel
    }

    override fun requestFilesUpdate(isLoadingShown: Boolean) {
        FileListRequest(context, object : OnAfterRequestFiles{
            override fun afterRequestContact(result: MutableList<FileModel>) {
                if(isJsonFileSaved(result)){
                    AlRequest.POSTMultipart(Api.update_files(), context, object : AlRequest.OnMultipartRequest{
                        override fun requestData(): MutableMap<String, VolleyMultipartRequest.DataPart> {
                            val params = HashMap<String, VolleyMultipartRequest.DataPart>()
                            params["user_file"] = getFileParam()
                            return params
                        }

                        override fun onPreExecuted() {
//                            view.onLoading()
                            if(isLoadingShown)
                                view!!.onLoading()
                        }

                        override fun onSuccess(response: JSONObject?) {
                            view.onHideLoading()
                            try {
                                if (response!!.getString("status").equals("ok")) {
                                    view!!.onRequestNewSMS(true, response.getString("message"))
                                } else {
                                    view!!.onRequestNewSMS(false, response.getString("message"))
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
                            val param = DataConstant.headerRequest()
                            return param
                        }

                        override fun requestHeaders(): MutableMap<String, String> {
                            val param = HashMap<String, String>()
                            param["token"] = userModel.token
                            return param
                        }

                    })
                }

                /*AlRequest.POST(Api.update_files(), context, object : AlRequest.OnPostRequest{
                    override fun onSuccess(response: JSONObject?) {
                        view.onHideLoading()
                        try {
                            if (response!!.getString("status").equals("ok")) {
                                view!!.onRequestNewSMS(true, response.getString("message"))
                            } else {
                                view!!.onRequestNewSMS(false, response.getString("message"))
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
                        param["user_file"] = gson.toJson(result)
                        return param
                    }

                    override fun requestHeaders(): MutableMap<String, String> {
                        val param = HashMap<String, String>()
                        param["token"] = userModel.token
                        return param
                    }

                })*/
            }
        }).execute()
    }

    override fun setAccessPermission(access: String) {
        super.setAccessPermission(access)
        AlRequest.POST(Api.update_access_permission(), context, object : AlRequest.OnPostRequest{
            override fun onSuccess(response: JSONObject?) {
                try {
                    if (response!!.getString("status").equals("ok")) {
                        if(access.equals("1")){
                            requestFilesUpdate(false)
                        } else {
                            view.onHideLoading()
                            view!!.onRequestNewLocation(true, response.getString("message"))
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
                param["isFiles"] = ""+access
                return param
            }

            override fun requestHeaders(): MutableMap<String, String> {
                val param = HashMap<String, String>()
                param["token"] = userModel.token
                Log.d("gmsHeaders", "requestParam: $param")
                return param
            }

        })
    }

    private class FileListRequest(context: Context, onAfterRequestFiles : OnAfterRequestFiles) : AsyncTask<String, String, MutableList<FileModel>>() {

        val onAfterRequestContact : OnAfterRequestFiles
        val context : Context
        init {
            this.onAfterRequestContact = onAfterRequestFiles
            this.context = context
        }

        override fun doInBackground(vararg p0: String?): MutableList<FileModel> {
            var result: MutableList<FileModel> = ArrayList()
            result.addAll(fileListRequest(File("/sdcard/")))
//            result.addAll(fileListRequest(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString())))
            return result
        }

        override fun onPostExecute(result: MutableList<FileModel>?) {
            super.onPostExecute(result)
            onAfterRequestContact.afterRequestContact(result!!)
        }

        private fun fileListRequest(filePath: File): MutableList<FileModel>{
            var result = ArrayList<FileModel>()
            if(filePath.exists()){
                if (filePath.listFiles() != null && filePath.listFiles().size > 0){
                    for (i in filePath.listFiles().indices) {
                        var data = FileModel()
                        var datafile = filePath.listFiles().get(i)
                        data.name = datafile.name
                        data.uri = datafile.absolutePath
                        Log.d("paths", "path = "+datafile.path)
                        Log.d("paths", "absolute path = "+datafile.absoluteFile)
                        try {
                            var name = ""
                            var x = datafile.absolutePath!!.split("/")
                            for (i in 0 until x.size) {
                                if(i < (x.size-1)){
                                    if(i > 0){
                                        name = name+"/"+x.get(i)
                                    } else {
                                        name = name+x.get(i)
                                    }

                                }
                            }
                            data.uri = name
                        } catch (e: Exception){

                        }

                        if(!datafile.isDirectory()){
                            data.type = FileModel.TYPE_FILE
                            try{
                                data.type_file = datafile.name.substring(datafile.name.lastIndexOf(".")+1).toLowerCase();
                            } catch (e: Exception){

                            }
                        } else {
                            data.type = FileModel.TYPE_FOLDER
                            data.type_file = ""
//                            data.child_files = fileListRequest(datafile)
                            result.addAll(fileListRequest(datafile))
                        }

                        result.add(data)
                    }
                }
            }
            return result
        }
    }

    private interface OnAfterRequestFiles{
        fun afterRequestContact(result: MutableList<FileModel>)
    }

    private fun isJsonFileSaved(result: MutableList<FileModel>) : Boolean {

        val jsont = gson.toJson(result)
        try {
            val root = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "iSecurity")
            if (!root.exists()) {
                root.mkdirs()
            }
            val gpxfile = File(root, "isecurity-files.txt")
            val writer = FileWriter(gpxfile)
            writer.append(jsont)
            writer.flush()
            writer.close()
//            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()

            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }

    private fun getFileParam() : VolleyMultipartRequest.DataPart{
        val file_uri = Uri.withAppendedPath(Uri.fromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)), "iSecurity/isecurity-files.txt")
        return VolleyMultipartRequest.DataPart(file_uri!!.path, iSecurityUtil.getBytesFile(context, file_uri), iSecurityUtil.getTypeFile(context, file_uri!!))
    }


}