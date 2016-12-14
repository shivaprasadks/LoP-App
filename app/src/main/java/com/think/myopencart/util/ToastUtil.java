package com.think.myopencart.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    public static void showToast(Context context, String message) {
        if (null != message && null != context)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        else if(null==message)
            ToastUtil.showToast(context,"Null");
    }
}
