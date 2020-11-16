package com.wwsl.mdsj.utils;

import com.frame.fire.util.LogUtils;

import java.lang.reflect.Field;

public class SecurityUtil {
    private static final String XPOSED_HELPERS = "de.robv.android.xposed.XposedHelpers";
    private static final String XPOSED_BRIDGE = "de.robv.android.xposed.XposedBridge";

    //手动抛出异常，检查堆栈信息是否有xp框架包
    public static boolean isEposedExistByThrow() {
        try {
            throw new Exception("gg");
        } catch (Exception e) {
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                if (stackTraceElement.getClassName().contains(XPOSED_BRIDGE)) return true;
            }
            return false;
        }
    }

    //检查xposed包是否存在
    public static boolean isXposedExists() {
        try {
            Object xpHelperObj = ClassLoader
                    .getSystemClassLoader()
                    .loadClass(XPOSED_HELPERS)
                    .newInstance();
        } catch (InstantiationException | IllegalAccessException e) {//实测debug跑到这里报异常
            e.printStackTrace();
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        try {
            Object xpBridgeObj = ClassLoader
                    .getSystemClassLoader()
                    .loadClass(XPOSED_BRIDGE)
                    .newInstance();
        } catch (InstantiationException | IllegalAccessException e) { //实测debug跑到这里报异常
            e.printStackTrace();
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //尝试关闭xp的全局开关，亲测可用
    public static boolean tryShutdownXposed() {
        if (isEposedExistByThrow()) {
            Field xpdisabledHooks = null;
            try {
                xpdisabledHooks = ClassLoader.getSystemClassLoader()
                        .loadClass(XPOSED_BRIDGE)
                        .getDeclaredField("disableHooks");
                xpdisabledHooks.setAccessible(true);
                xpdisabledHooks.set(null, Boolean.TRUE);
                return true;
            } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
                e.printStackTrace();
                return false;
            }
        } else return true;
    }

    private final static String TAG = "SecurityUtil";

    public static void xPosedCheck() {
        LogUtils.e(TAG, "检测xposed框架");
        if (!tryShutdownXposed()) {
            ToastUtil.show("请关闭Xposed框架!!!");
            int i = 100;
            int j = 25;
            int k = 4;
            int m = i / (i / j - k);
        }
    }

}
