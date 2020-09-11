package com.mobile.isecurity.app.detailsetting.presenter

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.os.AsyncTask
import android.provider.ContactsContract
import com.mobile.isecurity.app.detailsetting.DetailSettingView
import com.mobile.isecurity.data.Api
import com.mobile.isecurity.data.DataConstant
import com.mobile.isecurity.data.model.Contact.ContactModel
import com.mobile.isecurity.data.model.UserModel
import lib.alframeworkx.utils.AlRequest
import org.json.JSONException
import org.json.JSONObject
import java.lang.IllegalStateException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set


class ContactPermissionPresenter(context: Context, userModel: UserModel, view: DetailSettingView.View) : DetailSettingPresenter(context), DetailSettingView.PresenterContact {

    var view: DetailSettingView.View
    var userModel: UserModel

    init {
        this.view = view
        this.userModel = userModel
    }

    override fun requestContact(isLoadingShown: Boolean) {
        ContactListRequest(context, object: OnAfterRequestContact{
            override fun afterRequestContact(result: MutableList<ContactModel>) {
                AlRequest.POST(Api.update_contacts(), context, object : AlRequest.OnPostRequest{
                    override fun onSuccess(response: JSONObject?) {
                        view.onHideLoading()
                        try {
                            if (response!!.getString("status").equals("ok")) {
                                view!!.onRequestNewContact(true, response.getString("message"))
                            } else {
                                view!!.onRequestNewContact(false, response.getString("message"))
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(error: String?) {
                        view!!.onHideLoading()
                        view!!.onError(error)
                    }

                    override fun onPreExecuted() {
                        if(isLoadingShown)
                            view!!.onLoading()
                    }

                    override fun requestParam(): MutableMap<String, String> {
                        val param = DataConstant.headerRequest()
                        param["phoneNumbers"] = gson.toJson(result)
                        return param
                    }

                    override fun requestHeaders(): MutableMap<String, String> {
                        val param = HashMap<String, String>()
                        param["token"] = userModel.token
//                        param["Content-Type"] = DataConstant.CONTENT_TYPE
                        return param
                    }

                })
            }
        }).execute()
    }

    override fun setAccessPermission(access: String) {
        super.setAccessPermission(access)
        AlRequest.POST(Api.update_access_permission(), context, object : AlRequest.OnPostRequest{
            override fun onSuccess(response: JSONObject?) {
                try {
                    if (response!!.getString("status").equals("ok")) {
                        if(access.equals("1")){
                            requestContact(false)
                        } else {
                            view.onHideLoading()
                            view!!.onRequestNewLocation(true, response.getString("message"))
                        }
                    } else {
                        view!!.onError(response.getString("message"))
                    }
                } catch (e: JSONException) {
                    view.onHideLoading()
                    e.printStackTrace()
                }
            }

            override fun onFailure(error: String?) {
                view!!.onHideLoading()
                view!!.onError(error)
            }

            override fun onPreExecuted() {
                view!!.onLoading()
            }

            override fun requestParam(): MutableMap<String, String> {
                val param = DataConstant.headerRequest()
                param["isContacts"] = ""+access
                return param
            }

            override fun requestHeaders(): MutableMap<String, String> {
                val param = HashMap<String, String>()
                param["token"] = userModel.token
//                param["Content-Type"] = DataConstant.CONTENT_TYPE
                return param
            }

        })
    }

    private class ContactListRequest(context: Context, onAfterRequestContact : OnAfterRequestContact) : AsyncTask<String, String, MutableList<ContactModel>>() {

        val onAfterRequestContact : OnAfterRequestContact
        val context : Context
        init {
            this.onAfterRequestContact = onAfterRequestContact
            this.context = context
        }

        override fun doInBackground(vararg p0: String?): MutableList<ContactModel> {
            var result: MutableList<ContactModel> = ArrayList()
            val cr: ContentResolver = context.getContentResolver()
            val cur: Cursor? = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
            if (cur?.count ?: 0 > 0) {
                while (cur != null && cur.moveToNext()) {
                    val contactModel = ContactModel()
                    val id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID))

                    try {
//                        contactModel.name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                        contactModel.name = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    } catch (e : IllegalStateException){

                    }


                    if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        val pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", arrayOf(id), null)
                        val array_num = ArrayList<String>()
                        while (pCur!!.moveToNext()) {
                            val phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            array_num.add(phoneNo)
                        }
                        pCur.close()
                        contactModel.phone = array_num
                    }

                    val curEmail = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", arrayOf(id), null)
                    val array_email = ArrayList<String>()
                    while (curEmail!!.moveToNext()) {
//                        val name: String = curEmail.getString(curEmail.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                        val email: String = curEmail.getString(curEmail.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                        array_email.add(email)
                    }
//                    if(array_email.size == 0)
//                        array_email.add("")

                    contactModel.email = array_email
                    curEmail.close()

                    if(contactModel.phone.size > 0){
//                        if(result.size < 1){
//                            result.add(contactModel)
//                        }
                        result.add(contactModel)
                    }
                }
                cur!!.close()
            }
            return result
        }

        override fun onPostExecute(result: MutableList<ContactModel>?) {
            super.onPostExecute(result)
            onAfterRequestContact.afterRequestContact(result!!)
        }
    }

    private interface OnAfterRequestContact{
        fun afterRequestContact(result: MutableList<ContactModel>)
    }
}