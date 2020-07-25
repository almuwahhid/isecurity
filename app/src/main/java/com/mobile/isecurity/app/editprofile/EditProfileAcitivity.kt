package com.mobile.isecurity.app.editprofile

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import com.google.gson.Gson
import com.mobile.isecurity.BuildConfig
import com.mobile.isecurity.R
import com.mobile.isecurity.app.forgotpassword.DialogForgotPassword
import com.mobile.isecurity.core.application.iSecurityActivityPermission
import com.mobile.isecurity.data.DataConstant
import com.mobile.isecurity.data.StringConstant
import com.mobile.isecurity.data.model.UserModel
import com.mobile.isecurity.util.DialogImagePicker
import com.mobile.isecurity.util.iSecurityUtil
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_edit_profile_acitivity.*
import kotlinx.android.synthetic.main.activity_edit_profile_acitivity.avatarview
import kotlinx.android.synthetic.main.activity_edit_profile_acitivity.edt_email
import kotlinx.android.synthetic.main.activity_edit_profile_acitivity.edt_name
import kotlinx.android.synthetic.main.activity_edit_profile_acitivity.edt_phone
import kotlinx.android.synthetic.main.activity_edit_profile_acitivity.spinner
import kotlinx.android.synthetic.main.toolbar_main.*
import lib.alframeworkx.Activity.ActivityGeneral
import lib.alframeworkx.Activity.Interfaces.PermissionResultInterface
import lib.alframeworkx.easyphotopicker.DefaultCallback
import lib.alframeworkx.easyphotopicker.EasyImage
import lib.alframeworkx.utils.AlStatic
import lib.alframeworkx.utils.VolleyMultipartRequest
import java.io.File
import java.util.ArrayList

class EditProfileAcitivity : iSecurityActivityPermission(), View.OnClickListener, EditProfileView.View {

    lateinit var user : UserModel
    var gson = Gson()
    lateinit var status : MutableList<String>
    lateinit var adapter : ArrayAdapter<String>
    lateinit var presenter : EditProfilePresenter

    var uri: Uri? = null
    var isPictOpened = false

    protected var RequiredPermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile_acitivity)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.title = "Edit Profile"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        user = iSecurityUtil.userLoggedIn(context!!, gson)!!
        presenter = EditProfilePresenter(context, this)

        status = ArrayList()
        status.add("+60")
        status.add("+62")
        status.add("+01")
        adapter = ArrayAdapter<String>(
            this,
            R.layout.simple_spinner_dropdown_item, status)
        spinner.setAdapter(adapter)
        initProfile(user, true)

        setFormsToValidate()

        btn_update.setOnClickListener({
            validate()
        })
        tv_change_photo.setOnClickListener(this)
        avatarview.setOnClickListener(this)
    }

    private fun validate() {
        if (AlStatic.isFormValid(this, window.decorView, forms, "Field Reqiured")) {
            val param = DataConstant.headerRequest()
            param["name"] = edt_name.text.toString()
            param["email"] = edt_email.text.toString()
            param["phone"] = edt_phone.text.toString()
            param["countryCode"] = spinner.selectedItem.toString()
            if(isPictOpened) {
                presenter.sendUpdateData(user.token, param, VolleyMultipartRequest.DataPart(uri!!.path, iSecurityUtil.getBytesFile(context, uri), iSecurityUtil.getTypeFile(context, uri!!)))
            } else {
                presenter.sendUpdateData(user.token, param, null)
            }

        }
    }

    internal var forms: ArrayList<Int> = ArrayList()
    private fun setFormsToValidate() {
        forms.add(R.id.edt_email)
        forms.add(R.id.edt_name)
        forms.add(R.id.edt_phone)
    }

    private fun initProfile(userModel: UserModel, needtoUpdatePhoto: Boolean){
        if(needtoUpdatePhoto){
            Picasso.with(context)
                .load(BuildConfig.base_image+user.profile_image)
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .fit()
                .into(avatarview)
        }

        edt_email.setText(userModel.email)
        edt_name.setText(userModel.name)
        edt_phone.setText(userModel.phone)
        spinner.setSelection(getSelectionIndexCode())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.change_password -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getSelectionIndexCode(): Int{
        for (i in 0 until status.size) {if(status.get(i).equals(user.countryCode)) return i else return 0}
        return 0
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.menu_password, menu)
        return true
    }

    override fun onClick(p0: View?) {
        if(p0!!.id == R.id.tv_change_photo || p0!!.id == R.id.avatarview){
            askCompactPermissions(RequiredPermissions, object : PermissionResultInterface {
                override fun permissionDenied() {

                }

                override fun permissionGranted() {
                    DialogImagePicker(context, object : DialogImagePicker.OnDialogImagePicker {
                        override fun onCameraClick() {
                            EasyImage.openCamera(this@EditProfileAcitivity, 0)
                        }

                        override fun onFileManagerClick() {
                            EasyImage.openGallery(this@EditProfileAcitivity, 0)
                        }

                    })
                }

            })
        }
    }

    private fun startCropActivity(uri: Uri) {
        CropImage.activity(uri)
//            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .setAspectRatio(1, 1)
            .start(this@EditProfileAcitivity)
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

    override fun onSuccessEditProfile(userModel: UserModel, message: String){
        userModel.token = user.token
        userModel.firebaseToken = userModel.firebaseToken
        AlStatic.setSPString(context, StringConstant.LOGIN_SP, gson.toJson(userModel))
        AlStatic.ToastShort(context, message)
        initProfile(userModel, false)
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
