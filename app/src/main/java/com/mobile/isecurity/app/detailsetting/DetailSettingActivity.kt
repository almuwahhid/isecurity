package com.mobile.isecurity.app.detailsetting

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationListener
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.mobile.isecurity.R
import com.mobile.isecurity.app.detailsetting.presenter.CameraPermissionPresenter
import com.mobile.isecurity.app.detailsetting.presenter.FilePermissionPresenter
import com.mobile.isecurity.app.detailsetting.presenter.LocationPermissionPresenter
import com.mobile.isecurity.app.detailsetting.presenter.SMSPermissionPresenter
import com.mobile.isecurity.core.application.iSecurityActivityPermission
import com.mobile.isecurity.core.service.MainService
import com.mobile.isecurity.data.StringConstant
import com.mobile.isecurity.data.model.SecurityMenuModel
import com.mobile.isecurity.data.model.UserModel
import com.mobile.isecurity.util.iSecurityUtil
import kotlinx.android.synthetic.main.activity_detail_setting.*
import kotlinx.android.synthetic.main.toolbar_main.*
import lib.alframeworkx.Activity.Interfaces.PermissionResultInterface
import lib.alframeworkx.utils.AlStatic
import lib.alframeworkx.utils.AlertDialogBuilder

class DetailSettingActivity : iSecurityActivityPermission(), DetailSettingView.View {

    lateinit var securityMenuModel: SecurityMenuModel
    var gson = Gson()

    private val CameraPermissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    private val FilePermissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val SMSPermissions = arrayOf(Manifest.permission.READ_SMS)
    private val LocationPermissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    private val ContactPermissions = arrayOf(Manifest.permission.READ_CONTACTS)

    lateinit var userModel: UserModel

    lateinit var presenterLocation : LocationPermissionPresenter
    lateinit var presenterFile : FilePermissionPresenter
    lateinit var presenterSMS : SMSPermissionPresenter
    lateinit var presenterCamera : CameraPermissionPresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_setting)

        if(intent.hasExtra("data")){
            securityMenuModel = intent.getSerializableExtra("data") as SecurityMenuModel
            userModel = iSecurityUtil.userLoggedIn(context, gson)!!
            presenterLocation = LocationPermissionPresenter(context, userModel, this)
        } else {
            finish()
        }

        setComponent()

        btn_enable.setOnClickListener({
            val message = "Are you sure to "+(if(securityMenuModel.status == 0) "Enable" else "Disable")+" "+securityMenuModel.title+" Permissions ?"
            AlertDialogBuilder(context, message, "Yes", "No", object  : AlertDialogBuilder.OnAlertDialog{
                override fun onPositiveButton(dialog: DialogInterface?) {
//                    securityMenuModel.status = if(securityMenuModel.status == 0) 1 else 0
//                    AlStatic.setSPString(context, securityMenuModel.id, gson.toJson(securityMenuModel))
//                    initEnableComponent(securityMenuModel.status)
                    ask(securityMenuModel.id)
//                    if(securityMenuModel.status == 1){
//                        ask(securityMenuModel.id)
//                    } else {
//                        stop(securityMenuModel.id)
//                    }
                }

                override fun onNegativeButton(dialog: DialogInterface?) {

                }

            })
        })
    }

    fun setComponent(){
        switch_enable.isClickable = false
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.title = securityMenuModel.title
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        img_setting.setImageResource(securityMenuModel.background)

        tv_title.setText(securityMenuModel.title)
        tv_subtitle.setText(securityMenuModel.subtitle)

        initEnableComponent(securityMenuModel.status)
    }

    fun initEnableComponent(enabled : Int){
        if(enabled == 0){
            switch_enable.isChecked = false
            btn_enable.setBackground(resources.getDrawable(R.drawable.button_main))
            btn_enable.setText("ENABLE")
        } else {
            switch_enable.isChecked = true
            btn_enable.setBackground(resources.getDrawable(R.drawable.button_disable))
            btn_enable.setText("DISABLE")

        }
    }

    fun stop(id_security : String){
        when(id_security){
            StringConstant.ID_FINDPHONE -> {

            }
            StringConstant.ID_CONTACTS -> {

            }
            StringConstant.ID_MESSAGES -> {

            }
            StringConstant.ID_FILES -> {

            }
            StringConstant.ID_CAMERA -> {
                sendBroadcast(Intent("stopservice"))
            }
        }
    }

    fun ask(id_security : String){
        when(id_security){
            StringConstant.ID_FINDPHONE -> {
                askCompactPermissions(LocationPermissions!!, object : PermissionResultInterface{
                    override fun permissionDenied() {

                    }

                    override fun permissionGranted() {
                        presenterLocation.setAccessPermission(if(securityMenuModel.status == 0) ""+1 else ""+0)
                    }

                })
            }
            StringConstant.ID_CONTACTS -> {
                askCompactPermissions(ContactPermissions!!, object : PermissionResultInterface{
                    override fun permissionDenied() {

                    }

                    override fun permissionGranted() {
                        securityMenuModel.status = if(securityMenuModel.status == 0) 1 else 0
                        AlStatic.setSPString(context, securityMenuModel.id, gson.toJson(securityMenuModel))
                        initEnableComponent(securityMenuModel.status)
                    }

                })
            }
            StringConstant.ID_MESSAGES -> {
                askCompactPermissions(SMSPermissions!!, object : PermissionResultInterface{
                    override fun permissionDenied() {

                    }

                    override fun permissionGranted() {
                        securityMenuModel.status = if(securityMenuModel.status == 0) 1 else 0
                        AlStatic.setSPString(context, securityMenuModel.id, gson.toJson(securityMenuModel))
                        initEnableComponent(securityMenuModel.status)
                    }

                })
            }
            StringConstant.ID_FILES -> {
                askCompactPermissions(FilePermissions!!, object : PermissionResultInterface{
                    override fun permissionDenied() {

                    }

                    override fun permissionGranted() {
                        securityMenuModel.status = if(securityMenuModel.status == 0) 1 else 0
                        AlStatic.setSPString(context, securityMenuModel.id, gson.toJson(securityMenuModel))
                        initEnableComponent(securityMenuModel.status)
                    }

                })
            }
            StringConstant.ID_CAMERA -> {
                askCompactPermissions(CameraPermissions!!, object : PermissionResultInterface{
                    override fun permissionDenied() {

                    }

                    override fun permissionGranted() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if(!Settings.canDrawOverlays(this@DetailSettingActivity)){
                                val intent = Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:$packageName")
                                )
                                startActivityForResult(intent, 0)
                            }
                        }

                        try {
                            var intent = Intent(this@DetailSettingActivity, MainService::class.java)
                            if(!iSecurityUtil.isServiceRunning(this@DetailSettingActivity, MainService::class.java)){
                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    startForegroundService(intent)
                                } else {
                                    startService(intent)
                                }
                            }
                        }
                        catch (ex: IllegalStateException) {

                        }
                    }

                })
            }
        }
    }

    override fun onRequestNewLocation(message: String) {
        AlStatic.ToastShort(context, message)
        securityMenuModel.status = if(securityMenuModel.status == 0) 1 else 0
        AlStatic.setSPString(context, securityMenuModel.id, gson.toJson(securityMenuModel))
        initEnableComponent(securityMenuModel.status)
    }

    override fun onHideLoading() {
        AlStatic.hideLoadingDialog(context)
    }

    override fun onLoading() {
        AlStatic.showLoadingDialog(context, R.drawable.ic_logo)
    }

    override fun onError(message: String?) {
        AlStatic.ToastShort(context, message)
    }

}
