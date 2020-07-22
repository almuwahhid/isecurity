package com.mobile.isecurity.util;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.util.Iterator;

public class iSecurityUtil {
    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager)context.getSystemService("activity");
        Iterator var3 = manager.getRunningServices(2147483647).iterator();

        ActivityManager.RunningServiceInfo service;
        do {
            if (!var3.hasNext()) {
                Log.i("isMyServiceRunning?", "false");
                return false;
            }

            service = (ActivityManager.RunningServiceInfo)var3.next();
        } while(!serviceClass.getName().equals(service.service.getClassName()));

        Log.i("isMyServiceRunning?", "true");
        return true;
    }
}
