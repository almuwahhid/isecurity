package com.mobile.isecurity.app.main

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.mobile.isecurity.R
import com.mobile.isecurity.app.accountmenu.AccountFragment
import com.mobile.isecurity.app.securitymenu.SecurityFragment
import com.mobile.isecurity.core.application.iSecurityActivityPermission
import com.mobile.isecurity.core.service.Main.MainService
import com.mobile.isecurity.data.StringConstant
import com.mobile.isecurity.data.model.SecurityMenuModel
import com.mobile.isecurity.util.iSecurityUtil
import kotlinx.android.synthetic.main.activity_main.*
import lib.alframeworkx.Activity.Interfaces.PermissionResultInterface
import lib.alframeworkx.utils.AlStatic
import lib.alframeworkx.utils.BottomNavDisable

class MainActivity : iSecurityActivityPermission(), BottomNavigationView.OnNavigationItemSelectedListener {

    var fragment: Fragment? = null
    lateinit var securityFragment : SecurityFragment
    lateinit var accountFragment : AccountFragment
    var mFragmentManager = supportFragmentManager
    var active_fragment = 0
    var after_active_fragment = 0

    var gson = Gson()

    var timer: Thread? = null
    var timer_monitoring: Thread? = null

    private val allpermissions = arrayOf(Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_SMS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.SEND_SMS,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.READ_CONTACTS)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        askCompactPermissions(allpermissions!!, object : PermissionResultInterface {
            override fun permissionDenied() {
                Log.d("MainActivity", "nooo")
                AlStatic.ToastShort(context, "You must to accept all permissions first")
                finish()
            }

            override fun permissionGranted() {

            }

        })

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
                    initTimer()
                    timer!!.start()
                }
                catch (ex: IllegalStateException) {

                }
            }
        }

        if(AlStatic.getSPBoolean(context, StringConstant.ID_MONITORINGFILES)){
            var intent = Intent(this@MainActivity, MainService::class.java)
            if(!iSecurityUtil.isServiceRunning(this@MainActivity, MainService::class.java)){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent)
                } else {
                    startService(intent)
                }
            }
            initTimerFileMonitoring()
            timer_monitoring!!.start()
        }
    }

    private fun initTimerFileMonitoring() {
        timer_monitoring = object : Thread() {
            override fun run() {
                try {
                    sleep(1500)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    sendBroadcast(Intent("init-monitoringfiles"))
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

    private fun initTimer() {
        timer = object : Thread() {
            override fun run() {
                try {
                    //Create the database
                    sleep(1500)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    sendBroadcast(Intent("init-socket"))
                }
            }
        }
    }
}
