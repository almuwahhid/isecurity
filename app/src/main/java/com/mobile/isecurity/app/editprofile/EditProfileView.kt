package com.mobile.isecurity.app.editprofile

import com.mobile.isecurity.data.model.UserModel
import lib.alframeworkx.base.BaseView
import lib.alframeworkx.utils.VolleyMultipartRequest
import java.util.HashMap

interface EditProfileView {
    interface View: BaseView{
        fun onSuccessEditProfile(userModel: UserModel, message: String)
    }
    interface Presenter{
        fun sendUpdateData(token: String, params : HashMap<String, String>, data: VolleyMultipartRequest.DataPart?)
    }
}