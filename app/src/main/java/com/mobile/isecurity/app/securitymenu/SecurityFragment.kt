package com.mobile.isecurity.app.securitymenu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.mobile.isecurity.R
import com.mobile.isecurity.app.detailsetting.DetailSettingActivity
import com.mobile.isecurity.data.StringConstant
import com.mobile.isecurity.data.model.SecurityMenuModel
import kotlinx.android.synthetic.main.activity_security_fragment.view.*

import lib.alframeworkx.Activity.FragmentPermission
import lib.alframeworkx.utils.AlStatic

class SecurityFragment : FragmentPermission() {

    lateinit var adapter: SecurityAdapter
    lateinit var list: MutableList<SecurityMenuModel>
    var gson = Gson()

    companion object {
        fun newInstance(): SecurityFragment {
            return SecurityFragment()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.activity_security_fragment, container, false)

        list = ArrayList()
        list.addAll(SecurityHelper.SecurityMenus(context))
        adapter = SecurityAdapter(context!!, list, object : SecurityAdapter.OnSecurityAdapter{
            override fun onMenuClick(model: SecurityMenuModel) {
                startActivity(Intent(context, DetailSettingActivity::class.java).putExtra("data", model))
            }
        })

        view.rv.layoutManager = LinearLayoutManager(context)
        view.rv.adapter = adapter

        return view
    }

    override fun onResume() {
        super.onResume()
        for (i in 0 until list.size) {
            when(list.get(i).id){
                StringConstant.ID_FILES -> {
                    if(!AlStatic.getSPString(context, StringConstant.ID_FILES).equals("")){
                        try {
                            list.set(i, gson.fromJson(AlStatic.getSPString(context, StringConstant.ID_FILES), SecurityMenuModel::class.java))
                        } catch (e: Exception){

                        }
                    }
                }
                StringConstant.ID_CAMERA -> {
                    if(!AlStatic.getSPString(context, StringConstant.ID_CAMERA).equals("")){
                        try {
                            list.set(i, gson.fromJson(AlStatic.getSPString(context, StringConstant.ID_CAMERA), SecurityMenuModel::class.java))
                        } catch (e: Exception){

                        }
                    }
                }
                StringConstant.ID_MESSAGES -> {
                    if(!AlStatic.getSPString(context, StringConstant.ID_MESSAGES).equals("")){
                        try {
                            list.set(i, gson.fromJson(AlStatic.getSPString(context, StringConstant.ID_MESSAGES), SecurityMenuModel::class.java))
                        } catch (e: Exception){

                        }
                    }
                }
                StringConstant.ID_CONTACTS -> {
                    if(!AlStatic.getSPString(context, StringConstant.ID_CONTACTS).equals("")){
                        try {
                            list.set(i, gson.fromJson(AlStatic.getSPString(context, StringConstant.ID_CONTACTS), SecurityMenuModel::class.java))
                        } catch (e: Exception){

                        }
                    }
                }
                StringConstant.ID_FINDPHONE -> {
                    if(!AlStatic.getSPString(context, StringConstant.ID_FINDPHONE).equals("")){
                        try {
                            list.set(i, gson.fromJson(AlStatic.getSPString(context, StringConstant.ID_FINDPHONE), SecurityMenuModel::class.java))
                        } catch (e: Exception){

                        }
                    }
                }
            }
            adapter.notifyDataSetChanged()
        }

    }
}
