package com.mobile.isecurity.app.register

import com.mobile.isecurity.data.model.UserModel
import lib.alframeworkx.base.BaseView
import lib.alframeworkx.utils.VolleyMultipartRequest
import java.util.HashMap

interface RegisterView {
    interface Presenter{
        fun sendRegisterData(params : HashMap<String, String>, data: VolleyMultipartRequest.DataPart?)
    }
    interface View: BaseView {
        fun onSuccessRegister(message: String)
    }
}