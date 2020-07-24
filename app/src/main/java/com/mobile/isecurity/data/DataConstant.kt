package com.mobile.isecurity.data

import java.util.HashMap

class DataConstant {
    companion object{
        fun headerRequest(): HashMap<String, String> {
            val param = HashMap<String, String>()
            param["APP_KEY"] = "cSNOBVMHFgU8exSIxS"
            param["device_id"] = "samsung"
            return param
        }
    }
}