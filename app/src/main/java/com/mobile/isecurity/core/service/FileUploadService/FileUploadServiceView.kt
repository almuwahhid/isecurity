package com.mobile.isecurity.core.service.FileUploadService

interface FileUploadServiceView {
    interface Presenter{
        fun requestFiles()
    }
    interface View{
        fun percentage(percent : Int, isDone : Boolean)
        fun onRequestResult(isSuccess: Boolean, message: String)
    }
}