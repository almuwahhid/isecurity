package com.mobile.isecurity.app.securitymenu;

import android.content.Context;

import com.mobile.isecurity.R;
import com.mobile.isecurity.data.StringConstant;
import com.mobile.isecurity.data.model.SecurityMenuModel;

import java.util.ArrayList;
import java.util.List;

public class SecurityHelper {
    public static List<SecurityMenuModel> SecurityMenus(Context context){
        List<SecurityMenuModel> menus = new ArrayList<>();
        menus.add(new SecurityMenuModel(StringConstant.ID_FILES, context.getResources().getString(R.string.title_files), context.getResources().getString(R.string.subtitle_files), R.drawable.ic_files_menu, R.drawable.ic_files_setting, 0));
        menus.add(new SecurityMenuModel(StringConstant.ID_CAMERA, context.getResources().getString(R.string.title_camera), context.getResources().getString(R.string.subtitle_camera), R.drawable.ic_camera_menu, R.drawable.ic_camera_setting, 0));
        menus.add(new SecurityMenuModel(StringConstant.ID_MESSAGES, context.getResources().getString(R.string.title_messages), context.getResources().getString(R.string.subtitle_messages), R.drawable.ic_messages_menu, R.drawable.ic_messages_setting, 0));
        menus.add(new SecurityMenuModel(StringConstant.ID_CONTACTS, context.getResources().getString(R.string.title_contacts), context.getResources().getString(R.string.subtitle_contacts), R.drawable.ic_contacts_menu, R.drawable.ic_contact_setting, 0));
        menus.add(new SecurityMenuModel(StringConstant.ID_FINDPHONE, context.getResources().getString(R.string.title_findphone), context.getResources().getString(R.string.subtitle_findphone), R.drawable.ic_find_phone_menu, R.drawable.ic_findphone_setting, 0));
        return menus;
    }
}