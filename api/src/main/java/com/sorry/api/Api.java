package com.sorry.api;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import okhttp3.Callback;

/**
 * Created by sorry on 8/29/16.
 */
public interface Api {

    public final static String REGISTER = "user.registerByPhone";
    public final static String LOGIN = "user.loginByApp";
    public final static String GET_PINFO = "user.getPersonalInfo";
    public final static String PUSH_PINFO = "user.pushPersonalInfo";
    public final static String PUSH_POST = "user.pushPost";

    public void showToast(Context context, String msg, int aloneTime);

}
