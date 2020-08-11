package com.mobile.isecurity.data.model.Files

data class FileModel(var id: String = "",
                     var name: String = "",
                     var type: String = "",
                     var type_file: String = "",
                     var uri: String = "",
                     var file_size: String = "",
                     var child_files: MutableList<FileModel> = ArrayList()) {
    companion object{
        const val TYPE_FOLDER = "folder"
        const val TYPE_FILE = "file"
    }
}