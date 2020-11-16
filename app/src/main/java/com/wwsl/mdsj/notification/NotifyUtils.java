package com.wwsl.mdsj.notification;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

public class NotifyUtils {
    public static final String UNABLE_TO_RESOLVE_INTENT_ERROR_ = "unable to resolve intent: ";

    private static NotifyUtils instance;

    public static NotifyUtils getInstance() {
        if (instance == null) {
            instance = new NotifyUtils();
        }
        return instance;
    }

    public boolean canResolveBroadcast(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> receivers = packageManager.queryBroadcastReceivers(intent, 0);
        return receivers != null && receivers.size() > 0;
    }

    public String getLaunchIntentForPackage(Context context) {
        return context.getPackageManager().getLaunchIntentForPackage(context.getPackageName()).getComponent().getClassName();
    }
}
