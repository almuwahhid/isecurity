package com.mobile.isecurity.data.model.Contact

data class ContactModel(var name : String = "",
                        var phone : List<String> = ArrayList(),
                        var email : List<String> = ArrayList()
                        ){
}