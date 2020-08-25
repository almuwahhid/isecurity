package com.mobile.isecurity.data.DAO.Login

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert

@Dao
interface LoginAccess {

    @Insert
    fun insertLogin(loginModel: LoginModel)

    @Delete
    fun deleteLogin(loginModel: LoginModel)


}