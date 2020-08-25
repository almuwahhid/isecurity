package com.mobile.isecurity.data.DAO

import androidx.room.Database
import com.mobile.isecurity.data.DAO.Login.LoginAccess
import com.mobile.isecurity.data.DAO.Login.LoginModel

@Database(entities = [LoginModel::class], version = 1, exportSchema = false)
class iSecurityDB {
//    abstract fun db_notif(): LoginAccess?
}