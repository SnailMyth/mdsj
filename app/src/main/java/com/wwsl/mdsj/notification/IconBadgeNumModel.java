package com.wwsl.mdsj.notification;

import android.app.Application;
import android.app.Notification;

import androidx.annotation.NonNull;

public interface IconBadgeNumModel {
    Notification setIconBadgeNum(@NonNull Application context, Notification notification, int count) throws Exception;
}
