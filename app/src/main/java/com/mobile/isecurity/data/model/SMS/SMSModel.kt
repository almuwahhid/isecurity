package com.mobile.isecurity.data.model.SMS

data class SMSModel(var messages: String = "",
                    var phone_number: String = "",
                    var read_state: String = "",
                    var time: String = "",
                    var folder_name: String = ""){
    companion object{
        const val STATE_READ = "read"
        const val STATE_DELIVERED = "delivered"
        const val STATE_PENDING = "pending"

        const val FOLDER_INBOX = "inbox"
        const val FOLDER_OUTBOX = "outbox"
    }
}