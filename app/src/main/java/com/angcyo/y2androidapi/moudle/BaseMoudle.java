package com.angcyo.y2androidapi.moudle;

import android.content.Context;

/**
 * Created by angcyo on 15-07-26-026.
 */
public abstract class BaseMoudle {
    public abstract Builder with(Context context);

    public static abstract class Builder{
        public abstract Builder get();
        public abstract Builder onNoNetworkListener();
        public abstract Builder onGetSucceedListener();
        public abstract Builder onGetFailedListener();
    }
}
