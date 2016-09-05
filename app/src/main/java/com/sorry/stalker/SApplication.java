package com.sorry.stalker;

import android.app.Application;

import com.sorry.core.AppAction;
import com.sorry.core.AppActionImpl;
import com.sorry.core.UIAction;
import com.sorry.core.UIActionImpl;

/**
 * Created by sorry on 9/5/16.
 */
public class SApplication extends Application {
    AppAction appAction;
    UIAction uiAction;
    @Override
    public void onCreate() {
        super.onCreate();
        appAction = new AppActionImpl();
        uiAction = new UIActionImpl();
    }

    public AppAction getAppAction() {
        return appAction;
    }

    public UIAction getUiAction() {
        return uiAction;
    }
}
