package cn.com.lyk.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import cn.com.lyk.base.AbsWorkService;

/**
 * Created by LeeYK on 2017/7/12.
 */

public final class DaemonEnv {
    private DaemonEnv(){}
    public static final int DEFAULT_WAKE_UP_INTERVAL = 6 * 60 * 1000;
    private static final int MINIMAL_WAKE_UP_INTERVAL = 3 * 60 * 1000;

    static Context sApp;
    static Class<? extends AbsWorkService> sServiceClass;
    private static int sWakeUpInterval = DEFAULT_WAKE_UP_INTERVAL;
    static boolean sInitialized;

    /**
     * @param app Application Context.
     * @param wakeUpInterval 定时唤醒的时间间隔(ms).
     */
    public static void initialize(@NonNull Context app, @NonNull Class<? extends AbsWorkService> serviceClass, @Nullable Integer wakeUpInterval) {
        sApp = app;
        sServiceClass = serviceClass;
        if (wakeUpInterval != null) sWakeUpInterval = wakeUpInterval;
        sInitialized = true;
    }

    static int getWakeUpInterval() {
        return Math.max(sWakeUpInterval, MINIMAL_WAKE_UP_INTERVAL);
    }
}
