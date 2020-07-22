package com.mobile.isecurity.app.register

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.mobile.isecurity.R
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.toolbar_main.*
import lib.alframeworkx.Activity.ActivityPermission

class RegisterActivity : ActivityPermission() {

    val postalcodes = arrayOf(
        "+60",
        "+62",
        "+01"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.title = "Register"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        /*val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.select_dialog_singlechoice,
            postalcodes
        )
        postalcode.threshold = 1
        postalcode.setAdapter(adapter)*/

        val dayValues = resources.getStringArray(R.array.zipcode)
        val adapter: ArrayAdapter<String> = object : ArrayAdapter<String>(
            this,
            R.layout.spinner_item, dayValues) {
            override fun getCount(): Int {
                val c = super.getCount()
                if (spinner.selectedItemPosition < c - 1) return c
                return if (c > 0) c - 1 else c
            }
        }
        spinner.setAdapter(adapter)
//        spinner.setSelection(0)
        spinner.setSelection(dayValues.size - 1);

        avatarview.setImageResource(R.drawable.ic_account_circle_black_24dp)
        btn_register.setOnClickListener({
            finish()
        })
    }
}
