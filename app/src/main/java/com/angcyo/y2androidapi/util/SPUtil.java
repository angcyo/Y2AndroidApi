package com.angcyo.y2androidapi.util;

/**
 * Created by angcyo on 15-07-25-025.
 */

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtil {
    static final String SHARED_FILE_NAME = "AppSetting";

    public static SharedPreferences getSP(Context context) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(SHARED_FILE_NAME, Context.MODE_MULTI_PROCESS);
        return sp;
    }

}

