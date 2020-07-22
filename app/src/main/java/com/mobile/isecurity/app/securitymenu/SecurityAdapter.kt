package com.mobile.isecurity.app.securitymenu

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobile.isecurity.R
import com.mobile.isecurity.data.model.SecurityMenuModel
import kotlinx.android.synthetic.main.adapter_security.view.*

class SecurityAdapter (context: Context, list: MutableList<SecurityMenuModel>, private val onSecurityAdapter: SecurityAdapter.OnSecurityAdapter) : RecyclerView.Adapter<SecurityAdapter.Holder>() {

    var list: MutableList<SecurityMenuModel>

    init {
        this.list = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SecurityAdapter.Holder {
        val layoutView: View
        layoutView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_security, parent, false)
        return Holder(layoutView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: SecurityAdapter.Holder, position: Int) {
        holder.bindTo(list.get(position), onSecurityAdapter)
    }


    interface OnSecurityAdapter{
        fun onMenuClick(model: SecurityMenuModel)
    }

    class Holder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindTo(data: SecurityMenuModel, onSecurityAdapter: OnSecurityAdapter): Unit = with(itemView) {

            card_menu.setOnClickListener({
                onSecurityAdapter.onMenuClick(data)
            })

            img_menu.setImageResource(data.icon)
            tv_title.setText(data.title)
            tv_subtitle.setText(data.subtitle)

            if(data.status == 0){
                tv_status.setBackground(resources.getDrawable(R.drawable.bg_btn_off))
                tv_status.setTextColor(resources.getColor(R.color.grey_700))
                tv_status.setText("OFF")
            } else {
                tv_status.setBackground(resources.getDrawable(R.drawable.bg_btn_on))
                tv_status.setTextColor(resources.getColor(R.color.white))
                tv_status.setText("ON")
            }
        }
    }
}
