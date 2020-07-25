package com.mobile.isecurity.app.accountmenu

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import com.google.gson.Gson
import com.mobile.isecurity.BuildConfig
import com.mobile.isecurity.R
import com.mobile.isecurity.app.changepassword.ChangePasswordActivity
import com.mobile.isecurity.app.editprofile.EditProfileAcitivity
import com.mobile.isecurity.app.login.LoginActivity
import com.mobile.isecurity.data.model.UserModel
import com.mobile.isecurity.util.iSecurityUtil
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_account.view.*
import lib.alframeworkx.Activity.FragmentPermission
import lib.alframeworkx.utils.AlertDialogBuilder

class AccountFragment : FragmentPermission() {

    companion object {
        fun newInstance(): AccountFragment {
            return AccountFragment()
        }
    }

    lateinit var user : UserModel;
    var gson = Gson()

    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_account, container, false)
//        view.avatarview.setImageResource(R.drawable.ic_account_circle_black_24dp)
        view.lay_editprofile.setOnClickListener({
            startActivity(Intent(context, EditProfileAcitivity::class.java))
        })
        view.lay_changepassword.setOnClickListener({
            startActivity(Intent(context, ChangePasswordActivity::class.java))
        })

        view.lay_signout.setOnClickListener({
            AlertDialogBuilder(context,
                "Are you sure?",
                "Yes",
                "No", object : AlertDialogBuilder.OnAlertDialog{
                    override fun onPositiveButton(dialog: DialogInterface?) {
                        iSecurityUtil.logout(context!!)
                        activity!!.finish()
                        startActivity(Intent(context, LoginActivity::class.java))
                    }

                    override fun onNegativeButton(dialog: DialogInterface?) {

                    }

                })

        })
        return view
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    private fun initData(){
        user = iSecurityUtil.userLoggedIn(context!!, gson)!!
        Picasso.with(context)
            .load(BuildConfig.base_image+user.profile_image)
            .placeholder(R.drawable.ic_account_circle_black_24dp)
            .fit()
            .into(view!!.avatarview)
        view!!.tv_name.setText(user.name)
        view!!.tv_email.setText(user.email)
    }
}
