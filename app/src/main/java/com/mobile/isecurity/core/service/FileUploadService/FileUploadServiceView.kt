package com.mobile.isecurity.core.service.FileUploadService

interface FileUploadServiceView {
    interface Presenter{
        fun requestFiles()
        fun requestFilesVersion2()
    }
    interface View{
        fun percentage(percent : Int, isDone : Boolean)
        fun onRequestResult(isSuccess: Boolean, message: String)
        fun onScanningProgress(title: String)
    }
}