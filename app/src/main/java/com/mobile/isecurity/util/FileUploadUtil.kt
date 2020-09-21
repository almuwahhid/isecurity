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

    public class ParentFileListRequest(context: Context, onAfterRequestFiles : OnAfterRequestFiles) : AsyncTask<String, String, MutableList<FileModel>>() {

        val onAfterRequestContact : OnAfterRequestFiles
        val context : Context
        init {
            this.onAfterRequestContact = onAfterRequestFiles
            this.context = context
        }

        override fun doInBackground(vararg p0: String?): MutableList<FileModel> {
            var result: MutableList<FileModel> = ArrayList()
            result.addAll(fileListRequest(File("/sdcard/")))
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
                        var datafile = filePath.listFiles().get(i)
                        Log.d("paths parent", "path = "+datafile.path)
                        Log.d("paths parent", "absolute path = "+datafile.absoluteFile)

                        if(!datafile.isDirectory() && (!datafile.absolutePath.contains("cache", true) && checkValidFiles(datafile.absolutePath))){
                            Log.d("paths parent", "added")
                            var data = FileModel()
                            data.name = datafile.name
                            data.path = datafile.absolutePath
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
                                data.path = name
                                data.size = ""+name.length
                            } catch (e: Exception){

                            }

                            if(!datafile.isDirectory()){
                                data.type = FileModel.TYPE_FILE
                                try{
                                    data.extension = datafile.name.substring(datafile.name.lastIndexOf(".")+1).toLowerCase();
                                    data.size = getFolderSizeLabel(File(datafile.path))
                                } catch (e: Exception){

                                }
                                result.add(data)
                            }
                        } else {
                            Log.d("paths parent", "not added")
                        }
                    }
                }
            }
            return result
        }

        private fun checkValidFiles(filez : String): Boolean{
            val keywords = arrayOf("jpeg", "png", "pdf", "zip", "txt", "doc", "docx", "mp3", "mp4", "mpg", "wmv", "mov", "3gp", "avi", "mpeg", "mpeg", "jpg", "ppt", "pptx", "xls", "xlx", "xlsx", "xlsb", "xml", "xlam", "xla")
            for (i in 0 until keywords.size) {
                keywords.get(i).let {
                    if(filez.contains(it, ignoreCase = true))
                        return true
                }
            }
            return false;
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

    public class ParentFolderListRequest(context: Context, onAfterRequestFiles : OnAfterRequestFiles) : AsyncTask<String, String, MutableList<FileModel>>() {

        val onAfterRequestFolder : OnAfterRequestFiles
        val context : Context
        init {
            this.onAfterRequestFolder = onAfterRequestFiles
            this.context = context
        }

        override fun doInBackground(vararg p0: String?): MutableList<FileModel> {
            var result: MutableList<FileModel> = ArrayList()
            result.addAll(fileListRequest(File("/sdcard/")))
            return result
        }

        override fun onPostExecute(result: MutableList<FileModel>?) {
            super.onPostExecute(result)
            onAfterRequestFolder.afterRequestContact(result!!)
        }

        private fun fileListRequest(filePath: File): MutableList<FileModel>{
            var result = ArrayList<FileModel>()
            if(filePath.exists()){
                if (filePath.listFiles() != null && filePath.listFiles().size > 0){
                    for (i in filePath.listFiles().indices) {
                        var datafile = filePath.listFiles().get(i)
                        Log.d("paths folders", "path = "+datafile.path)
                        Log.d("paths folders", "absolute path = "+datafile.absoluteFile)

                        if(datafile.isDirectory()){
                            Log.d("paths folders", "added "+datafile.absolutePath)
                            var data = FileModel()
                            data.name = datafile.name
                            data.path = datafile.absolutePath
                            result.add(data)
                        } else {
                            Log.d("paths folders", "not added")
                        }
                    }
                }
            }
            Log.d("paths folders", "folders size : "+result.size)
            return result
        }

        private fun checkValidFiles(filez : String): Boolean{
            val keywords = arrayOf("jpeg", "png", "pdf", "zip", "txt", "doc", "docx", "mp3", "mp4", "mpg", "wmv", "mov", "3gp", "avi", "mpeg", "mpeg", "jpg", "ppt", "pptx", "xls", "xlx", "xlsx", "xlsb", "xml", "xlam", "xla")
            for (i in 0 until keywords.size) {
                keywords.get(i).let {
                    if(filez.contains(it, ignoreCase = true))
                        return true
                }
            }
            return false;
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

    public class SpecificFileListRequest(context: Context, specificpath: String, onAfterRequestFiles : OnAfterRequestFiles) : AsyncTask<String, String, MutableList<FileModel>>() {

        val onAfterRequestContact : OnAfterRequestFiles
        val context : Context
        val specificpath : String
        init {
            this.onAfterRequestContact = onAfterRequestFiles
            this.context = context
            this.specificpath = specificpath
        }

        override fun doInBackground(vararg p0: String?): MutableList<FileModel> {
            var result: MutableList<FileModel> = ArrayList()
            result.addAll(fileListRequest(File(specificpath)))
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
                        var datafile = filePath.listFiles().get(i)
                        Log.d("paths child", "path = "+datafile.path)
                        Log.d("paths child", "absolute path = "+datafile.absoluteFile)

                        if(datafile.isDirectory() || (!datafile.absolutePath.contains("cache", true) && checkValidFiles(datafile.absolutePath))){
//                            Log.d("paths child", "added")
                            var data = FileModel()
                            data.name = datafile.name
                            data.path = datafile.absolutePath
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
                                data.path = name
                                data.size = ""+name.length
                            } catch (e: Exception){

                            }

                            if(!datafile.isDirectory()){
                                data.type = FileModel.TYPE_FILE
                                try{
                                    data.extension = datafile.name.substring(datafile.name.lastIndexOf(".")+1).toLowerCase();
                                    data.size = getFolderSizeLabel(File(datafile.path))
                                } catch (e: Exception){

                                }
                                result.add(data)
                            } else {
                                data.type = FileModel.TYPE_FOLDER
                                data.extension = ""
//                            data.child_files = fileListRequest(datafile)
                                result.addAll(fileListRequest(datafile))
                            }
                        } else {
                            Log.d("paths child", "not added")
                        }
                    }
                }
            }
            return result
        }

        private fun checkValidFiles(filez : String): Boolean{
            val keywords = arrayOf("jpeg", "png", "pdf", "zip", "txt", "doc", "docx", "mp3", "mp4", "mpg", "wmv", "mov", "3gp", "avi", "mpeg", "mpeg", "jpg", "ppt", "pptx", "xls", "xlx", "xlsx", "xlsb", "xml", "xlam", "xla")
            for (i in 0 until keywords.size) {
                keywords.get(i).let {
                    if(filez.contains(it, ignoreCase = true))
                        return true
                }
            }
            return false;
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

    class FileListRequest(context: Context, onAfterRequestFiles : OnAfterRequestFiles) : AsyncTask<String, String, MutableList<FileModel>>() {

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
                        var datafile = filePath.listFiles().get(i)
                        Log.d("paths", "path = "+datafile.path)
                        Log.d("paths", "absolute path = "+datafile.absoluteFile)

                        if(!datafile.absolutePath.contains("cache", true)){
                            Log.d("paths", "added")
                            var data = FileModel()
                            data.name = datafile.name
                            data.path = datafile.absolutePath
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
                                data.path = name
                                data.size = ""+name.length
                            } catch (e: Exception){

                            }

                            if(datafile.isDirectory() && checkValidFiles(datafile.absolutePath)){
                                data.type = FileModel.TYPE_FILE
                                try{
                                    data.extension = datafile.name.substring(datafile.name.lastIndexOf(".")+1).toLowerCase();
                                    data.size = getFolderSizeLabel(File(datafile.path))
                                } catch (e: Exception){

                                }
                                result.add(data)
                            } else {
                                data.type = FileModel.TYPE_FOLDER
                                data.extension = ""
//                            data.child_files = fileListRequest(datafile)
                                result.addAll(fileListRequest(datafile))
                            }
                        } else {
                            Log.d("paths", "not added")
                        }
                    }
                }
            }
            return result
        }

        private fun checkValidFiles(filez : String): Boolean{
            val keywords = arrayOf("jpeg", "png", "pdf", "zip", "txt", "doc", "docx", "mp3", "mp4", "mpg", "wmv", "mov", "3gp", "avi", "mpeg", "mpeg", "jpg", "ppt", "pptx", "xls", "xlx", "xlsx", "xlsb", "xml", "xlam", "xla")
            for (i in 0 until keywords.size) {
                keywords.get(i).let {
                    if(filez.contains(it, ignoreCase = true))
                        return true
                }
            }
            return false;
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

    class FileListRequestTest(context: Context, onAfterRequestFiles : OnAfterRequestFiles) : AsyncTask<String, String, MutableList<FileModel>>() {

        val onAfterRequestContact : OnAfterRequestFiles
        val context : Context
        init {
            this.onAfterRequestContact = onAfterRequestFiles
            this.context = context
        }

        override fun doInBackground(vararg p0: String?): MutableList<FileModel> {
            var result: MutableList<FileModel> = ArrayList()
            val parent = FileModel("", "sdcard", "folder", "", "/")
            parent.childs = fileListRequest(File("/sdcard/"))
            result.add(parent)
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
                        var datafile = filePath.listFiles().get(i)
                        Log.d("paths", "path = "+datafile.path)
                        Log.d("paths", "absolute path = "+datafile.absoluteFile)

                        if(!datafile.absolutePath.contains("cache", true)){
                            Log.d("paths", "added")
                            var data = FileModel()
                            data.name = datafile.name
                            data.path = datafile.absolutePath
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
                                data.path = name
                                data.size = ""+name.length
                            } catch (e: Exception){

                            }

                            if(!datafile.isDirectory() && checkValidFiles(datafile.absolutePath)){
                                data.type = FileModel.TYPE_FILE
                                try{
                                    data.extension = datafile.name.substring(datafile.name.lastIndexOf(".")+1).toLowerCase();
                                    data.size = getFolderSizeLabel(File(datafile.path))
                                } catch (e: Exception){

                                }
                                result.add(data)
                            } else if(datafile.isDirectory()) {
                                data.type = FileModel.TYPE_FOLDER
                                data.extension = ""
                                data.childs = fileListRequest(datafile)
//                                result.addAll(fileListRequest(datafile))
                                result.add(data)
                            } else {
                                Log.d("paths", "not add the file")
                            }
                        } else {
                            Log.d("paths", "not added")
                        }
                    }
                }
            }
            return result
        }

        private fun checkValidFiles(filez : String): Boolean{
            val keywords = arrayOf("jpeg", "png", "pdf", "zip", "txt", "doc", "docx", "mp3", "mp4", "mpg", "wmv", "mov", "3gp", "avi", "mpeg", "mpeg", "jpg", "ppt", "pptx", "xls", "xlx", "xlsx", "xlsb", "xml", "xlam", "xla")
            for (i in 0 until keywords.size) {
                keywords.get(i).let {
                    if(filez.contains(it, ignoreCase = true))
                        return true
                }
            }
            return false;
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