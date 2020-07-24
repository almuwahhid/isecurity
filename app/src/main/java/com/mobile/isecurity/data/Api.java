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
}
