package com.mobile.isecurity.util

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import com.mobile.isecurity.data.model.Files.FileModel
import lib.alframeworkx.utils.VolleyMultipartRequest
import java.io.File
import java.io.FileWriter
import java.io.IOException

class FileUploadUtil {
    interface OnAfterRequestFiles{
        fun afterRequestContact(result: MutableList<FileModel>)
    }

    public class FileListRequest(context: Context, onAfterRequestFiles : OnAfterRequestFiles) : AsyncTask<String, String, MutableList<FileModel>>() {

        val onAfterRequestContact : OnAfterRequestFiles
        val context : Context
        init {
            this.onAfterRequestContact = onAfterRequestFiles
            this.context = context
        }

        override fun doInBackground(vararg p0: String?): MutableList<FileModel> {
            var result: MutableList<FileModel> = ArrayList()
//            result.addAll(fileListRequest(File("/sdcard/")))
            result.addAll(fileListRequest(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString())))
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
                            data.file_size = ""+name.length
                        } catch (e: Exception){

                        }

                        if(!datafile.isDirectory()){
                            data.type = FileModel.TYPE_FILE
                            try{
                                data.type_file = datafile.name.substring(datafile.name.lastIndexOf(".")+1).toLowerCase();
                                data.file_size = getFolderSizeLabel(File(datafile.path))
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
    companion object{

        fun getFileParam(context: Context) : VolleyMultipartRequest.DataPart{
            val file_uri = Uri.withAppendedPath(Uri.fromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)), "iSecurity/isecurity-files.txt")
            return VolleyMultipartRequest.DataPart(file_uri!!.path, iSecurityUtil.getBytesFile(context, file_uri), iSecurityUtil.getTypeFile(context, file_uri!!))
        }

        fun isJsonFileSaved(gson: Gson, result: MutableList<FileModel>) : Boolean {
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
    }
}