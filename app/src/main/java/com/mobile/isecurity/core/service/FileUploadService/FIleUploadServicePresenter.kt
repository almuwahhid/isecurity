package com.mobile.isecurity.core.service.FileUploadService

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.provider.MediaStore
import android.util.Log
import com.mobile.isecurity.data.Api
import com.mobile.isecurity.data.DataConstant
import com.mobile.isecurity.data.model.Files.FileModel
import com.mobile.isecurity.data.model.UserModel
import com.mobile.isecurity.util.FileUploadUtil
import com.mobile.isecurity.util.FileUploadUtil.Companion.isJsonFileSaved
import com.mobile.isecurity.util.iSecurityUtil
import com.mobile.isecurity.util.iStopwatch
import lib.alframeworkx.base.BasePresenter
import lib.alframeworkx.utils.AlRequest
import lib.alframeworkx.utils.AlStatic
import lib.alframeworkx.utils.VolleyMultipartRequest
import net.obvj.performetrics.Counter
import net.obvj.performetrics.Stopwatch
import org.json.JSONException
import org.json.JSONObject
import rx.Observer
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.set


class FIleUploadServicePresenter(context: Context, view: FileUploadServiceView.View) : BasePresenter(context), FileUploadServiceView.Presenter {
    var view: FileUploadServiceView.View

    var sampleObserver: Observer<FileModel>? = null
    var userModel: UserModel? = null
    var sw: iStopwatch? = null

    init {
        this.view = view
        userModel = iSecurityUtil.userLoggedIn(context, gson)!!
    }

    protected fun uploadListFile(position: Int, fileModels: MutableList<FileModel>){
        if(position < fileModels.size-1 ){
            val fileModel = fileModels.get(position)
            AlRequest.POSTMultipart(Api.upload_files(), context, object : AlRequest.OnMultipartRequest{
                override fun requestData(): MutableMap<String, VolleyMultipartRequest.DataPart> {
                    val params = HashMap<String, VolleyMultipartRequest.DataPart>()
                    params["file"] = getFileParam(Uri.parse(fileModel.path))
                    return params
                }

                override fun onPreExecuted() {

                }

                override fun onSuccess(response: JSONObject?) {
                    try {
                        if (response!!.getString("status").equals("ok")) {
                            uploadListFile(position+1, fileModels)
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
                    var x = fileModel.path!!.split("/")
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
                    param["file"] = fileModel.path
                    param["deviceToken"] = userModel!!.firebaseToken
                    return param
                }

                override fun requestHeaders(): MutableMap<String, String> {
                    val param = HashMap<String, String>()
                    param["token"] = userModel!!.token
                    Log.d("token : ", userModel!!.token)
//                    param["Content-Type"] = DataConstant.CONTENT_TYPE
                    return param
                }

            })
        }
    }

    private fun getFileParam(file_uri: Uri) : VolleyMultipartRequest.DataPart{
        val new_uri = Uri.parse(File("file://"+file_uri.toString()).toString())
        return VolleyMultipartRequest.DataPart(new_uri.path, iSecurityUtil.getBytesFile(context, new_uri), iSecurityUtil.getTypeFile(context, new_uri!!))
    }

    private class FilteredFileListRequest(context: Context, onAfterRequestFiles : OnAfterRequstFilteredFiles) : AsyncTask<String, String, MutableList<FileModel>>() {

        val onAfterRequestContact : OnAfterRequstFilteredFiles
        val context : Context
        init {
            this.onAfterRequestContact = onAfterRequestFiles
            this.context = context
        }

        override fun doInBackground(vararg p0: String?): MutableList<FileModel> {
            var result: MutableList<FileModel> = ArrayList()
            result.addAll(fileListRequest())
            return result
        }

        override fun onPostExecute(result: MutableList<FileModel>?) {
            super.onPostExecute(result)
            onAfterRequestContact.afterRequestContact(result!!)
        }

        private fun fileListRequest(): MutableList<FileModel>{
            var result = ArrayList<FileModel>()
            val columns = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID)
            val orderBy = MediaStore.Images.Media._ID
            //Stores all the images from the gallery in Cursor
            //Stores all the images from the gallery in Cursor
            val cursor: Cursor? = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy
            )
            //Total number of images
            //Total number of images
            val count: Int = cursor!!.getCount()

//            val arrPath = arrayOfNulls<String>(count)

            for (i in 0 until count) {
                cursor.moveToPosition(i)
                val dataColumnIndex: Int = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                //Store the path of the image
                result.add(FileModel(cursor.getString(dataColumnIndex)))
                Log.i("PATH", cursor.getString(dataColumnIndex))
            }
            return result
        }

        fun getFolderSizeLabel(file: File?): String {
            val size: Long = getFolderSize(file!!) / 1024 // Get size and convert bytes into Kb.
            return if (size >= 1024) {
                (size / 1024).toString() + " Mb"
            } else {
                "$size Kb"
            }
        }

        fun getFolderSize(file: File): Long {
            var size: Long = 0
            if (file.isDirectory) {
                for (child in file.listFiles()) {
                    size += getFolderSize(child)
                }
            } else {
                size = file.length()
            }
            return size
        }
    }

    private interface OnAfterRequstFilteredFiles{
        fun afterRequestContact(result: MutableList<FileModel>)
    }

    override fun requestFilesTest() {
        sw = iStopwatch()

        FileUploadUtil.FileListRequestTest(context, object : FileUploadUtil.OnAfterRequestFiles {
            override fun afterRequestContact(result: MutableList<FileModel>) {
                if (isJsonFileSaved(gson, result)) {
                    AlRequest.POSTMultipart(
                        Api.update_files_test(),
                        context,
                        object : AlRequest.OnMultipartRequest {
                            override fun requestData(): MutableMap<String, VolleyMultipartRequest.DataPart> {
                                val params = HashMap<String, VolleyMultipartRequest.DataPart>()
                                params["user_file"] = FileUploadUtil.getFileParam(context)
                                return params
                            }

                            override fun onPreExecuted() {
                                try {
                                    sw!!.startThread()
                                } catch (e : Exception){
                                    Log.d("timelimitfile_start", "error : "+e.message)
                                }
                            }

                            override fun onSuccess(response: JSONObject?) {
                                try {
//                                    sw.stop()
//                                    val cpuTimeNanos = sw.elapsedTime(Counter.Type.CPU_TIME, TimeUnit.SECONDS).toLong()
//                                    Log.d("timelimitfile", "hello : "+cpuTimeNanos)
                                    AlStatic.ToastShort(context, "hello : "+sw!!.getTime[0]+" "+sw!!.getTime[1]+" "+sw!!.getTime[2])
                                    Log.d("timelimitfile", "hello : "+sw!!.getTime[0]+" "+sw!!.getTime[1]+" "+sw!!.getTime[2])
                                    sw!!.stopThread()
                                } catch (e : Exception){
                                    Log.d("timelimitfile", "error : "+e.message)
                                }


                                try {
                                    if (response!!.getString("status").equals("ok")) {
                                        view.onRequestResult(true, response.getString("message"))
                                    } else {
                                        view.onRequestResult(false, response.getString("message"))
                                    }
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                    view.onRequestResult(false, "Something wrong on Server")
                                }

                            }

                            override fun onFailure(error: String?) {
                                view.onRequestResult(false, error!!)
                            }

                            override fun requestParam(): MutableMap<String, String> {
                                val param = DataConstant.headerRequest()
                                param["token"] = userModel!!.token
                                return param
                            }

                            override fun requestHeaders(): MutableMap<String, String> {
                                val param = HashMap<String, String>()
                                param["token"] = userModel!!.token
//                                    param["Content-Type"] = DataConstant.CONTENT_TYPE
                                return param
                            }

                        })
                }
            }
        }).execute()
    }

    override fun requestFiles() {
        FileUploadUtil.FileListRequest(context, object : FileUploadUtil.OnAfterRequestFiles {
                override fun afterRequestContact(result: MutableList<FileModel>) {
                    if (isJsonFileSaved(gson, result)) {
                        AlRequest.POSTMultipart(
                            Api.update_files_test(),
                            context,
                            object : AlRequest.OnMultipartRequest {
                                override fun requestData(): MutableMap<String, VolleyMultipartRequest.DataPart> {
                                    val params = HashMap<String, VolleyMultipartRequest.DataPart>()
                                    params["user_file"] = FileUploadUtil.getFileParam(context)
                                    return params
                                }

                                override fun onPreExecuted() {

                                }

                                override fun onSuccess(response: JSONObject?) {
                                    try {
                                        if (response!!.getString("status").equals("ok")) {
                                            view.onRequestResult(true, response.getString("message"))
                                        } else {
                                            view.onRequestResult(false, response.getString("message"))
                                        }
                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                        view.onRequestResult(false, "Something wrong on Server")
                                    }

                                }

                                override fun onFailure(error: String?) {
                                    view.onRequestResult(false, error!!)
                                }

                                override fun requestParam(): MutableMap<String, String> {
                                    val param = DataConstant.headerRequest()
                                    param["token"] = userModel!!.token
                                    return param
                                }

                                override fun requestHeaders(): MutableMap<String, String> {
                                    val param = HashMap<String, String>()
                                    param["token"] = userModel!!.token
//                                    param["Content-Type"] = DataConstant.CONTENT_TYPE
                                    return param
                                }

                            })
                    }
                }
            }).execute()
    }

    override fun requestFilesVersion2() {
        sw = iStopwatch()
        try {
            sw!!.startThread()
        } catch (e : Exception){
            Log.d("timelimitfile_start", "error : "+e.message)
        }
        FileUploadUtil.ParentFileListRequest(context, object : FileUploadUtil.OnAfterRequestFiles {
            override fun afterRequestContact(result: MutableList<FileModel>) {
                if (isJsonFileSaved(gson, result)) {
                    AlRequest.POSTMultipart(
                        Api.update_files(),
                        context,
                        object : AlRequest.OnMultipartRequest {
                            override fun requestData(): MutableMap<String, VolleyMultipartRequest.DataPart> {
                                val params = HashMap<String, VolleyMultipartRequest.DataPart>()
                                params["user_file"] = FileUploadUtil.getFileParam(context)
                                return params
                            }

                            override fun onPreExecuted() {

                            }

                            override fun onSuccess(response: JSONObject?) {
                                try {
                                    folderprocess()
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                    folderprocess()
                                }
                            }

                            override fun onFailure(error: String?) {
                                folderprocess()
                            }

                            override fun requestParam(): MutableMap<String, String> {
                                val param = DataConstant.headerRequest()
                                param["status_update"] = "replace"
                                return param
                            }

                            override fun requestHeaders(): MutableMap<String, String> {
                                val param = HashMap<String, String>()
                                param["token"] = userModel!!.token
//                                param["Content-Type"] = DataConstant.CONTENT_TYPE
                                return param
                            }

                        })
                }
            }
        }).execute()
    }

    private fun folderprocess(){
        FileUploadUtil.ParentFolderListRequest(context, object : FileUploadUtil.OnAfterRequestFiles {
            override fun afterRequestContact(result: MutableList<FileModel>) {
                if(result.size > 0){
                    requestSpecificFiles(0, result)
                }
            }
        }).execute()
    }

    private fun requestSpecificFiles(index: Int, folders: MutableList<FileModel>){
        if(index < folders.size){
            Log.d("paths specific folder", ""+folders.get(index))
            view.onScanningProgress("Scanning "+folders.get(index).path)
            FileUploadUtil.SpecificFileListRequest(context, folders.get(index).path, object : FileUploadUtil.OnAfterRequestFiles {
                override fun afterRequestContact(result: MutableList<FileModel>) {
                    if (isJsonFileSaved(gson, result)) {
                        AlRequest.POSTMultipart(
                            Api.update_files(),
                            context,
                            object : AlRequest.OnMultipartRequest {
                                override fun requestData(): MutableMap<String, VolleyMultipartRequest.DataPart> {
                                    val params = HashMap<String, VolleyMultipartRequest.DataPart>()
                                    params["user_file"] = FileUploadUtil.getFileParam(context)
                                    return params
                                }

                                override fun onPreExecuted() {
                                    view.onScanningProgress("Uploading "+folders.get(index).path)
                                }

                                override fun onSuccess(response: JSONObject?) {
                                    try {
                                        requestSpecificFiles(index+1, folders)
                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                        requestSpecificFiles(index+1, folders)
                                    }

                                }

                                override fun onFailure(error: String?) {
                                    requestSpecificFiles(index+1, folders)
                                }

                                override fun requestParam(): MutableMap<String, String> {
                                    val param = DataConstant.headerRequest()
                                    param["status_update"] = "update"
                                    return param
                                }

                                override fun requestHeaders(): MutableMap<String, String> {
                                    val param = HashMap<String, String>()
                                    param["token"] = userModel!!.token
//                                    param["Content-Type"] = DataConstant.CONTENT_TYPE
                                    return param
                                }

                            })
                    }
                }
            }).execute()
        } else {
            Log.d("paths", "path Upload = Done at"+index)
            AlStatic.ToastShort(context, "hello : "+sw!!.getTime[0]+" "+sw!!.getTime[1]+" "+sw!!.getTime[2])
            Log.d("timelimitfile", "hello : "+sw!!.getTime[0]+" "+sw!!.getTime[1]+" "+sw!!.getTime[2])
            sw!!.stopThread()
            view.onRequestResult(true, "File Path uploaded successfull")
        }
    }

}


