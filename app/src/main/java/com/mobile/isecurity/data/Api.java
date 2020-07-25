package com.mobile.isecurity.data;

import com.mobile.isecurity.BuildConfig;

public class Api {
    static {
        System.loadLibrary("native-lib");
    }

    private static String base_api = BuildConfig.base_api;

    public static native String stringAPPKEY();

    public static String app_key() {
        return base_api + stringAPPKEY();
    }

    public static native String stringLogin();

    public static String login() {
        return base_api + stringLogin();
    }

    public static native String stringRegister();

    public static String register() {
        return base_api + stringRegister();
    }

    public static native String stringUpdateProfile();
    public static String update_profile() {
        return base_api + stringUpdateProfile();
    }

    public static native String stringUpdatePassword();
    public static String change_password() {
        return base_api + stringUpdatePassword();
    }

    public static native String stringForgotPassword();
    public static String forgot_password() {
        return base_api + stringForgotPassword();
    }

    public static native String stringLocation();
    public static String update_location() {
        return base_api + stringLocation();
    }

    public static native String stringAccessPermission();
    public static String update_access_permission() {
        return base_api + stringAccessPermission();
    }
}
