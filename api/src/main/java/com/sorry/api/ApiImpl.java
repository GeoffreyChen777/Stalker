package com.sorry.api;

import android.content.Context;
import android.widget.Toast;
import com.sorry.api.util.DatabaseEngine;
import com.sorry.api.util.HttpEngine;

public class ApiImpl implements Api {

    private HttpEngine httpEngine;
    private DatabaseEngine databaseEngine;

    public ApiImpl(Context context){
        httpEngine = HttpEngine.getInstance();
        databaseEngine = DatabaseEngine.getInstance(context);
    }

    @Override
    public void showToast(Context context, String msg, int aloneTime) {
        Toast.makeText(context, msg, aloneTime).show();
    }
}
