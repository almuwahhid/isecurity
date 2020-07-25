package com.mobile.isecurity.app.detailsetting.presenter

import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mobile.isecurity.app.detailsetting.DetailSettingView
import com.mobile.isecurity.data.Api
import com.mobile.isecurity.data.DataConstant
import com.mobile.isecurity.data.model.UserModel
import lib.alframeworkx.base.BasePresenter
import lib.alframeworkx.utils.AlRequest
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class LocationPermissionPresenter(context: Context, userModel: UserModel, view: DetailSettingView.View) : DetailSettingPresenter(context), DetailSettingView.PresenterLocation {

    var view: DetailSettingView.View
    var userModel: UserModel
    var fusedLocationClient: FusedLocationProviderClient
    init {
        this.view = view
        this.userModel = userModel
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }


    override fun requestNewLocation(isLoadingShown: Boolean) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
                AlRequest.POST(Api.update_location(), context, object : AlRequest.OnPostRequest{
                    override fun onSuccess(response: JSONObject?) {
                        view.onHideLoading()
                        try {
                            if (response!!.getString("status").equals("ok")) {
                                view!!.onRequestNewLocation(response.getString("message"))
                            } else {
                                view!!.onError(response.getString("message"))
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(error: String?) {
                        view!!.onHideLoading()
                        view!!.onError(error)
                    }

                    override fun onPreExecuted() {
                        if(isLoadingShown)
                            view!!.onLoading()
                    }

                    override fun requestParam(): MutableMap<String, String> {
                        val param = DataConstant.headerRequest()
                        param["latitude"] = ""+location!!.latitude
                        param["longitude"] = ""+location!!.longitude
                        return param
                    }

                    override fun requestHeaders(): MutableMap<String, String> {
                        val param = HashMap<String, String>()
                        param["token"] = userModel.token
                        return param
                    }

                })
        }
    }

    override fun setAccessPermission(access: String) {
        AlRequest.POST(Api.update_access_permission(), context, object : AlRequest.OnPostRequest{
            override fun onSuccess(response: JSONObject?) {
                try {
                    if (response!!.getString("status").equals("ok")) {
                        if(access.equals("1")){
                            requestNewLocation(false)
                        } else {
                            view.onHideLoading()
                            view!!.onRequestNewLocation(response.getString("message"))
                        }
                    } else {
                        view!!.onError(response.getString("message"))
                    }
                } catch (e: JSONException) {
                    view.onHideLoading()
                    e.printStackTrace()
                }
            }

            override fun onFailure(error: String?) {
                view!!.onHideLoading()
                view!!.onError(error)
            }

            override fun onPreExecuted() {
                view!!.onLoading()
            }

            override fun requestParam(): MutableMap<String, String> {
                val param = DataConstant.headerRequest()
                param["isLocation"] = ""+access
                return param
            }

            override fun requestHeaders(): MutableMap<String, String> {
                val param = HashMap<String, String>()
                param["token"] = userModel.token
                return param
            }

        })
    }


}