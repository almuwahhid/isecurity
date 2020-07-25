package com.mobile.isecurity.data.model

import java.io.Serializable

data class UserModel(var username : String = "",
                     var name : String = "",
                     var email : String = "",
                     var phone : String = "",
                     var profile_image : String = "",
                     var isContacts : Int = 0,
                     var isSms : Int = 0,
                     var isFiles : Int = 0,
                     var isCamera : Int = 0,
                     var isLocation : Int = 0,
                     var created_at : String = "",
                     var token : String = "",
                     var firebaseToken: String = "",
                     var countryCode: String = "") : Serializable