package com.think.myopencart.util;

import android.util.Log;

public class LogUtils {

    public static void LOGD(final String tag, String message) {
        Log.d(tag, message);
    }

    public static void LOGV(final String tag, String message) {
        Log.v(tag, message);
    }

    public static void LOGI(final String tag, String message) {
        Log.i(tag, message);
    }

    public static void LOGW(final String tag, String message) {
        Log.w(tag, message);
    }

    public static void LOGE(final String tag, String message) {
        Log.e(tag, message);
    }
}
