package com.mobile.isecurity.data.model

import java.io.Serializable

data class SecurityMenuModel(
    var id: String,
    var title: String,
    var subtitle: String,
    var icon: Int,
    var background: Int,
    var status: Int = 0) : Serializable