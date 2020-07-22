package com.mobile.isecurity.app.accountmenu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import com.mobile.isecurity.R
import com.mobile.isecurity.app.changepassword.ChangePasswordActivity
import com.mobile.isecurity.app.editprofile.EditProfileAcitivity
import com.mobile.isecurity.app.login.LoginActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_account.view.*
import lib.alframeworkx.Activity.FragmentPermission

class AccountFragment : FragmentPermission() {

    companion object {
        fun newInstance(): AccountFragment {
            return AccountFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_account, container, false)

//        Picasso.with(context).load(R.drawable.ic_account_circle_black_24dp).fit().into(view.avatarview)
        view.avatarview.setImageResource(R.drawable.ic_account_circle_black_24dp)
        view.lay_editprofile.setOnClickListener({
            startActivity(Intent(context, EditProfileAcitivity::class.java))
        })
        view.lay_changepassword.setOnClickListener({
            startActivity(Intent(context, ChangePasswordActivity::class.java))
        })

        view.lay_signout.setOnClickListener({
            activity!!.finish()
            startActivity(Intent(context, LoginActivity::class.java))
        })
        return view
    }
}
