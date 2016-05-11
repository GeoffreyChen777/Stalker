package com.sorry.stalker.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sorry.stalker.R;

/**
 * Created by sorry on 2016/5/1.
 */
public class MainToolBar extends RelativeLayout {

    private RelativeLayout backgroundLayout;
    private TextView textView;
    public MainToolBar(Context context) {
        this(context, null);
    }

    public MainToolBar(final Context context, AttributeSet attrs) {
        super(context, attrs);
        // 导入布局
        LayoutInflater.from(context).inflate(R.layout.main_toolbar, this, true);
    }
}
