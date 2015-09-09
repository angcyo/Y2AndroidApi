package com.angcyo.y2androidapi.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by angcyo on 15-07-26-026.
 */
public class Util {
    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String str) {
        if (str == null || str.length() == 0)
            return true;
        return false;
    }

    /**
     * 判断网络是否可以用
     *
     * @param context the con
     * @return the boolean
     */
    public static boolean isNetOk(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
            return false;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo == null) {
            return false;
        }
        if (netInfo.isConnected()) {
            return true;
        }
        return false;
    }
}
