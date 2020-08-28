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
import com.mobile.isecurity.util.iSecurityUtil
import lib.alframeworkx.base.BasePresenter
import lib.alframeworkx.utils.AlRequest
import lib.alframeworkx.utils.VolleyMultipartRequest
import org.json.JSONException
import org.json.JSONObject
import rx.Observable
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
import rx.schedulers.Schedulers
import java.io.File
import java.util.HashMap


//class FIleUploadServicePresenter(context: Context, view : FileUploadServiceView.View) : BasePresenter(context), FileUploadServiceView.Presenter {
class FIleUploadServicePresenter(context: Context, view: FileUploadServiceView.View) : BasePresenter(context), FileUploadServiceView.Presenter {
    var view: FileUploadServiceView.View

    var sampleObserver: Observer<FileModel>? = null
    var userModel: UserModel? = null

    init {
        this.view = view
        userModel = iSecurityUtil.userLoggedIn(context, gson)!!
    }

    override fun requestImages() {

        /*sampleObserver = object : Observer<FileModel> {
            override open fun onCompleted() {}
            override open fun onError(e: Throwable) {}
            override open fun onNext(s: FileModel) {
                Log.d("TestOnNext", "onNext: $s")

            }
        }

        FilteredFileListRequest(context, object: OnAfterRequstFilteredFiles{
            override fun afterRequestContact(result: MutableList<FileModel>) {
                Observable.from(result)
                    .subscribeOn(Schedulers.io())
                    .map(Func1<FileModel, FileModel> { s ->
                        try {
                            Thread.sleep(200)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        s
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(sampleObserver)
            }
        })*/
    }

    protected fun uploadListFile(position: Int, fileModels: MutableList<FileModel>){
        if(position < fileModels.size-1 ){
            val fileModel = fileModels.get(position)
            AlRequest.POSTMultipart(Api.upload_files(), context, object : AlRequest.OnMultipartRequest{
                override fun requestData(): MutableMap<String, VolleyMultipartRequest.DataPart> {
                    val params = HashMap<String, VolleyMultipartRequest.DataPart>()
                    params["file"] = getFileParam(Uri.parse(fileModel.uri))
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
                    var x = fileModel.uri!!.split("/")
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
                    param["file"] = fileModel.uri
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

}


