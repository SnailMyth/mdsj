package com.wwsl.mdsj.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;

import androidx.annotation.NonNull;

import com.frame.fire.util.LogUtils;
import com.umeng.umcrash.UMCrash;
import com.wwsl.mdsj.activity.common.ActivityManager;

import java.util.Arrays;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private final static String TAG = "CrashHandler";

    private static final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String FILE_NAME_SUFFIX = ".trace";
    private static CrashHandler sInstance = new CrashHandler();
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;
    private Context mContext;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return sInstance;
    }

    public void init(@NonNull Context context) {
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }

    /**
     * 当程序中有未被捕获的异常，系统将会调用这个方法
     *
     * @param t 出现未捕获异常的线程
     * @param e 得到异常信息
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        try {
            //保存到本地
            exportException(e);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        e.printStackTrace();

        //如果系统提供了默认的异常处理器，则交给系统去结束程序，否则就自己结束自己

        ActivityManager.getInstance().finishAllActivity();

        Process.killProcess(Process.myPid());
    }

    /**
     * 导出异常信息到SD卡
     *
     * @param e
     */
    private void exportException(@NonNull Throwable e) throws PackageManager.NameNotFoundException {

        String log = appendPhoneInfo() + e.getMessage();

        UMCrash.generateCustomLog(log, "Android");
        LogUtils.e(TAG, "global exception: " + log);

    }

    /**
     * 获取手机信息
     */
    private String appendPhoneInfo() throws PackageManager.NameNotFoundException {
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        StringBuilder sb = new StringBuilder();
        //App版本
        sb.append("App Version: ");
        sb.append(pi.versionName);
        sb.append("_");
        sb.append(pi.getLongVersionCode()).append("\n");

        //Android版本号
        sb.append("OS Version: ");
        sb.append(Build.VERSION.RELEASE);
        sb.append("_");
        sb.append(Build.VERSION.SDK_INT).append("\n");

        //手机制造商
        sb.append("Vendor: ");
        sb.append(Build.MANUFACTURER).append("\n");

        //手机型号
        sb.append("Model: ");
        sb.append(Build.MODEL).append("\n");

        //CPU架构
        sb.append("CPU: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sb.append(Arrays.toString(Build.SUPPORTED_ABIS));
        } else {
            sb.append(Build.CPU_ABI);
        }

        return sb.toString();
    }
}