package com.sorry.stalker.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.sorry.core.AppAction;
import com.sorry.core.UIAction;
import com.sorry.stalker.SApplication;

/**
 * Created by sorry on 9/5/16.
 */
public class BaseActivity extends Activity {
    // 上下文实例
    public Context context;
    // 应用全局的实例
    public SApplication application;
    // 核心层的AppAction实例
    public AppAction appAction;
    // 核心层的UIAction实例
    public UIAction uiAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        application = (SApplication) this.getApplication();
        appAction = application.getAppAction();
        uiAction = application.getUiAction();

    }
}
