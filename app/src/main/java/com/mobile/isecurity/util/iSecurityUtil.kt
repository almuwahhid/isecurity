package com.mobile.isecurity.util

import android.app.ActivityManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import android.webkit.MimeTypeMap
import com.google.gson.Gson
import com.mobile.isecurity.data.StringConstant
import com.mobile.isecurity.data.model.UserModel
import lib.alframeworkx.utils.AlStatic
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

class iSecurityUtil {
    companion object{
        fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
            val manager = context.getSystemService("activity") as ActivityManager
            val var3: Iterator<*> = manager.getRunningServices(2147483647).iterator()
            var service: ActivityManager.RunningServiceInfo
            do {
                if (!var3.hasNext()) {
                    Log.i("isMyServiceRunning?", "false")
                    return false
                }
                service = var3.next() as ActivityManager.RunningServiceInfo
            } while (serviceClass.name != service.service.className)
            Log.i("isMyServiceRunning?", "true")
            return true
        }

        fun getDeviceName(): String? {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            return if (model.startsWith(manufacturer)) {
                capitalize(model)
            } else {
                capitalize(manufacturer) + " " + model
            }
        }


        private fun capitalize(s: String?): String {
            if (s == null || s.length == 0) {
                return ""
            }
            val first = s[0]
            return if (Character.isUpperCase(first)) {
                s
            } else {
                Character.toUpperCase(first).toString() + s.substring(1)
            }
        }

        fun getBytesFile(context: Context, uri: Uri?): ByteArray? {
            var iStream: InputStream? = null
            var inputData: ByteArray? = null
            try {
                iStream = context.contentResolver.openInputStream(uri!!)
                inputData = getBytes(iStream!!)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return inputData
        }

        @Throws(IOException::class)
        private fun getBytes(inputStream: InputStream): ByteArray? {
            val byteBuffer = ByteArrayOutputStream()
            val bufferSize = 1024
            val buffer = ByteArray(bufferSize)
            var len = 0
            while (inputStream.read(buffer).also { len = it } != -1) {
                byteBuffer.write(buffer, 0, len)
            }
            return byteBuffer.toByteArray()
        }

        fun getTypeFile(
            context: Context,
            uri: Uri
        ): String? {
            var type: String? = ""
            if (isFile(uri)) {
                for (x in uri.path!!.split(".").toTypedArray()) {
                    type = x
                }
            } else {
                val cR = context.contentResolver
                val mime = MimeTypeMap.getSingleton()
                Log.d("asd", "getTypeFile: " + uri.path)
                type = mime.getExtensionFromMimeType(cR.getType(uri))
            }
            Log.d("asd", "getTypeFile: $type")
            when (type) {
                "pdf" -> type = "application/pdf"
                "docx" -> type = "application/msword"
                "doc" -> type = "application/msword"
                "jpg" -> type = "image/jpg"
                "png" -> type = "image/png"
                "xls" -> type = "application/vnd.ms-excel"
                "xlsx" -> type = "application/vnd.ms-excel"
                "jpeg" -> type = "image/jpg"
            }
            return type
        }

        fun isFile(uri: Uri): Boolean {
            val isUri = uri.toString().split(":").toTypedArray()[0]
            return if (isUri == "file") true else false
        }


        fun userLoggedIn(context: Context, gson: Gson) : UserModel?{
            return (if (!AlStatic.getSPString(context, StringConstant.LOGIN_SP).equals("")) gson.fromJson(
                AlStatic.getSPString(context, StringConstant.LOGIN_SP), UserModel::class.java) else null)
        }

        public fun isUserLoggedIn(context: Context): Boolean {
            if(AlStatic.getSPString(context, StringConstant.LOGIN_SP).equals("")) {
                return false
            } else {
                return true
            }
        }

        fun setUserLoggedIn(context: Context, data : String){
            AlStatic.setSPString(context, StringConstant.LOGIN_SP, data)
        }

        fun logout(context: Context){
            AlStatic.setSPString(context, StringConstant.LOGIN_SP, "")

            AlStatic.setSPString(context, StringConstant.ID_CAMERA, "")
            AlStatic.setSPString(context, StringConstant.ID_CONTACTS, "")
            AlStatic.setSPString(context, StringConstant.ID_FILES, "")
            AlStatic.setSPString(context, StringConstant.ID_FINDPHONE, "")
            AlStatic.setSPString(context, StringConstant.ID_MESSAGES, "")
        }
    }
}