package com.mobile.isecurity.app.register

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.mobile.isecurity.R
import com.mobile.isecurity.core.application.iSecurityActivityPermission
import com.mobile.isecurity.data.DataConstant
import com.mobile.isecurity.util.DialogImagePicker
import com.mobile.isecurity.util.iSecurityUtil
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.toolbar_main.*
import lib.alframeworkx.Activity.Interfaces.PermissionResultInterface
import lib.alframeworkx.easyphotopicker.DefaultCallback
import lib.alframeworkx.easyphotopicker.EasyImage
import lib.alframeworkx.utils.AlStatic
import lib.alframeworkx.utils.VolleyMultipartRequest
import java.io.File
import java.util.*


class RegisterActivity : iSecurityActivityPermission(), RegisterView.View, View.OnClickListener {


    lateinit var presenter: RegisterPresenter
    protected var RequiredPermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    var uri: Uri? = null
    var isPictOpened = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.title = "Register"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        presenter = RegisterPresenter(context, this)

        setFormsToValidate()
//        val status: MutableList<String> = ArrayList()
//        status.add("+60")
//        status.add("+62")
//        status.add("+01")

        /*val adapter = ArrayAdapter<String>(
            this,
            R.layout.simple_spinner_dropdown_item, status)*/
//        spinner.setAdapter(adapter)

        ccp.setOnCountryChangeListener { selectedCountry ->
//            Toast.makeText(
//                context,
//                "Updated " + selectedCountry.name,
//                Toast.LENGTH_SHORT
//            ).show()
        }

        avatarview.setImageResource(R.drawable.ic_account_circle_black_24dp)
        btn_register.setOnClickListener({
            validate()
        })

        tv_change_photo.setOnClickListener(this)
        avatarview.setOnClickListener(this)
    }

    internal var forms: ArrayList<Int> = ArrayList()
    private fun setFormsToValidate() {
        forms.add(R.id.edt_email)
        forms.add(R.id.edt_password)
        forms.add(R.id.edt_name)
        forms.add(R.id.edt_phone)
    }

    private fun validate() {
        if (AlStatic.isFormValid(this, window.decorView, forms, "Field Required")) {
            val param = DataConstant.headerRequest()
            param["name"] = edt_name.text.toString()
            param["email"] = edt_email.text.toString()
            param["phone"] = edt_phone.text.toString()
            param["password"] = edt_password.text.toString()
//            param["countryCode"] = spinner.selectedItem.toString()
            param["countryCode"] = "+"+ccp.selectedCountryCode
            if(isPictOpened) {
                presenter.sendRegisterData(param, VolleyMultipartRequest.DataPart(uri!!.path, iSecurityUtil.getBytesFile(context, uri), iSecurityUtil.getTypeFile(context, uri!!)))
            } else {
                presenter.sendRegisterData(param, null)
            }

        }
    }

    override fun onSuccessRegister(message: String) {
        AlStatic.ToastShort(context, message)
        finish()
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

    private fun startCropActivity(uri: Uri) {
        CropImage.activity(uri)
//            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .setAspectRatio(1, 1)
            .start(this@RegisterActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                Log.d("ikiopo", "")
                uri = result.uri
                isPictOpened = true

//                val bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri)
                Picasso.with(context)
                    .load(uri)
                    .fit()
                    .into(avatarview)

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error.toString()
                AlStatic.ToastShort(context, "" + error)
            }
        } else {
            EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), object : DefaultCallback(){
                override fun onImagesPicked(imageFiles: MutableList<File>, source: EasyImage.ImageSource?, type: Int) {
                    startCropActivity(Uri.fromFile(imageFiles[0]))
                }
            })
        }
    }

    override fun onClick(p0: View?) {
        if(p0!!.id == R.id.tv_change_photo || p0!!.id == R.id.avatarview){
            askCompactPermissions(RequiredPermissions, object : PermissionResultInterface {
                override fun permissionDenied() {

                }

                override fun permissionGranted() {
                    DialogImagePicker(context, object : DialogImagePicker.OnDialogImagePicker {
                        override fun onCameraClick() {
                            EasyImage.openCamera(this@RegisterActivity, 0)
                        }

                        override fun onFileManagerClick() {
                            EasyImage.openGallery(this@RegisterActivity, 0)
                        }

                    })
                }

            })
        }
    }
}
