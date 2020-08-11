package com.mobile.isecurity.app.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.mobile.isecurity.R
import com.mobile.isecurity.app.accountmenu.AccountFragment
import com.mobile.isecurity.app.securitymenu.SecurityFragment
import com.mobile.isecurity.core.application.iSecurityActivity
import com.mobile.isecurity.core.service.MainService
import com.mobile.isecurity.data.StringConstant
import com.mobile.isecurity.data.model.SecurityMenuModel
import com.mobile.isecurity.util.iSecurityUtil
import kotlinx.android.synthetic.main.activity_main.*
import lib.alframeworkx.Activity.ActivityGeneral
import lib.alframeworkx.utils.AlStatic
import lib.alframeworkx.utils.BottomNavDisable

class MainActivity : iSecurityActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    var fragment: Fragment? = null
    lateinit var securityFragment : SecurityFragment
    lateinit var accountFragment : AccountFragment
    var mFragmentManager = supportFragmentManager
    var active_fragment = 0
    var after_active_fragment = 0

    var gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        BottomNavDisable.disableShiftMode(navigation)

        securityFragment = SecurityFragment.newInstance()
        accountFragment = AccountFragment.newInstance()
        if (savedInstanceState != null) {
            fragment = supportFragmentManager.getFragment(savedInstanceState, "fragment")
        } else {
            fragment = securityFragment
        }

        initializeNavFragment(fragment!!)
        navigation.setOnNavigationItemSelectedListener(this)

        if(!AlStatic.getSPString(context, StringConstant.ID_CAMERA).equals("")){
            val securityMenuModel = gson.fromJson(AlStatic.getSPString(context, StringConstant.ID_CAMERA), SecurityMenuModel::class.java)
            if(securityMenuModel.status == 1){
                try {
                    var intent = Intent(this@MainActivity, MainService::class.java)
                    if(!iSecurityUtil.isServiceRunning(this@MainActivity, MainService::class.java)){
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
        }
    }

    private fun initializeNavFragment(curFragment: Fragment) {
        val transaction: FragmentTransaction = mFragmentManager.beginTransaction()
        if (mFragmentManager.findFragmentByTag(curFragment::class.java.simpleName) == null) {
            transaction.add(R.id.contentContainer, curFragment, curFragment.javaClass.getSimpleName())
        }
        val tagSecurity = mFragmentManager.findFragmentByTag(securityFragment.javaClass.getSimpleName())
        val tagAccount = mFragmentManager.findFragmentByTag(accountFragment.javaClass.getSimpleName())

        hideFragment(transaction, tagSecurity, tagAccount)
        showFragment(curFragment, transaction, tagSecurity, tagAccount)
        after_active_fragment = active_fragment
        transaction.commitAllowingStateLoss()
    }

    private fun initActiveFragment(a: Int) {
        active_fragment = a
    }

    private fun showFragment(
        curFragment: Fragment,
        transaction: FragmentTransaction,
        tagSecurity: Fragment?,
        tagAccount: Fragment?
    ) {
        if (curFragment::class.java.simpleName.equals(securityFragment.javaClass.getSimpleName())
        ) {
            if (tagSecurity != null) {
                transaction.show(tagSecurity)
            }
        }
        if (curFragment::class.java.simpleName.equals(accountFragment.javaClass.getSimpleName()))
            if (tagAccount != null) {
                transaction.show(tagAccount)
            }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.nav_security -> {
                initActiveFragment(0)
                fragment = securityFragment
            }
            R.id.nav_account -> {
                initActiveFragment(1)
                fragment = accountFragment
            }
        }
        initializeNavFragment(fragment!!)
        return true
    }

    private fun hideFragment(transaction: FragmentTransaction, tagSecurity: Fragment?, tagAccount: Fragment?) {
        if (tagSecurity != null) {
//            initAnimNav(transaction);
            transaction.hide(tagSecurity)
        }
        if (tagAccount != null) {
//            initAnimNav(transaction);
            transaction.hide(tagAccount)
        }
    }
}
