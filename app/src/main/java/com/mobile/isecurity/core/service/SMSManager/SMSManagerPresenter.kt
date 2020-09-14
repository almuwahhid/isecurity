package com.mobile.isecurity.core.service.SMSManager

import android.content.Context
import lib.alframeworkx.base.BasePresenter

class SMSManagerPresenter(context: Context, view: SMSManagerView.View) : BasePresenter(context), SMSManagerView.Presenter {

    var view : SMSManagerView.View

    init {
        this.view = view
    }

    override fun deleteMessage() {

    }
}